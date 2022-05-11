package com.example.projekt_poc;

import javafx.application.Platform;
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
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

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
    public Label postep;
    boolean O=false;
    boolean P=false;
    String path="";


    Mat a ;
    Mat b ;
    List<Mat> c=new ArrayList<Mat>();
    int y ;
    int x ;
    int N;
    int T = 15;
    //  int REGMAX = 20000; // maksymalna ilość regionówint
    int REGMAX = 20000; // maksymalna ilość regionówint
    double[] MARRAY = new double[REGMAX+3];
    int[][] LABEL = new int[x][y]; //array intow zawierajacy labele // w ksiazce zapisane jako *LABEL
    int NI = 0;
    int IT = 0; // licznik iteracji// nie ma orginalnie w w programie
    int NMAX = y;  // nie wiem, chyba granice obrazu
    int MMAX = x;  // nie wiem, chyba granice obrazu



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
        I=0;
        firstIteration.setImage(null);
        Image image=new Image("Gear.gif");
        loading.setImage(image);
        images=new ArrayList<>();
        b= Imgcodecs.imread(path);
        Thread thread = new Thread(){
            public void run(){
                obliczenia(b,Integer.parseInt(Threshold.getText()));
                matToImage mi =new matToImage();
                images.addAll(mi.toImages(c));
                thirdIteration.setImage(mi.toImage(b));
                if(!images.isEmpty()){
                    firstIteration.setImage(images.get(I));
                }
                loading.setImage(new Image("ok-icon.png"));
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
            O=true;
        }
        if(P && O){
            start.setDisable(false);
        }
    }


    public void obliczenia(Mat c,int T) {
        a =new Mat();
        b =new Mat();
        Imgproc.cvtColor(c,a, Imgproc.COLOR_BGR2GRAY);
        b=a.clone();
        y = (int) a.size().width;
        x = (int) a.size().height;
        NMAX = x; // nie wiem, chyba granice obrazu
        MMAX = y; // nie wiem, chyba granice obrazu
        N = 0; // ilość regionów # w książce odnoszą sie doniej przez referencje wiec chyba chcą mieć ją jako zmienna
// globalną, nie nie jestem pewnien jak działa

        int N1 = 0;//  # punkt od którego zaczynamy
        int M1 = 0;//   punkt od którego zaczynamy
        int N2 = NMAX;//   punkt w którym koczymy
        int M2 = MMAX;//   punkt w którym koczymy
        LABEL = new int[x][y];
        MARRAY = new double[REGMAX];
        System.out.println("zdj 00 "+a.get(0,0)[0]+"zdj 0 120 "+a.get(0,120)[0]+" zd 320 120 "+a.get(320,120)[0]);
        region_split_merge(b,LABEL,MARRAY,N1, M1, N2, M2, T, REGMAX);
    }

    public int region_split_merge(Mat b,int [][] LABEL,double[] MARRAY,int N1, int M1, int N2, int M2, int T, int REGMAX) {
        IT++;

        int  test = 0, ret = 0, ret1 = 0, ret2 = 0, ret3 = 0, ret4 = 0;
        long sum ;
        if (IT == 10000 + NI) {
            c.add(b.clone());
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    matToImage mi =new matToImage();
                    images.clear();
                    images.addAll(mi.toImages(c));
                    postep.setText("N:"+ N +" IT: "+ IT );
                }
            });
            I=0;
            NI = NI + 10000;
            //imshow("ImageWindow", b);
            //waitKey(0);
        }
        test = test_homogenity(N1, M1, N2, M2, T);
        if (test == 0 && (N2 - N1) > 1 & (M2 - M1) > 1) {
            ret1 = region_split_merge(b,LABEL,MARRAY,N1, M1, (N1 + (N2 - N1) / 2),
                    (M1 + (M2 - M1) / 2), T, REGMAX);
            ret2 = region_split_merge(b,LABEL,MARRAY,(N1 + (N2 - N1) / 2), M1, N2, (M1 + (M2 - M1) / 2), T,
                    REGMAX);
            ret3 = region_split_merge(b,LABEL,MARRAY,N1, (M1 + (M2 - M1) / 2), (N1 + (N2 - N1) / 2), M2, T,
                    REGMAX);
            ret4 = region_split_merge(b,LABEL,MARRAY,(N1 + (N2 - N1) / 2), (M1 + (M2 - M1) / 2), N2, M2, T,
                    REGMAX);
            if (ret1 == -1 || ret2 == -1 || ret3 == -1 || ret4 == -1) {
                ret = -1;
            }
        } else {
            sum=0;
            N ++;
            if( N > REGMAX) return -92;
            for(int j=M1;j<M2;j++) {
                for(int i=N1;i<N2;i++) {
                    sum += a.get(j, i)[0];
                    LABEL[j][i] = (int) N;
                }
            }
            sum /= (((long)(N2 - N1) * (long)(M2 - M1)));
            for(int j=M1;j<M2;j++) {
                for(int i=N1;i<N2;i++) {
                    b.put(j, i,sum);
                }
            }
            MARRAY[N] = (int) sum;
            if (N > 1) merge(b,LABEL,MARRAY,N1, M1, N2, M2, T);
        }
        return ret;
    }
    public void merge(Mat b,int[][] LABEL, double[] MARRAY, int N1, int M1, int N2, int M2, int T) {
        int mergingLabel =0,sum =0, count =0,y =0,x =0,xd =0,xu =0, yd =0,yu =0,cmin =0,c =0;

        if(N1 -1>=0) xd = N1 - 1;else xd =0;
        if(N2 +1 <NMAX) {xu = N2 + 1;} else{xu =N2;}
        if(M1 -1>=0) {yd =M1 -1;}else{yd =0;}
        if(M2 +1 <MMAX) {yu =M2 +1;}else{yu = M2;}
        cmin = 255;

        if (M1 -1>=0) {
            y = M1 - 1;
            for (x = xd; x < xu; x++) {
                if (b.get(y,x)[0] != 0) {
                    c = (int) abs(b.get(y,x)[0] - (float) MARRAY[N]);
                    if (c < cmin) {
                        cmin = c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if (M2 +1 <MMAX){
            y =M2 +1;
            for (x=xd;x<xu;x++){
                if (b.get(y,x)[0]!=0){
                    c = (int) abs(b.get(y,x)[0]-(float) MARRAY[N]);
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
                if (b.get(y,x)[0]!=0){
                    c = (int) (abs(b.get(y,x)[0]- (float) MARRAY[N]));
                    if (c<cmin){
                        cmin =c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if (N2 +1 <NMAX){
            x =N2 +1;
            for (y=yd;y<yu;y++){
                if (b.get(y,x)[0]!=0){
                    c = (int) (abs(b.get(y,x)[0]- (float) MARRAY[N]));
                    if (c<cmin){
                        cmin =c;
                        mergingLabel = LABEL[y][x];
                    }
                }
            }
        }
        if(cmin<T) {
            sum=0;count=0;
            for (y = 0; y < MMAX; y++)
                for (x = 0; x < NMAX; x++)
                    if (LABEL[y][x] == N || LABEL[y][x] == mergingLabel) {
                        sum +=a.get(y, x)[0];
                        count++;
                    }
            if (count !=0){
                sum /= count;
                for (y = 0; y < MMAX; y++)
                    for (x = 0; x < NMAX; x++)
                        if(LABEL[y][x]==N ||LABEL[y][x]==mergingLabel ){
                            b.put(y,x,sum);
                            LABEL[y][x]=mergingLabel;
                        }
            }
            MARRAY[mergingLabel]= sum;
            N--;
        }
    }

    public int test_homogenity(int N1, int M1, int N2, int M2, int T) {
        int max = 0;
        int min = 255;
        int i = 0;
        int j = 0;

        for (i = N1; i < N2; i++) {
            for (j = M1; j < M2; j++) {
                if (a.get(i,j)[0] < min) min = (int) a.get(i,j)[0];
                if (a.get(i,j)[0] > max) max = (int) a.get(i,j)[0];
            }
        }
        if (abs(max - min) < T) {
            return (1);
        } else {
            return (0);
        }

    }






}