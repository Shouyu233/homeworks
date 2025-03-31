import requests
from bs4 import BeautifulSoup

def crawl_tipdm_news():
    # 目标网址（新闻中心）
    url = "http://www.tipdm.com"

    # 1. 发起请求
    # 有些网站可能需要加 headers，否则会被识别为爬虫阻断
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 \
                      (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
    }
    response = requests.get(url, headers=headers, timeout=10)
    # 若网站使用了其他编码，可能需要自行指定
    response.encoding = response.apparent_encoding

    # 2. 解析 HTML
    soup = BeautifulSoup(response.text, "html.parser")

    # 3. 查找新闻列表所在的 HTML 区块
    #    具体的查找方式需要根据实际网页结构而定
    #    下面仅作示例，需要自行查看网页源代码进行适配
    news_list_container = soup.find("ul", class_="newstList")
    # 可能是包含新闻列表的 div
    
    if not news_list_container:
        print("未找到新闻列表所在的 div，请检查页面结构是否有变化。")
        return

    # 4. 在列表容器中获取所有新闻标题和链接
    #    根据实际情况选择合适的标签和属性
    #    假设每条新闻标题在 <li><a>...</a></li> 中
    news_items = news_list_container.find_all("li")
    for item in news_items:
        # 获取 a 标签
        a_tag = item.find("a")
        if a_tag:
            title = a_tag.get_text(strip=True)
            link = a_tag.get("href", "")
            print(f"标题: {title}, 链接: {link}")

if __name__ == "__main__":
    crawl_tipdm_news()
