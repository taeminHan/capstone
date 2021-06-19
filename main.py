# from Detection_img import predict
from flask import jsonify
import flask
import werkzeug
import time

app = flask.Flask(__name__)


@app.route('/img', methods=['GET', 'POST'])
def handle_request():
    files_ids = list(flask.request.files)
    print("\nNumber of Received Images : ", len(files_ids))
    image_num = 1

    for file_id in files_ids:
        print("\nSaving Image ", str(image_num), "/", len(files_ids))
        imagefile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        print("Image Filename : " + imagefile.filename)
        timestr = time.strftime("%Y%m%d-%H%M%S")
        imagefile.save(timestr + '_' + filename)
        image_num = image_num + 1
        # predict(timestr + '_' + filename)
    return jsonify({'object':'코카콜라'})


app.run(host="0.0.0.0", port=5000, debug=True)
