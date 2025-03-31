import requests
import json
import os
from urllib.parse import urlparse

# 请求URL
url = "https://www.ptpress.com.cn/recommendBook/getRecommendBookListForPortal?bookTagId=0d1ca23e-f8a9-49d0-900a-00cbdb6e87db"

# 发起请求
response = requests.get(url)
data = response.json()  # 获取返回的 JSON 数据

# 提取书籍数据
books_list = []

# 从返回的 JSON 数据中提取书籍列表
for book in data['data']:
    pic_path = book['picPath']
    book_name = book['bookName']
    book_id = book['bookId']
    
    books_list.append({
        "pic_path": pic_path,
        "book_name": book_name,
        "book_id": book_id
    })

# 打印书籍数据以供检查
for book in books_list:
    print(book)

# 指定存储目录
target_dir = r"C:\Users\ROG\Desktop\作业s\爬"  # 使用原始字符串避免转义问题

# 检查目录是否存在，如果不存在则创建
if not os.path.exists(target_dir):
    os.makedirs(target_dir)
    print(f"目录 {target_dir} 已创建")

# 指定文件路径
file_path = os.path.join(target_dir, "books_data.json")

# 将书籍数据存储到本地硬盘
with open(file_path, "w", encoding="utf-8") as file:
    json.dump(books_list, file, ensure_ascii=False, indent=4)

print(f"书籍数据已成功存储到本地文件: {file_path}")

# 下载并保存封面图片
images_dir = os.path.join(target_dir, "images")
if not os.path.exists(images_dir):
    os.makedirs(images_dir)
    print(f"图片目录 {images_dir} 已创建")

for book in books_list:
    pic_url = book['pic_path']
    # 从URL中提取文件名
    file_name = os.path.basename(urlparse(pic_url).path)
    image_path = os.path.join(images_dir, file_name)
    
    # 下载图片
    try:
        image_response = requests.get(pic_url)
        if image_response.status_code == 200:
            with open(image_path, "wb") as image_file:
                image_file.write(image_response.content)
            print(f"图片已保存: {image_path}")
        else:
            print(f"无法下载图片: {pic_url}, 状态码: {image_response.status_code}")
    except Exception as e:
        print(f"下载图片时出错: {pic_url}, 错误: {e}")

print("所有操作完成")