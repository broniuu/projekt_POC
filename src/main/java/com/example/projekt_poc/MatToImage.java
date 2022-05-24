package com.example.projekt_poc;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class MatToImage {
    public Image toImage(Mat mat ){
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", mat, byteMat);
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }
    public List<Image> toImages(List<Mat> mat ){
        List<Image> im=new ArrayList<Image>();
        for (Mat m:mat) {
            MatOfByte byteMat = new MatOfByte();
            Imgcodecs.imencode(".bmp", m, byteMat);
            im.add(new Image(new ByteArrayInputStream(byteMat.toArray())));
        }
        return im;

    }
}
