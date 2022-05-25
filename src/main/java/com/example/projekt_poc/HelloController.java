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
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public String[] pol={"start","wybierz plik","następny","poprzedni","Próg","Próbka iteracyjna","przed segmentacją","etapy segmentacji","po segmentacji","Segmentacja Split And Merge","(naciśnij enter aby zatwierdzić próg)","przed uśrednieniem","po uśrednieniu"};
    public String[] ang={"start","select file","next","previous","Threshold","Iteration Sample","before segmentation","segmentation stages","after segmentation","Split And Merge Segmentation","(press enter to confirm treshold)","after averaging","before averaging"};
    public String[] languages ={"polski","English"};
    public ImageView MainPhoto;
    public ImageView firstIteration;

    public List<Image> images;
    public int I=0;
    public int J=0;
    public ChoiceBox jezykBox;
    public Button chooseP;
    public Button start;
    public Button nextB;
    public Button prevB;
    public List<Image> coloredImages;
    public ImageView loading;
    public TextField Threshold;
    public Label thresholdLabel;
    public Label postep;
    public Label iterationSample;
    public Label title;
    public Label beforeSegmentation;
    public Label segmentationStages;
    public Label afterSegmentation;
    public Label pressEnter;
    public String[] regionsAndIterations = {"",""};
    public ChoiceBox iterationSampleChoiceBox;
    public Button prevB2;
    public Button nextB2;
    public ImageView coloredImage;
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
        fileChooser.setInitialDirectory(new File(imagesPath+"./"));
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
        OpenCV.loadLocally();
        if(images!=null){
            images.clear();
        }
        coloredImage.setImage(null);
        I=0;
        postep.setText("");
        firstIteration.setImage(null);
        Image gifImage =new Image("Gear.gif");
        loading.setImage(gifImage);
        images=new ArrayList<>();
        Mat mt=new Mat();
        mt= Imgcodecs.imread(path);
        Mat finalMt = mt;

        SplitAndMerge segmentatingMachine=new SplitAndMerge();
        Thread thread = new Thread(){
            public void run(){
                segmentatingMachine.obliczenia(finalMt,Integer.parseInt(Threshold.getText()), (Integer) iterationSampleChoiceBox.getValue());
                MatToImage mi =new MatToImage();
                images=segmentatingMachine.images;
                images.add(segmentatingMachine.doubleToImage(segmentatingMachine.segmentedImagePixels));
                for (int i=0;i<finalMt.rows();i++)
                    finalMt.put(i,0, segmentatingMachine.segmentedImagePixels[i]);
                coloredImage.setImage(mi.toImage(finalMt));
                if(!images.isEmpty()){
                    firstIteration.setImage(images.get(0));
                }
                Platform.runLater(() -> {
                    regionsAndIterations[0] = "Regiony: "+segmentatingMachine.getNumberOfRegions() + "   Iteracje: "+segmentatingMachine.getIterator();
                    regionsAndIterations[1] = "Regions: "+segmentatingMachine.getNumberOfRegions() + "   Iterations: "+segmentatingMachine.getIterator();
                    if(jezykBox.getValue()== languages[0]){
                        postep.setText(regionsAndIterations[0]);
                    }
                    if(jezykBox.getValue()== languages[1]){
                        postep.setText(regionsAndIterations[1]);
                    }

                });
                //tu zaczyna sie kolorowanie I Ustawianie zdjęcia:
                photoColoring pC=new photoColoring(segmentatingMachine.x,segmentatingMachine.y,segmentatingMachine.LABEL) ;
                pC.colors();
                Image im=mi.toImage(pC.getColorImg());
                photoColoring pC2=new photoColoring(segmentatingMachine.x,segmentatingMachine.y,segmentatingMachine.segmentedImagePixels) ;
                pC2.colorsDouble();
                Image imc2=mi.toImage(pC2.getColorImg());
                coloredImages=new ArrayList<>();
                coloredImages.add(im);
                coloredImages.add(imc2);
                coloredImage.setImage(im); // obencie ustawia sie do wynikowego zdjęcia trzeba dorobić nowe okno
                loading.setImage(new Image("ok-icon.png"));
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
    public void prevC(ActionEvent event) {
        if(coloredImages!=null){
            coloredImage.setImage(coloredImages.get(0));
        }
    }
    public void nextC(ActionEvent event) {
        if(coloredImages!=null){
            coloredImage.setImage(coloredImages.get(1));

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstIteration.setStyle("-fx-background-color: BLACK");
        iterationSampleChoiceBox.setItems(listOfItSample);
        jezykBox.setItems(FXCollections.observableArrayList(languages));
        jezykBox.setValue(languages[1]);
        iterationSampleChoiceBox.setValue(listOfItSample.get(0));
    }
    public void changeL(ActionEvent event) {
        if(jezykBox.getValue()== languages[0]){
            pressEnter.setText(pol[10]);
            title.setText(pol[9]);
            afterSegmentation.setText(pol[8]);
            segmentationStages.setText(pol[7]);
            beforeSegmentation.setText(pol[6]);
            iterationSample.setText(pol[5]);
            thresholdLabel.setText(pol[4]);
            prevB.setText(pol[3]);
            nextB.setText(pol[2]);
            prevB2.setText(pol[12]);
            nextB2.setText(pol[11]);
            start.setText(pol[0]);
            chooseP.setText(pol[1]);
            if(postep.getText() != ""){
                postep.setText(regionsAndIterations[0]);
            }
        }
        if(jezykBox.getValue()== languages[1]){
            pressEnter.setText(ang[10]);
            title.setText(ang[9]);
            afterSegmentation.setText(ang[8]);
            segmentationStages.setText(ang[7]);
            beforeSegmentation.setText(ang[6]);
            iterationSample.setText(ang[5]);
            thresholdLabel.setText(ang[4]);
            prevB.setText(ang[3]);
            nextB.setText(ang[2]);
            prevB2.setText(ang[12]);
            nextB2.setText(ang[11]);
            start.setText(ang[0]);
            chooseP.setText(ang[1]);
            if(postep.getText() != ""){
                postep.setText(regionsAndIterations[1]);
            }
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