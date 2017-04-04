package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

class inputHandler {
    private final Context context;
    String IMG_PATH = "drawable/girl1.png";

    public inputHandler(Context current) {
        this.context = current;
    }

    void getImg() {
        // TODO: read it directly to Mat

        // Image has been automatically resized because of high DPI of screen on device.
        // To avoid this we had to set inScaled option to false:
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),R.drawable.girl1, options);
        Mat sourceImage = new Mat();
        Utils.bitmapToMat(bMap, sourceImage);

    }
}
