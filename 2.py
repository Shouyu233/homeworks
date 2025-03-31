import requests
from bs4 import BeautifulSoup

# 发送 HTTP 请求获取网页内容
url = 'http://www.tipdm.com/'
response = requests.get(url)

# 检查请求是否成功
if response.status_code == 200:
    # 设置响应内容的编码格式
    response.encoding = 'utf-8'
    
    # 使用 BeautifulSoup 解析 HTML 内容
    soup = BeautifulSoup(response.text, 'html.parser')
    
    # 查找新闻中心的标题内容
    news_titles = soup.select('newstList')  # 替换为实际的 CSS 选择器
    
    # 打印爬取到的标题
    for index, title in enumerate(news_titles, start=1):
        print(f"{index}. {title.text.strip()}")
else:
    print(f"请求失败，状态码：{response.status_code}")
