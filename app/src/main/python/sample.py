import librosa as lr
from os.path import dirname, join
from android.os import Environment
import numpy as np
def test():
    # print(file)
    d = str(Environment.getExternalStorageDirectory())
    #heart = join(dirname(__file__), "heart.wav")
    heart = join(d, "heart.wav")
    (signal, rate)= lr.load(heart, sr=None)
    return  np.array(list(signal), dtype=np.float)

def locate():
    d = str(Environment.getExternalStorageDirectory())
    heart = join(d, "heart.wav")
    return heart
