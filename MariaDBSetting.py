import csv
import requests
from bs4 import BeautifulSoup
import pymysql
import openpyxl
import pandas as pd
import urllib.request
import json

mydb = pymysql.connect(
        user='root',
        passwd='smtown05',
        host='localhost',
        db='sulivan',
        charset='utf8'
    )

cur = mydb.cursor()
cur.execute("truncate event;")
cur.execute("truncate nutritionfacts")


url = 'https://pyony.com/search/?event_type=&category=1&item=&sort=&q='
url2 = "https://www.fatsecret.kr/%EC%B9%BC%EB%A1%9C%EB%A6%AC-%EC%98%81%EC%96%91%EC%86%8C/search?q=%EC%9D%8C%EB%A3%8C&pg="
rurl = "https://www.fatsecret.kr/%EC%B9%BC%EB%A1%9C%EB%A6%AC-%EC%98%81%EC%96%91%EC%86%8C/search?q=%EC%97%90%EB%84%88%EC%A7%80+%EC%9D%8C%EB%A3%8C&pg="
insert_query = "insert into event (shopname, beveragename, eventname, price) values (%s, %s, %s, %s);"
insert_query2 = "insert into nutritionfacts (foodname, kcal, carbohydrate, protein, Fat) values (%s, %s, %s, %s, %s);"
insert_query3 = "insert into beverage (beveragename, price, convenience) values (%s, %s, %s);"
for page in range(0, 10):
    response = requests.get(rurl+str(page))
    for i in range(0, 10):
        if response.status_code == 200:
            html = response.text
            soup = BeautifulSoup(html, 'html.parser')
            foodname = soup.select('.prominent')[i].get_text()
            rurl2 = soup.select(".prominent")[i]['href']
            response2 = requests.get("https://www.fatsecret.kr"+rurl2)
            html = response2.text
            soup2 = BeautifulSoup(html, 'html.parser')
            kcal = soup2.select('.nutrient.black.right.tRight')[0].get_text()
            carbohydrate = soup2.select('.nutrient.black.right.tRight')[1].get_text()
            protein = soup2.select('.nutrient.black.right.tRight')[2].get_text()
            Fat = soup2.select('.nutrient.black.right.tRight')[3].get_text()
            cur.execute(insert_query2, (foodname, kcal, carbohydrate, protein, Fat))
            mydb.commit()
for page in range(0, 100):
    response = requests.get(url2+str(page))
    for i in range(0, 10):
        if response.status_code == 200:
            html = response.text
            soup = BeautifulSoup(html, 'html.parser')
            foodname = soup.select('.prominent')[i].get_text()
            url3 = soup.select(".prominent")[i]['href']
            response2 = requests.get("https://www.fatsecret.kr"+url3)
            html = response2.text
            soup2 = BeautifulSoup(html, 'html.parser')
            kcal = soup2.select('.nutrient.black.right.tRight')[0].get_text()
            carbohydrate = soup2.select('.nutrient.black.right.tRight')[1].get_text()
            protein = soup2.select('.nutrient.black.right.tRight')[2].get_text()
            Fat = soup2.select('.nutrient.black.right.tRight')[3].get_text()
            cur.execute(insert_query2, (foodname, kcal, carbohydrate, protein, Fat))
            mydb.commit()

for page in range(0, 80):
    response = requests.get(url+str(page))

    if response.status_code == 200:
        html = response.text
        soup2 = BeautifulSoup(html, 'html.parser')
        keys = soup2.select(".col-md-6")
        for key in keys:
            shopname = key.select_one('small').get_text()
            beveragename = key.select_one("strong").get_text()
            price = key.select_one('a > div > div.card-body.px-2.py-2 > div:nth-child(2)').contents[6].get_text().strip()
            eventname = ''
            if key.select_one('small').get_text() == 'MINISTOP(미니스톱)':
                eventname = key.select_one(".badge.bg-ministop.text-white").get_text()
            elif key.select_one('small').get_text() == 'GS25(지에스25)':
                eventname = key.select_one(".badge.bg-gs25.text-white").get_text()
            elif key.select_one('small').get_text() == 'CU(씨유)':
                eventname = key.select_one(".badge.bg-cu.text-white").get_text()
            elif key.select_one('small').get_text() == 'EMART24(이마트24)':
                eventname = key.select_one(".badge.bg-emart24.text-white").get_text()
            elif key.select_one('small').get_text() == '7-ELEVEN(세븐일레븐)':
                eventname = key.select_one(".badge.bg-seven.text-white").get_text()
            cur.execute(insert_query, (shopname, beveragename, eventname, price))
            mydb.commit()
    else:
        print(response.status_code)

df1 = pd.read_csv("한국소비자원 생필품 및 서비스 가격 정보_20210827.csv", encoding='euc-kr', index_col=0)
df1.drop(['Unnamed: 1'], axis = 1, inplace=True)
cu = df1.loc[df1['판매업소'] == 'CU(본사)']
gs25 = df1.loc[df1['판매업소'] == 'GS25(본사)']
seveneleven = df1.loc[df1['판매업소'] == '세븐일레븐(본사)']
for index, row in cu.iterrows():
    price = row[0]
    convenience = row[1]
    beveragename = index
    cur.execute(insert_query3, (beveragename, price, convenience))
    mydb.commit()
for index, row in gs25.iterrows():
    price = row[0]
    convenience = row[1]
    beveragename = index
    cur.execute(insert_query3, (beveragename, price, convenience))
    mydb.commit()
for index, row in seveneleven.iterrows():
    price = row[0]
    convenience = row[1]
    beveragename = index
    cur.execute(insert_query3, (beveragename, price, convenience))
    mydb.commit()
print("Done")