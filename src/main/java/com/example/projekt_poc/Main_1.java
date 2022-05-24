package com.example.projekt_poc;

import com.example.projekt_poc.HelloApplication;
import org.opencv.core.Core;

import java.io.File;
import java.io.InputStream;

public class Main_1 {
    public static void main(String[] args) {
        HelloApplication.main(args);
        loadLibraries();
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
                opencvpath = opencvpath+"Your path to .dylib";
            }
            System.out.println(opencvpath);
            System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }
}
