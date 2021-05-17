from flask import Flask
from flask_restful import reqparse, abort, Api, Resource

# Flask 인스턴스 생성
app = Flask(__name__)
api = Api(app)

# 할일 정의
Todos = {
    'todo1': {'task': 'Make Money'},
    'todo2': {'task': 'Play PS4'},
    'todo3': {'task': 'Study!'},
}

# 예외 처리
def abort_if_todo_doesnt_exist(todo_id):
    if todo_id not in Todos:
        abort(404, message="Todo {} doesn't exist".format(todo_id))

parser = reqparse.RequestParser()
parser.add_argument('task')

# 할일(Todo)
# Get, Delete, Put 정의
class Todo(Resource):
    def get(self, todo_id):
        abort_if_todo_doesnt_exist(todo_id)
        return Todos[todo_id]

    def delete(self, todo_id):
        abort_if_todo_doesnt_exist(todo_id)
        del Todos[todo_id]
        return '', 204

    def put(self, todo_id):
        args = parser.parse_args()
        task = {'task': args['task']}
        Todos[todo_id] = task
        return task, 201

# 할일 리스트(Todos)
# Get, POST 정의
class TodoList(Resource):
    def get(self):
        return Todos

    def post(self):
        args = parser.parse_args()
        todo_id = 'todo%d' % (len(Todos) + 1)
        Todos[todo_id] = {'task': args['task']}
        return Todos[todo_id], 201


##
## URL Router에 맵핑한다.(Rest URL정의)
##
api.add_resource(TodoList, '/todos/')
api.add_resource(Todo, '/todos/<string:todo_id>')

# 서버 실행
if __name__ == '__main__':
    app.run(host="http://13.125.224.42", port=5000, debug=True)

