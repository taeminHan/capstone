from Detection_img import predict
from flask import jsonify, request
import flask
import werkzeug
import time
import urllib.request
import xml.dom.minidom
import pymysql
import xmltodict
import json
import requests
import os
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


@app.route('/imgInformation', methods=['GET', 'POST'])
def imageInformation():
    files_ids = list(flask.request.files)
    image_num = 1
    for file_id in files_ids:
        imagefile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        timestr = time.strftime("%Y%m%d-%H%M%S")
        imagefile.save(timestr + '_' + filename)
        image_num = image_num + 1
        name = predict(timestr + '_' + filename)
        sql = 'select * from nutritionfacts where foodname like "%{}%"'.format(name)
        sql2 = 'select * from beverage where beveragename like "%{}%"'.format(name)
        sql3 = 'select * from event where beveragename like "%{}%"'.format(name)
        mycursor.execute(sql)
        nutrition = mycursor.fetchall()
        mycursor.execute(sql2)
        price = mycursor.fetchall()
        mycursor.execute(sql3)
        event = mycursor.fetchall()
        if len(event) > 1:
            event = event[0][2] + "행사상품입니다."
        else:
            event = "행사상품이아닙니다."

        os.remove(timestr + '_' + filename)
    return jsonify({'object': name, 'price': price[0][1]+"원", 'nutrition_facts': nutrition[0][1:], 'event':event})


@app.route('/login', methods=['GET', 'POST'])
def login():
    post_result = json.loads()
    sql = "INSERT INTO member (email, creditcard, mmyy, pwd) VALUES (%s, %s, %s, %s)"
    val = (post_result['name'], post_result['id'])
    mycursor.execute(sql, val)
    mydb.commit()

    return {post_result}
@app.route('/text', methods=['GET', 'POST'])
def text():
    texts = request.form
    print(texts['beveragename'])

    return jsonify("")
@app.route('/imgSearch', methods=['GET', 'POST'])
def imgSearch():
    files_ids = list(flask.request.files)
    image_num = 1
    for file_id in files_ids:
        imagefile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        timestr = time.strftime("%Y%m%d-%H%M%S")
        imagefile.save(timestr + '_' + filename)
        image_num = image_num + 1
        place = ""

        os.remove(timestr + '_' + filename)
    return jsonify({'place': place})

app.run(host="0.0.0.0", port=5000, debug=True)
