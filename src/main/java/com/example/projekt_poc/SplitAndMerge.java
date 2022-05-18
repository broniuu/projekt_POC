package com.example.projekt_poc;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static org.opencv.core.CvType.CV_8UC1;

public class SplitAndMerge {
    double[][] inputImagePixels;
    double[][] segmentedImagePixels;
    public List<Image> images;
    int y ;
    int x ;
    int numberOfRegions;
    //  int REGMAX = 20000; // maksymalna ilość regionówint
    final int maximalRegionsCount = 200000; // maksymalna ilość regionówint
    double[] meanRegionsValues = new double[maximalRegionsCount];
    int[][] LABEL = new int[x][y]; //array intow zawierajacy labele // w ksiazce zapisane jako *LABEL
    int lastIterationSampleValue = 0;
    int iterator = 0; // licznik iteracji// nie ma orginalnie w w programie
    int lastImageRow = y;  // nie wiem, chyba granice obrazu
    int iterationSampleSize;
    int lastImageColumn = x;  // nie wiem, chyba granice obrazu
    public void obliczenia(Mat c, int T,int sampleIter) {
        images=new ArrayList<>();
        iterator =0;
        numberOfRegions =0;
        this.iterationSampleSize =sampleIter;
        Imgproc.cvtColor(c,c, Imgproc.COLOR_BGR2GRAY);
        inputImagePixels =new double[c.rows()][c.cols()];
        segmentedImagePixels =new double[c.rows()][c.cols()];
        for(int i=0;i<c.rows();i++){
            for(int j=0;j<c.cols();j++){
                inputImagePixels[i][j]=c.get(i,j)[0];
                segmentedImagePixels[i][j]=c.get(i,j)[0];
            }
        }
        y = c.rows();
        x =  c.cols();
        lastImageRow = x; // nie wiem, chyba granice obrazu
        lastImageColumn = y; // nie wiem, chyba granice obrazu
        numberOfRegions = 0; // ilość regionów # w książce odnoszą sie doniej przez referencje wiec chyba chcą mieć ją jako zmienna
// globalną, nie nie jestem pewnien jak działa

        int firstPixelColumn = 0;//  # punkt od którego zaczynamy
        int firstPixelRow = 0;//   punkt od którego zaczynamy
        int lastPixelColumn = lastImageRow;//   punkt w którym koczymy
        int lastPixelRow = lastImageColumn;//   punkt w którym koczymy
        LABEL = new int[x][y];
        meanRegionsValues = new double[maximalRegionsCount];
        region_split_merge(inputImagePixels, segmentedImagePixels,LABEL, meanRegionsValues,firstPixelColumn, firstPixelRow, lastPixelColumn, lastPixelRow, T, maximalRegionsCount);
    }
    public int region_split_merge(double[][] a,double[][] b,int [][] LABEL,double[] MARRAY,int N1, int M1, int N2, int M2, int T, int REGMAX) {
        iterator++;
        int  test , ret =0, ret1 , ret2 , ret3 , ret4 ;
        long sum ;
        if (iterator == iterationSampleSize + lastIterationSampleValue) {
            matToImage mi =new matToImage();
            Mat m=new Mat(x,y, CV_8UC1);
            int j=0;
            for (int i=0;i<a[1].length;i++){
                m.put(i,0, b[i]);
            }
            images.add(mi.toImage(m));


            lastIterationSampleValue = lastIterationSampleValue + iterationSampleSize;
            //imshow("ImageWindow", b);
            //waitKey(0);
        }
        test = test_homogenity(N1, M1, N2, M2, T);
        if (test == 0 && (N2 - N1) > 1 & (M2 - M1) > 1) {
            ret1 = region_split_merge(a,b,LABEL,MARRAY,N1, M1, (N1 + (N2 - N1) / 2),
                    (M1 + (M2 - M1) / 2), T, REGMAX);
            ret2 = region_split_merge(a,b,LABEL,MARRAY,(N1 + (N2 - N1) / 2), M1, N2, (M1 + (M2 - M1) / 2), T,
                    REGMAX);
            ret3 = region_split_merge(a,b,LABEL,MARRAY,N1, (M1 + (M2 - M1) / 2), (N1 + (N2 - N1) / 2), M2, T,
                    REGMAX);
            ret4 = region_split_merge(a,b,LABEL,MARRAY,(N1 + (N2 - N1) / 2), (M1 + (M2 - M1) / 2), N2, M2, T,
                    REGMAX);
            if (ret1 == -1 || ret2 == -1 || ret3 == -1 || ret4 == -1) {
                ret = -1;
            }
        } else {
            sum=0;
            numberOfRegions++;
            if( numberOfRegions > REGMAX) return -92;
            for(int j=M1;j<M2;j++) {
                for(int i=N1;i<N2;i++) {
                    sum +=  a[j][i];
                    LABEL[j][i] = numberOfRegions;
                }
            }
            sum /= (((long)(N2 - N1) * (long)(M2 - M1)));
            for(int j=M1;j<M2;j++) {
                for(int i=N1;i<N2;i++) {
                    b[j][i]=sum;
                }
            }
            MARRAY[numberOfRegions] = (int) sum;
            if (numberOfRegions > 1) merge(a,b,LABEL,MARRAY,N1, M1, N2, M2, T);
        }
        return ret;
    }
    public void merge(double[][] a,double[][] b, int[][] LABEL, double[] MARRAY, int N1, int M1, int N2, int M2, int T) {
        int mergingLabel=0 ,sum , count ,y ,x ,xd ,xu , yd ,yu ,cmin ,c ;

        if(N1 -1>=0) xd = N1 - 1;else xd =0;
        if(N2 +1 < lastImageRow) {xu = N2 + 1;} else{xu =N2;}
        if(M1 -1>=0) {yd =M1 -1;}else{yd =0;}
        if(M2 +1 < lastImageColumn) {yu =M2 +1;}else{yu = M2;}
        cmin = 255;

        if (M1 -1>=0) {
            y = M1 - 1;
            for (x = xd; x < xu; x++) {
                if (b[y][x]!= 0) {
                    c = (int) abs(b[y][x] - (float) MARRAY[numberOfRegions]);
                    if (c < cmin) {
                        cmin = c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if (M2 +1 < lastImageColumn){
            y =M2 +1;
            for (x=xd;x<xu;x++){
                if ( b[y][x]!=0){
                    c = (int) abs(b[y][x]-(float) MARRAY[numberOfRegions]);
                    if (c<cmin){
                        cmin =c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if (N1 -1>=0){
            x =N1 -1;
            for (y=yd;y<yu;y++){
                if (b[y][x]!=0){
                    c = (int) (abs(b[y][x]- (float) MARRAY[numberOfRegions]));
                    if (c<cmin){
                        cmin =c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if (N2 +1 < lastImageRow){
            x =N2 +1;
            for (y=yd;y<yu;y++){
                if (b[y][x]!=0){
                    c = (int) (abs(b[y][x]- (float) MARRAY[numberOfRegions]));
                    if (c<cmin){
                        cmin =c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if(cmin<T) {
            sum=0;count=0;
            for (y = 0; y < lastImageColumn; y++)
                for (x = 0; x < lastImageRow; x++)
                    if (LABEL[y][x] == numberOfRegions || LABEL[y][x] == mergingLabel) {
                        sum +=a[y][x];
                        count++;
                    }
            if (count !=0){
                sum /= count;
                for (y = 0; y < lastImageColumn; y++)
                    for (x = 0; x < lastImageRow; x++)
                        if(LABEL[y][x]== numberOfRegions ||LABEL[y][x]==mergingLabel ){
                            b[y][x]=sum;
                            LABEL[y][x]=mergingLabel;
                        }
            }
            MARRAY[mergingLabel]= sum;
            numberOfRegions--;
        }
    }

    public int test_homogenity(int N1, int M1, int N2, int M2, int T) {
        int max = 0;
        int min = 255;
        int i = 0;
        int j = 0;
        for (i = N1; i < N2; i++) {
            for (j = M1; j < M2; j++) {
                if (inputImagePixels[i][j] < min) min = (int) inputImagePixels[i][j];
                if (inputImagePixels[i][j] > max) max = (int) inputImagePixels[i][j];
            }
        }
        if (abs(max - min) < T) {
            return (1);
        } else {
            return (0);
        }
    }

    public List<Image> getImages() {
        return images;
    }

    public double[][] getSegmentedImagePixels() {
        return segmentedImagePixels;
    }

    public int getNumberOfRegions() {
        return numberOfRegions;
    }

    public int getIterator() {
        return iterator;
    }

    public void setInputImagePixels(double[][] inputImagePixels) {
        this.inputImagePixels = inputImagePixels;
    }

    public void setSegmentedImagePixels(double[][] segmentedImagePixels) {
        this.segmentedImagePixels = segmentedImagePixels;
    }

    public void setIterationSampleSize(int iterationSampleSize) {
        this.iterationSampleSize = iterationSampleSize;
    }
}
