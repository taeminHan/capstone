from flask import Flask

app = Flask(__name__)


@app.route('/')
def home():
    return 'hwan, dong, tae'


if __name__ == '__main__':
    app.run(debug=True)
