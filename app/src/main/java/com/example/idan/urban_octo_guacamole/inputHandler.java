package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
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


    HashMap<Integer, HashMap<Integer, MatOfFloat>> splitToPatches(Mat imgMat) {
        Mat forHOGim = new Mat();
        Size sz = new Size(Settings.WIDTH_SIZE, Settings.HEIGHT_SIZE);
        Imgproc.resize(imgMat, imgMat, sz);
        Imgproc.cvtColor(imgMat, forHOGim, Imgproc.COLOR_RGB2GRAY);

        // base on http://stackoverflow.com/questions/38233753/android-opencv-why-hog-descriptors-are-always-zero

        Size winSize = new Size(Settings.WINDOW_SIZE, Settings.WINDOW_SIZE);
        Size blockSize = new Size(Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
        Size cellSize = new Size(Settings.CELL_SIZE, Settings.CELL_SIZE);
        Size winStride = new Size(Settings.WINDOW_SIZE / Settings.STEP_OVERLAP,
                Settings.WINDOW_SIZE / Settings.STEP_OVERLAP); //50% overlap in the sliding window
        Size padding = new Size(Settings.PADDING_SIZE,Settings.PADDING_SIZE); //no padding around the image
        MatOfPoint locations = new MatOfPoint(); // an empty vector of locations, so perform full search

        HOGDescriptor hog = new HOGDescriptor(winSize, blockSize, cellSize, cellSize, 9);
        Log.i(TAG, "Constructed");

        HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors = new HashMap<>();

        for (int col = 0; col < (forHOGim.cols() - Settings.OVERLAP_SIZE); col += Settings.OVERLAP_SIZE) {
            HashMap<Integer, MatOfFloat> innerMap = new HashMap<>();

            for (int row = 0; row < (forHOGim.rows() - Settings.OVERLAP_SIZE); row += Settings.OVERLAP_SIZE) {
                MatOfFloat patch_descriptor = new MatOfFloat(); //an empty vector of descriptors

                Rect roi = new Rect(col, row, Settings.PATCH_SIZE, Settings.PATCH_SIZE);
                Mat patch = forHOGim.submat(roi);

                hog.compute(patch, patch_descriptor, winStride, padding, locations);
                Log.i(TAG, "Computed");
                innerMap.put(row, patch_descriptor);
            }

            img_descriptors.put(col, innerMap);
        }

        return img_descriptors;
    }
}
