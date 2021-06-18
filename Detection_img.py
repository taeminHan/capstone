from keras.models import load_model
from PIL import Image
import numpy as np


def predict(f):
    image_w = 64
    image_h = 64
    pixels = image_h * image_w * 3
    X = []
    filenames = []

    img = Image.open(f)
    img = img.convert("RGB")
    img = img.resize((image_w, image_h))
    data = np.asarray(img)
    filenames.append(f)
    X.append(data)
    X = np.array(X)
    model = load_model('capstone_model.h5')

    prediction = model.predict(X)
    np.set_printoptions(formatter={'float': lambda x: "{0:0.3f}".format(x)})
    cnt = 0

    for i in prediction:
        pre_ans = i.argmax()  # 예측 레이블
        pre_ans_str = ''
        if pre_ans == 0:
            pre_ans_str = "코카콜라"
        elif pre_ans == 1:
            pre_ans_str = "칠성사이다"
        elif pre_ans == 2:
            pre_ans_str = "스프라이트"

        if i[0] >= 0.8:
            return pre_ans_str
        if i[1] >= 0.8:
            return pre_ans_str
        if i[2] >= 0.8:
            return pre_ans_str
