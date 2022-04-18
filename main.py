import sys

import cv2
import numpy as np
from PIL import Image

a = cv2.cvtColor(cv2.imread("baboon.jpg"), cv2.COLOR_BGR2GRAY)##pobranie obrazu, i konwercja na skale szarości
b = a#ten sam obraz ale jako wyjsciowy
shape = a.shape

y = shape[0]
x = shape[1]
NMAX = 250 #nie wiem, chyba granice obrazu
MMAX = 250 #nie wiem, chyba granice obrazu
Regmax = 25 #maksymalna ilość regionów
global N
N=0#ilość regionów # w książce odnoszą sie doniej przez referencje wiec chyba chcą mieć ją jako zmienna globalną, nie nie jestem pewnien jak działa
MARRAY =[0 for col in range(31)] #wektor zawierajacy średnie wartości każdego regionu **MARRAY
LABEL = [[0 for col in range(x)] for row in range(y)] # array intow zawierajacy labele // w ksiazce zapisane jako *LABEL

N1 = 0 #punkt od którego zaczynamy
M1 = 0#punkt od którego zaczynamy
N2 = int(y)#punkt w którym koczymy
M2 = int(x)#punkt w którym koczymy

T = 6 #próg // nw czego
global NI
NI=0
global I
I=0 #licznik iteracji// nie ma orginalnie w w programie




def increase():##dopisałem sb do iterowanie I
    global I
    I=I+1
def region_split_merge(a, b, MARRAY, LABEL, N1, M1, N2, M2, T, REGMAX):
    ret = 0
    increase()
    global N
    test = test_homogenity(a, N1, M1, N2, M2, T)
    if test == 0 and N2 - N1 > 1 and M2 - M1 > 1:

        ret1 = region_split_merge(a, b, MARRAY, LABEL, int(N1), int(M1), int(N1 + (N2 - N1) / 2),
                                  int(M1 + (M2 - M1) / 2),  T, REGMAX)
        ret2 = region_split_merge(a, b, MARRAY, LABEL, int(N1 + (N2 - N1) / 2), M1, N2, int(M1 + (M2 - M1) / 2),  T,
                                  REGMAX)
        ret3 = region_split_merge(a, b, MARRAY, LABEL, N1, int(M1 + (M2 - M1) / 2), int(N1 + (N2 - N1) / 2), M2,  T,
                                  REGMAX)
        ret4 = region_split_merge(a, b, MARRAY, LABEL, int(N1 + (N2 - N1) / 2), int(M1 + (M2 - M1) / 2), N2, M2,  T,
                                  REGMAX)
        if ret1 == -1 or ret2 == -1 or ret3 == -1 or ret4 == -1:
            ret = -1
    else:
        print("N:",N,"iteracje",I)
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
        if N > 1: rmerge(a, b, MARRAY, LABEL, N1, M1, N2, M2, T)
    global NI
    if I==100+NI:

        NI=NI+100
        cv2.imshow('ImageWindow', b)
        cv2.waitKey(0)
    return ret


def rmerge(a, b, MARRAY, LABEL, N1, M1, N2, M2, T):
    sum = 0
    count = 0
    y=0
    x=0
    xd=0
    xu=0
    yd=0
    yu=0
    cmin=0
    c=0
    global N

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


def main():
    print(LABEL)
    region_split_merge(a, b, MARRAY, LABEL, N1, M1, N2, M2, T, Regmax)
    print(LABEL)

    cv2.imshow('ImageWindow',b)
    cv2.waitKey(0)

main()
