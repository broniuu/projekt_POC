package com.example.projekt_poc;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public String[] pol={"start","wybierz plik","następny","poprzedni","Próg","Próbka iteracyjna"};
    public String[] ang={"start","Choose File","next","previous","Threshold","Iteration Sample"};
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
    public Label postep;

    public ChoiceBox iterationSampleChoiceBox;
    boolean O=false;
    boolean P=false;
    String path="";
    ObservableList<Integer> listOfItSample = FXCollections.observableArrayList(1000,3000, 5000, 7000, 9000, 11000, 13000, 15000);
    @FXML
    private Label welcomeText;
    
    public void getPath(ActionEvent event) {
        I=0;
        FileChooser fileChooser=new FileChooser();
        String imagesPath = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(imagesPath+"/Images"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg","*.bmp"));
        File selectedFile=fileChooser.showOpenDialog(null);
        
        if(selectedFile!=null){
            path= selectedFile.getPath();
            Image im =new Image(path);
            MainPhoto.setImage(im);
            P=true;
        }
        if(P && O){
            start.setDisable(false);
        }
    }
    public void start(ActionEvent event) {
        if(images!=null){
            images.clear();
        }
        thirdIteration.setImage(null);
        I=0;
        postep.setText("");
        firstIteration.setImage(null);
        Image image=new Image("Gear.gif");
        loading.setImage(image);
        images=new ArrayList<>();
        Mat mt=new Mat();
        mt= Imgcodecs.imread(path);
        Mat finalMt = mt;

        SplitAndMerge segmentatingMachine=new SplitAndMerge();
        Thread thread = new Thread(){
            public void run(){
                segmentatingMachine.obliczenia(finalMt,Integer.parseInt(Threshold.getText()), (Integer) iterationSampleChoiceBox.getValue());
                matToImage mi =new matToImage();
                images=segmentatingMachine.images;
                for (int i=0;i<finalMt.rows();i++)
                    finalMt.put(i,0, segmentatingMachine.segmentedImagePixels[i]);
                thirdIteration.setImage(mi.toImage(finalMt));
                if(!images.isEmpty()){
                    firstIteration.setImage(images.get(0));
                }
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        postep.setText("Regions: "+segmentatingMachine.getNumberOfRegions() + "   It:"+segmentatingMachine.getIterator());
                    }
                });
                loading.setImage(new Image("ok-icon.png"));
                //tu zaczyna sie kolorowanie I Ustawianie zdjęcia:
                photoColoring pC=new photoColoring(segmentatingMachine.x,segmentatingMachine.y,segmentatingMachine.segmentedImagePixels) ;
                pC.colors();
                Image im=mi.toImage(pC.getColorImg());

                thirdIteration.setImage(im); // obencie ustawia sie do wynikowego zdjęcia trzeba dorobić nowe okno
                // a tu sie konczy


            }
        };
        thread.start();

        O=false;
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
        iterationSampleChoiceBox.setItems(listOfItSample);
        jezykBox.setItems(FXCollections.observableArrayList(angL));
        jezykBox.setValue(angL[0]);
        iterationSampleChoiceBox.setValue(listOfItSample.get(0));
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
            O=true;
        }
        if(P && O){
            start.setDisable(false);
        }
    }

}