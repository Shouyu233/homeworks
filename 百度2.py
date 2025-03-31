import requests
import mysql.connector
import json

# 请求URL
url = "https://news.baidu.com/widget?id=LocalNews&loc=0&ajax=json&t=1743384594188"

# 发起请求
response = requests.get(url)
data = response.json()  # 获取返回的 JSON 数据

# 提取新闻数据
news_list = []

# 从返回的 JSON 数据中提取新闻列表
for news in data['data']['LocalNews']['data']['rows']['first'] + data['data']['LocalNews']['data']['rows']['second']:
    title = news['title']
    time = news['time']
    url = news['url']
    
    # 提取新闻的来源信息，这里假设从新闻的 URL 获取该信息
    source = "百度新闻"  # 实际情况下，你可能需要从具体的新闻页面中解析来源
    
    news_list.append((title, source, time, url))

# 打印新闻数据以供检查
for news in news_list:
    print(news)

# 连接到MySQL数据库
db = mysql.connector.connect(
    host="localhost",       # 数据库主机
    user="root",            # 数据库用户名
    password="1026",  # 数据库密码
    database="news_db"      # 数据库名称
)

cursor = db.cursor()

# 创建新闻表（如果不存在的话）
cursor.execute("""
    CREATE TABLE IF NOT EXISTS baidu_news (
        id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255),
        source VARCHAR(255),
        time VARCHAR(50),
        url VARCHAR(255)
    )
""")

# 将新闻数据存储到数据库中
for news in news_list:
    cursor.execute("""
        INSERT INTO baidu_news (title, source, time, url)
        VALUES (%s, %s, %s, %s)
    """, news)

# 提交事务并关闭连接
db.commit()
cursor.close()
db.close()

print("新闻数据已成功存储到数据库")
