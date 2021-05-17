from flask import Flask
from flask_restful import Resource, Api, reqparse, abort

app = Flask(__name__)
api = Api(app)
Todos = {
    'todo1': {"task": "exercise"},
    'todo2': {'task': "eat delivery food"},
    'todo3': {'task': 'watch movie'}
}

class TodoList(Resource):
    def get(self):
        return Todos

api.add_resource(TodoList, '/todos/')

if __name__ == '__main__':
    app.run(host="192.168.1.103", port=6000, debug=True)
