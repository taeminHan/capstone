from keras.models import load_model
from PIL import Image
import numpy as np
import tensorflow as tf
from tensorflow.keras import datasets, layers, models

gpus = tf.config.experimental.list_physical_devices('GPU')

if gpus:
    try:
        # Currently, memory growth needs to be the same across GPUs
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
    except RuntimeError as e:
        # Memory growth must be set before GPUs have been initialized
        print(e)


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

    print(prediction)
    np.set_printoptions(formatter={'float': lambda x: "{0:0.3f}".format(x)})
    cnt = 0

    for i in prediction:
        # ["코카콜라", "레드불", "밀키스", "칠성사이다", "환타"]
        pre_ans = i.argmax()  # 예측 레이블
        pre_ans_str = ''
        if pre_ans == 0:
            pre_ans_str = "코카콜라"
        elif pre_ans == 1:
            pre_ans_str = "레드불"
        elif pre_ans == 2:
            pre_ans_str = "밀키스"
        elif pre_ans == 3:
            pre_ans_str = "칠성사이다"

        if i[0] >= 0.8:
            return pre_ans_str
        if i[1] >= 0.8:
            return pre_ans_str
        if i[2] >= 0.8:
            return pre_ans_str
        if i[3] >= 0.8:
            return pre_ans_str
