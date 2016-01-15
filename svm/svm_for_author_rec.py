from sklearn.multiclass import OneVsRestClassifier
from sklearn.multiclass import OneVsOneClassifier
#import matplotlib.pyplot as plt
import scipy.io as sio
from sklearn.svm import LinearSVC
import numpy as np

def run():
    N = 5; #number of classes
    mat_contents = sio.loadmat('octave_X.mat')
    X = mat_contents['Norm_X']
    mat_contents = sio.loadmat('octave_Y.mat')
    Y = mat_contents['Y']
    mat_contents = sio.loadmat('octave_XT.mat')
    XT = mat_contents['Norm_XT']
    mat_contents = sio.loadmat('octave_YT.mat')
    YT = mat_contents['Y_Test']

    classifier = OneVsRestClassifier(LinearSVC(random_state=0)).fit(X, Y)
    prediction = classifier.predict(XT)

    print("Accuracy : %f" %classifier.score(XT,YT))
    confusion_matrix = np.zeros((N, N))
    for (ind,r) in enumerate(prediction):
        confusion_matrix[int(YT[ind][0])][int(r)] +=1

    print("Confusion Matrix")
    for line in confusion_matrix:
        print(line)

if __name__ == "__main__":
    run()
