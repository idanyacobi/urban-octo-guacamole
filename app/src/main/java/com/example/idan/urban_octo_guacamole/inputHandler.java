package com.example.idan.urban_octo_guacamole;

import android.provider.Settings;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

class inputHandler {
    String IMG_PATH = "/data/girl1.png";

    void getImg() {
        Mat m = Imgcodecs.imread(IMG_PATH);
        System.out.println(m);
    }
}
