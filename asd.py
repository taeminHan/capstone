import requests
from bs4 import BeautifulSoup
import pymysql
import openpyxl
import pandas as pd
import urllib.request
import json

mydb = pymysql.connect(
        user='root',
        passwd='sulivan',
        host='localhost',
        db='sulivan',
        charset='utf8'
    )

cur = mydb.cursor()
"""cur.execute("truncate event;")"""
"""cur.execute("truncate nutritionfacts")"""

url = 'https://pyony.com/search/?event_type=&category=1&item=&sort=&q='
url2 = "https://www.fatsecret.kr/%EC%B9%BC%EB%A1%9C%EB%A6%AC-%EC%98%81%EC%96%91%EC%86%8C/search?q=%ec%9d%8c%eb%a3%8c%ec%88%98&pg="
insert_query = "insert into event (shopname, beveragename, eventname, price) values (%s, %s, %s, %s);"
insert_query2 = "insert into nutritionfacts (foodname, kcal, carbohydrate, protein, Fat) values (%s, %s, %s, %s, %s);"
"""for page in range(0, 36):
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
"""
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

sql = 'select * from nutritionfacts where foodname="{}"'.format('사이다')
sql2 = 'select * from event where beveragename like "%사이다%"'
nut = cur.execute(sql)
row2 = cur.fetchall()
print(row2)
price = cur.execute(sql2)
row = cur.fetchall()

print(row)