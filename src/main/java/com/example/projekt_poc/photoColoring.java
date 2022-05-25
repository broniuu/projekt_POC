package com.example.projekt_poc;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opencv.core.CvType.*;
public class photoColoring {
    Mat colorImg;
    int IT=0;
    int [] colors;

    int[][] pictureInt;
    double [][] pictureDouble;
    photoColoring(int x,int y,int[][] Photo){
        colorImg=new Mat(x,y,CV_16UC3);
        pictureInt =Photo;
        colors = new int[x*y];
    }
    photoColoring(int x,int y,double[][] Photo){
        colorImg=new Mat(x,y,CV_16UC3);
        pictureDouble = Photo;
        colors = new int[x*y];
    }
    public void colorsDouble(){
        int it=0;
        for(int i = 0; i< pictureDouble[1].length; i++){
            for(int j = 0; j< pictureDouble[2].length; j++){
                if(!checkIfIn(pictureDouble[i][j])){
                    colors[it]=(int) pictureDouble[i][j];
                    it++;
                }
            }
        }
        Random rand = new Random();
        List<int[]> newColors = new ArrayList<int[]>();
        int Is = 1;
        for (double c:colors) {
            int r = rand.nextInt(1,255);
            int g = rand.nextInt(1,255);
            int b = rand.nextInt(1,255);
            newColors.add(new int[]{r, g, b});
            if(c!=0){
                Is++;
            }
        }
        System.out.println(" "+Is);
        for(int i = 0; i< pictureDouble[1].length; i++){
            for(int j = 0; j< pictureDouble[2].length; j++){
                if(checkIfIn(pictureDouble[i][j])){
                    colorImg.put(i,j,newColors.get(IT)[0],newColors.get(IT)[1],newColors.get(IT)[2]) ;
                }
            }
        }
    }
    public void colors(){
        int it=0;
        for(int i = 0; i< pictureInt[1].length; i++){
            for(int j = 0; j< pictureInt[2].length; j++){
                if(!checkIfIn(pictureInt[i][j])){
                    colors[it]= pictureInt[i][j];
                    it++;
                }
            }
        }
        Random rand = new Random();
        List<int[]> newColors = new ArrayList<int[]>();
        int Is = 1;
        for (double c:colors) {
            int r = rand.nextInt(1,255);
            int g = rand.nextInt(1,255);
            int b = rand.nextInt(1,255);
            newColors.add(new int[]{r, g, b});
            if(c!=0){
                Is++;
            }
        }
        System.out.println(" "+Is);
        for(int i = 0; i< pictureInt[1].length; i++){
            for(int j = 0; j< pictureInt[2].length; j++){
                if(checkIfIn(pictureInt[i][j])){
                    colorImg.put(i,j,newColors.get(IT)[0],newColors.get(IT)[1],newColors.get(IT)[2]) ;
                }
            }
        }
    }
    public boolean checkIfIn(double c){
        IT=0;
        while(IT<colors.length){
            if(colors[IT]==c){
                return true;
            }
            IT++;
        }
        return false;
    }

    public Mat getColorImg() {
        return colorImg;
    }
}
