import requests
from bs4 import BeautifulSoup
import pymysql
from urllib.parse import urljoin
import random
import time
import logging
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    'host': '127.0.0.1',
    'user': 'root',
    'password': '1026',
    'database': 'news_db',
    'charset': 'utf8mb4'  # 重要：确保支持中文
}

# 反爬配置
HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
    'Accept-Language': 'zh-CN,zh;q=0.9',
    'Referer': 'https://www.baidu.com/',
    # 'Cookie': 'BAIDUID=D78FDC75C472B119A53B5177D15905AC; BDUSS=D78FDC75C472B119A53B5177D15905AC'  # 通过浏览器获取
}

# 日志配置
logging.basicConfig(level=logging.INFO)

def connect_db():
    """连接数据库"""
    return pymysql.connect(**DB_CONFIG)

def create_table(conn):
    """创建数据表"""
    with conn.cursor() as cursor:
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS news (
            id INT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) CHARACTER SET utf8mb4,
            source VARCHAR(500),
            content TEXT CHARACTER SET utf8mb4,
            publish_time DATETIME
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        ''')
        conn.commit()

def save_to_db(conn, title, source, content, publish_time):
    """保存数据到数据库"""
    try:
        with conn.cursor() as cursor:
            cursor.execute('''
            INSERT INTO news (title, source, content, publish_time)
            VALUES (%s, %s, %s, %s)
            ''', (title, source, content, publish_time if publish_time else None))
            conn.commit()
        logging.info(f"Inserted: {title[:30]}...")
    except Exception as e:
        logging.error(f"Error saving data to DB: {e}")

def get_news_details(url):
    """获取单个新闻的详细内容"""
    try:
        session = requests.Session()
        session.headers.update(HEADERS)
        time.sleep(random.uniform(1, 3))

        response = session.get(url, timeout=10)
        if response.status_code != 200:
            logging.warning(f"Failed to fetch {url}, status code: {response.status_code}")
            return None

        # 强制设置编码为 utf-8
        response.encoding = 'utf-8'

        soup = BeautifulSoup(response.text, 'lxml')

        # 获取内容和发布时间
        content = ""
        publish_time = None

        # 优化内容获取
        for tag in ['article', 'div', 'section']:
            article = soup.find(tag, class_=lambda x: x and 'article' in x.lower())
            if article:
                content = article.get_text(separator='\n', strip=True)
                break
            article = soup.find(tag, id=lambda x: x and 'article' in x.lower())
            if article:
                content = article.get_text(separator='\n', strip=True)
                break

        # 如果没有找到内容，尝试从页面提取
        if not content:
            content = soup.get_text(separator='\n', strip=True)

        # 提取发布时间
        time_tag = soup.find('meta', property='article:published_time')
        if time_tag and time_tag.get('content'):
            try:
                publish_time = datetime.strptime(time_tag['content'], '%Y-%m-%dT%H:%M:%SZ')
            except Exception as e:
                logging.warning(f"Error parsing time: {e}")
                publish_time = None

        if not publish_time:
            # 尝试从其他标签提取时间
            time_tag = soup.find('time')
            if time_tag and time_tag.get('datetime'):
                try:
                    publish_time = datetime.strptime(time_tag['datetime'], '%Y-%m-%dT%H:%M:%SZ')
                except Exception as e:
                    logging.warning(f"Error parsing time from <time>: {e}")
                    publish_time = None

        # 如果仍然没有时间，置为 None 或使用其他标记
        if not publish_time:
            publish_time = None

        return {
            'content': content,
            'publish_time': publish_time
        }

    except Exception as e:
        logging.error(f"Error fetching details from {url}: {str(e)}")
        return None

def get_news():
    """获取新闻数据"""
    url = "https://news.baidu.com"

    try:
        session = requests.Session()
        session.headers.update(HEADERS)

        time.sleep(random.uniform(1, 3))

        response = session.get(url, timeout=10)
        logging.info(f"HTTP状态码: {response.status_code}")
        logging.info(f"响应内容长度: {len(response.text)} 字符")

        if response.status_code != 200 or "验证" in response.text:
            logging.warning("!! 触发反爬机制 !! 建议操作：")
            logging.warning("1. 手动访问 https://news.baidu.com 完成验证")
            logging.warning("2. 更新请求头中的Cookie值")
            return []

        soup = BeautifulSoup(response.text, 'lxml')

        news_data = []

        left_items = soup.select('div#left-col-wrapper div.mod-tab-pane.active div.hotnews ul li strong a')
        right_items = soup.select('div#left-col-wrapper div.mod-tab-pane.active ul.ulist.focuslistnews li a')

        all_items = list(left_items + right_items)

        seen = set()
        for item in all_items:
            try:
                title = item.get_text().strip()
                href = urljoin(url, item.get('href'))

                if len(title) > 3 and title not in seen:
                    seen.add(title)
                    news_data.append((title, href))
            except Exception as e:
                logging.warning(f"解析异常：{str(e)}")
                continue

        logging.info(f"初步抓取数量：{len(all_items)}")
        logging.info(f"有效新闻数量：{len(news_data)}")
        if news_data:
            logging.info("示例新闻：")
            for title, source in news_data[:3]:
                logging.info(f"  - {title[:30]}... ({source[:50]}...)")

        return news_data

    except Exception as e:
        logging.error(f"抓取失败：{str(e)}")
        return []

def main():
    conn = connect_db()
    create_table(conn)

    try:
        news_data = get_news()

        if not news_data:
            logging.warning("无有效数据，建议检查：")
            logging.warning("1. 是否已更新Cookie值")
            logging.warning("2. 使用浏览器开发者工具检查页面结构")
            return

        batch_size = 5
        for i in range(0, len(news_data), batch_size):
            batch = news_data[i:i+batch_size]
            for title, source in batch:
                details = get_news_details(source)
                if details:
                    save_to_db(conn, title, source, details['content'], details['publish_time'])
            logging.info(f"已插入 {min(i+batch_size, len(news_data))}/{len(news_data)} 条数据")

        logging.info("数据保存完成！")

    finally:
        conn.close()

if __name__ == "__main__":
    main()
