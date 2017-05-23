package com.example.idan.urban_octo_guacamole;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

class inputHandler {


    HashMap<Integer, HashMap<Integer, Mat>> splitToPatches(Mat imgMat) {
        Mat forHOGim = new Mat();
        Size sz = new Size(Settings.WIDTH_SIZE, Settings.HEIGHT_SIZE);
        Imgproc.resize(imgMat, imgMat, sz);
        Imgproc.cvtColor(imgMat, forHOGim, Imgproc.COLOR_RGB2GRAY);

        HashMap<Integer, HashMap<Integer, Mat>> img_descriptors = new HashMap<>();
        // TODO: when overlap return to < instead of <=
        for (int col = 0; col <= (forHOGim.cols() - Settings.OVERLAP_SIZE); col += Settings.OVERLAP_SIZE) {
            HashMap<Integer, Mat> innerMap = new HashMap<>();
            // TODO: when overlap return to < instead of <=
            for (int row = 0; row <= (forHOGim.rows() - Settings.OVERLAP_SIZE); row += Settings.OVERLAP_SIZE) {

                Rect roi = new Rect(col, row, Settings.PATCH_SIZE, Settings.PATCH_SIZE);
                Mat patch = forHOGim.submat(roi);

                Log.i(TAG, "'Computed'");
                innerMap.put(row, patch);
            }

            img_descriptors.put(col, innerMap);
        }

        return img_descriptors;
    }
}
