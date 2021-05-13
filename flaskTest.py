from flask import Flask

app = Flask(__name__)


@app.route('/', methods=['GET'])
def home():
    return 'hwan, dong, tae'


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=False)
