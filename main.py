from Detection_img import predict
from flask import jsonify
import flask
import werkzeug
import time
import urllib.request
import xml.dom.minidom
import pymysql
import xmltodict
import json
import requests
from bs4 import BeautifulSoup

mydb = pymysql.connect(
        user='root',
        passwd='sulivan',
        host='localhost',
        db='sulivan',
        charset='utf8'
    )
mycursor = mydb.cursor()
app = flask.Flask(__name__)


@app.route('/img', methods=['GET', 'POST'])
def handle_request():
    files_ids = list(flask.request.files)
    image_num = 1
    for file_id in files_ids:
        imagefile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        timestr = time.strftime("%Y%m%d-%H%M%S")
        imagefile.save(timestr + '_' + filename)
        image_num = image_num + 1
        print(timestr + '_' + filename)
        time.sleep(3)
        name = predict(timestr + '_' + filename)
        print(name)
        sql = 'select * from nutritionfacts where foodname like "%{}%"'.format('코카콜라')
        sql2 = 'select * from beverage where beveragename like "%{}%"'.format('코카콜라')
        sql3 = 'select * from beverage where beveragename like "%{}%"'.format('코카콜라')
        mycursor.execute(sql)
        nutrition = mycursor.fetchall()
        mycursor.execute(sql2)
        price = mycursor.fetchall()
        name2 = "코카콜라"


    return jsonify({'object': name2, 'price': price, 'nutrition_facts': nutrition})

@app.route('/login', methods=['GET', 'POST'])
def login():
    post_result = json.loads()
    sql = "INSERT INTO member (email, creditcard, mmyy, pwd) VALUES (%s, %s, %s, %s)"
    val = (post_result['name'], post_result['id'])
    mycursor.execute(sql, val)
    mydb.commit()

    return {post_result}
app.run(host="0.0.0.0", port=5000, debug=True)
