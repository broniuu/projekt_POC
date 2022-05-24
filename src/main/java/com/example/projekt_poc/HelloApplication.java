package com.example.projekt_poc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        loadLibraries();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 500);
        stage.setScene(scene);
        stage.setTitle("Demonstration of Split and Merge");
        stage.show();

    }

    public static void main(String[] args) {

        launch();
    }
    private static void loadLibraries() {
        try {
            InputStream in = null;
            File fileOut = null;
            String osName = System.getProperty("os.name");
            String opencvpath = System.getProperty("user.dir");
            if(osName.startsWith("Windows")) {
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));

                if (bitness == 64) {
                    opencvpath=opencvpath+"/src/java/x64/";
                } else {
                    opencvpath=opencvpath+"/src/java/x86/";
                }
            }
            else if(osName.equals("Mac OS X")){
                opencvpath=opencvpath+"/src/java/x86/";
            }
            System.out.println(opencvpath);
            System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }
}