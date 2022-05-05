package com.example.projekt_poc;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public String[] pol={"start","wybierz plik","następny","poprzedni","Próg"};
    public String[] ang={"start","Choose File","next","previous","Threshold"};
    public String[] angL={"Polish","English"};
    public String[] polL={"Polski","Angielski"};
    public ImageView MainPhoto;
    public ImageView firstIteration;
    public ImageView thirdIteration;
    public List<Image> images;
    public int I=0;
    public ChoiceBox jezykBox;
    public Button chooseP;
    public Button start;
    public Button nextB;
    public Button prevB;
    public ImageView loading;
    public TextField Threshold;
    public Label thresholdLabel;
    boolean T=false;
    boolean P=false;
    String path="";
    @FXML
    private Label welcomeText;
    
    public void getPath(ActionEvent event) {
        I=0;
        FileChooser fileChooser=new FileChooser();
        fileChooser.setInitialDirectory(new File("./Images"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg","*.bmp"));
        File selectedFile=fileChooser.showOpenDialog(null);
        
        if(selectedFile!=null){
            path= selectedFile.getPath();
            Image im =new Image(path);
            MainPhoto.setImage(im);
            P=true;
        }
        if(P && T){
            start.setDisable(false);
        }
    }
    public void start(ActionEvent event) {
        I=0;
        firstIteration.setImage(null);
        Image image=new Image("Gear.gif");
        loading.setImage(image);
        images=new ArrayList<>();
        Mat b= Imgcodecs.imread(path);
        SplitAndMerge splitAndMerge=new SplitAndMerge();
        Thread thread = new Thread(){
            public void run(){
                splitAndMerge.obliczenia(b,Integer.parseInt(Threshold.getText()));
                matToImage mi =new matToImage();
                images.addAll(mi.toImages(splitAndMerge.c));
                thirdIteration.setImage(mi.toImage(splitAndMerge.b));
                if(!images.isEmpty()){
                    firstIteration.setImage(images.get(I));
                }
                loading.setImage(new Image("ok-icon.png"));
            }
        };
        thread.start();
        T=false;
        P=false;
    }

    public void prev(ActionEvent event) {
        if(images!=null){
            if(I>0){
                I--;
                firstIteration.setImage(images.get(I));
            }
        }
    }

    public void next(ActionEvent event) {
        if(images!=null){
            if(I+1<images.size()){
                I++;
                firstIteration.setImage(images.get(I));
            }
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            jezykBox.setItems(FXCollections.observableArrayList(angL));
            jezykBox.setValue(angL[0]);
    }
    public void changeL(ActionEvent event) {
        jezykBox.setValue(jezykBox.getValue());
        if(jezykBox.getValue()==angL[0]){
            thresholdLabel.setText(pol[4]);
            prevB.setText(pol[3]);
            nextB.setText(pol[2]);
            start.setText(pol[0]);
            chooseP.setText(pol[1]);
        }
        else{
            thresholdLabel.setText(ang[4]);
            prevB.setText(ang[3]);
            nextB.setText(ang[2]);
            start.setText(ang[0]);
            chooseP.setText(ang[1]);
        }
    }

    public void checkAction(ActionEvent event) {
        if(Threshold.getText() != null){
            T=true;
        }
        if(P && T){
            start.setDisable(false);
        }
    }
}