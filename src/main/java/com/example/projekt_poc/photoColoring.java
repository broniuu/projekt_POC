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
    int[][] picture;

    photoColoring(int x,int y,int[][] Photo){
        colorImg=new Mat(x,y,CV_16UC3);
        picture=Photo;
        colors = new int[x*y];
    }

    public void colors(){
        int it=0;
        for(int i = 0; i< picture[1].length; i++){
            for(int j = 0; j< picture[2].length; j++){
                if(!checkIfIn(picture[i][j])){
                    colors[it]= picture[i][j];
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
        for(int i = 0; i< picture[1].length; i++){
            for(int j = 0; j< picture[2].length; j++){
                if(checkIfIn(picture[i][j])){
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
