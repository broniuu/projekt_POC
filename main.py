import cv2
import numpy as np
import sys
import matplotlib.pyplot as plt
from cffi.backend_ctypes import xrange
from numpy import zeros
import sys
sys.setrecursionlimit(3500)
NMAX=10
MMAX=10
a = cv2.cvtColor(cv2.imread("baboon.jpg"), cv2.COLOR_BGR2GRAY)
b = cv2.cvtColor(cv2.imread("baboon.jpg"), cv2.COLOR_BGR2GRAY)
def rmerge(a, b, MARRAY, LABEL, N1, M1, N2, M2, N, T):
    sum = 0
    count = 0

    if N1 - 1 >= 0:
        xd = N1 - 1
    else:
        xd = 0
    if N2 + 1 < NMAX:
        xu = N2 + 1
    else:
        xu = N2
    if M1 - 1 >= 0:
        yd = M1 - 1
    else:
        yd = 0
    if M2 + 1 < MMAX:
        yu = M2 + 1
    else:
        yu = M2
    cmin = 255
    if M1 - 1 >= 0:
        y = M1 - 1
        for x in range(xd, xu):
            if b[y][x] != 0:
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if (c < cmin):
                    cmin = c
                    mergingLabel = LABEL[y][x];

    if M2 + 1 < MMAX:
        y = M2 + 1
        for x in range(xd, xu):
            if (b[y][x] != 0):
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if c < cmin:
                    cmin = c
                    mergingLabel = LABEL[y][x];
    if N1 - 1 >= 0:
        x = N1 - 1
        for x in range(yd, yu):
            if b[y][x] != 0:
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if c < cmin:
                    cmin = c
                    mergingLabel = LABEL[y][x];
    pass

    if N2 + 1 < NMAX:
        x = N2 + 1
        for x in range(yd, yu):
            if (b[y][x] != 0):
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if (c < cmin):
                    cmin = c
                    mergingLabel = LABEL[y][x];
    if cmin < T:
        sum = 0;
        count = 0
        for y in range(0, MMAX):
            for x in range(0, NMAX):
                if LABEL[y][x] == N or LABEL[y][x] == mergingLabel:
                    sum = sum + a[y][x];
                    count = count + 1

        if count != 0:
            for y in range(0, MMAX):
                for x in range(0, NMAX):
                    if LABEL[y][x] == N or LABEL[x][y] == mergingLabel:
                        b[y][x] = sum
                        LABEL[y][x] = mergingLabel
        MARRAY[mergingLabel] = sum;
        N = N - 1

    pass


def test_homogenity(a, N1, M1, N2, M2, T):
    max = 0;
    min = 22
    i = 0;
    j = 0

    for i in range(N1, N2):
        for j in range(M1, M2):
            if a[i][j] < min: min = a[i][j]
            if a[i][j] > max: max = a[i][j]
    if abs(max - min) < T:
        return (i)
    else:
        return (0)
    pass


def region_split_merge(a, b, MARRAY, LABEL, N1, M1, N2, M2, N, T, REGMAX):
    ret = 0
    test = test_homogenity(a, N1, M1, N2, M2, T)
    ret1 = region_split_merge(a, b, MARRAY, LABEL, int(N1),int(M1), int(N1 + (N2 - N1) / 2), int(M1 + (M2 - M1) / 2), N, T, REGMAX)
    ret2 = region_split_merge(a, b, MARRAY, LABEL, int(N1 + N2(-N1) / 2), M1, N2, int(M1 + (M2 - M1) / 2), N, T, REGMAX)
    ret3 = region_split_merge(a, b, MARRAY, LABEL, N1, int(M1 + (M2 / M1) / 2), int( N1 + (N2 - N1) / 2), M2, N, T, REGMAX)
    ret4 = region_split_merge(a, b, MARRAY, LABEL, int(N1 + (N2 - N1) / 2), int(M1 + (M2 - M1) / 2), N2, M2, N, T)
    if ret1 == -1 or ret2 == -1 or ret3 == -1 or ret4 == -1:
        ret = -1
    else:
        sum = 0
        N = N + 1
        if N > REGMAX: return -92
        for j in range(M1, M2):
            for i in range(N1, N2):
                sum = sum + a[i][j]
                LABEL[j][i] = N
        sum = sum / ((N2 - N1) * (M2 - M1))
        for j in range(M1, M2):
            for i in range(N1, N2):
                b[i][j] = sum
        MARRAY[N] = sum
        if N > 1: rmerge(a, b, MARRAY, LABEL, N1, M1, N2, M2, N, T)
    return ret


def main():

    shape = a.shape
    y = shape[0]
    x = shape[1]
    MARRAY = [1, 2, 3]
    LABEL = [[0 for col in range(x)] for row in range(y)]
    N1 = 0
    M1 = 0
    N2 = int(y)
    M2 = int(x)
    N = 0
    T = 20
    Regmax = 32
    region_split_merge(a, b, MARRAY, LABEL, N1, M1, N2, M2, N, T,Regmax)
    cv2.imshow('ImageWindow', a)
    cv2.waitKey(0)
    cv2.imshow(b)
    cv2.waitKey(0)

main()