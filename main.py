import cv2

global a
global b
a = cv2.cvtColor(cv2.imread("baboon.jpg"), cv2.COLOR_BGR2GRAY)  ##pobranie obrazu, i konwercja na skale szarości
b = cv2.cvtColor(cv2.imread("baboon.jpg"), cv2.COLOR_BGR2GRAY)  # ten sam obraz ale jako wyjsciowy
shape = a.shape

y = shape[0]
x = shape[1]
NMAX = 512  # nie wiem, chyba granice obrazu
MMAX = 512  # nie wiem, chyba granice obrazu
Regmax = 20000  # maksymalna ilość regionów
global N
N = 0  # ilość regionów # w książce odnoszą sie doniej przez referencje wiec chyba chcą mieć ją jako zmienna
# globalną, nie nie jestem pewnien jak działa
global MARRAY
MARRAY = [0 for col in range(Regmax + 1)]  # wektor zawierajacy średnie wartości każdego regionu **MARRAY
global LABEL
LABEL = [[0 for col in range(x)] for row in
         range(y)]  # array intow zawierajacy labele // w ksiazce zapisane jako *LABEL

N1 = 0  # punkt od którego zaczynamy
M1 = 0  # punkt od którego zaczynamy
N2 = NMAX  # punkt w którym koczymy
M2 = MMAX  # punkt w którym koczymy

T = 90  # próg
global NI
NI = 0
global I
I = 0  # licznik iteracji// nie ma orginalnie w w programie


def increase():  ##dopisałem sb do iterowanie I
    global I
    I = I + 1


def region_split_merge(N1, M1, N2, M2, T, REGMAX):
    global a, b, MARRAY, LABEL
    j = 0
    i = 0
    test = 0
    ret = 0
    ret1 = 0
    ret2 = 0
    ret3 = 0
    ret4 = 0
    increase()
    global N
    global NI
    if I == 3000 + NI:
        NI = NI + 3000
        cv2.imshow('ImageWindow', b)
        cv2.waitKey(0)

    test = test_homogenity(N1, M1, N2, M2, T)
    if test == 0 and (N2 - N1) > 1 and (M2 - M1) > 1:
        ret1 = region_split_merge(int(N1), int(M1), int(N1 + (N2 - N1) / 2),
                                  int(M1 + (M2 - M1) / 2), T, REGMAX)
        ret2 = region_split_merge(int(N1 + (N2 - N1) / 2), M1, N2, int(M1 + (M2 - M1) / 2), T,
                                  REGMAX)
        ret3 = region_split_merge(N1, int(M1 + (M2 - M1) / 2), int(N1 + (N2 - N1) / 2), M2, T,
                                  REGMAX)
        ret4 = region_split_merge(int(N1 + (N2 - N1) / 2), int(M1 + (M2 - M1) / 2), N2, M2, T,
                                  REGMAX)
        if ret1 == -1 or ret2 == -1 or ret3 == -1 or ret4 == -1:
            ret = -1
    else:
        sum = 0
        N = N + 1
        print("N:", N, "iteracje", I)
        if N > REGMAX: return -92
        for j in range(M1, M2):
            for i in range(N1, N2):
                sum = sum + a[j][i]
                LABEL[j][i] = N
        sum = sum / ((N2 - N1) * (M2 - M1))
        for j in range(M1, M2):
            for i in range(N1, N2):
                b[j][i] = sum
        MARRAY[N] = sum
        if N > 1:
            rmerge(LABEL, N1, M1, N2, M2, T)
    return ret


def rmerge(LABEL, N1, M1, N2, M2, T):
    global a, b
    global MARRAY
    global NI


    mergingLabel = 0
    sum = 0
    count = 0
    y = 0
    x = 0
    xd = 0
    xu = 0
    yd = 0
    yu = 0
    cmin = 0
    c = 0
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
                    mergingLabel = LABEL[y][x]

    if M2 + 1 < MMAX:
        y = M2 + 1
        for x in range(xd, xu):
            if b[y][x] != 0:
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if c < cmin:
                    cmin = c
                    mergingLabel = LABEL[y][x]
    if N1 - 1 >= 0:
        x = N1 - 1
        for x in range(yd, yu):
            if b[y][x] != 0:
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if c < cmin:
                    cmin = c
                    mergingLabel = LABEL[y][x]

    if N2 + 1 < NMAX:
        x = N2 + 1
        for x in range(yd, yu):
            if b[y][x] != 0:
                c = int(abs(float(b[y][x]) - float(MARRAY[N])))
                if (c < cmin):
                    cmin = c
                    mergingLabel = LABEL[y][x]
    if cmin < T:
        sum = 0
        count = 0
        for y in range(0, MMAX):
            for x in range(0, NMAX):
                if LABEL[y][x] == N or LABEL[y][x] == mergingLabel:
                    sum = sum + a[y][x]
                    count = count + 1

        if count != 0:
            sum = sum / count
            for y in range(0, MMAX):
                for x in range(0, NMAX):
                    if LABEL[y][x] == N or LABEL[y][x] == mergingLabel:
                        b[y][x] = sum
                        LABEL[y][x] = mergingLabel
        MARRAY[mergingLabel] = sum
        N = N - 1


def test_homogenity(N1, M1, N2, M2, T):
    global a
    max = 0
    min = 255
    i = 0
    j = 0

    for i in range(N1, N2):
        for j in range(M1, M2):
            if a[i][j] < min: min = a[i][j]
            if a[i][j] > max: max = a[i][j]
    if abs(max - min) < T:
        return (1)
    else:
        return (0)
    pass


def main():
    region_split_merge(N1, M1, N2, M2, T, Regmax)

    cv2.imshow('ImageWindow', b)
    cv2.waitKey(0)


main()
