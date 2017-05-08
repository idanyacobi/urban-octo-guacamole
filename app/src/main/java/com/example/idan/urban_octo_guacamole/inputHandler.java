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
    private static final int WIDTH_SIZE = 128;
    private static final int HEIGHT_SIZE = 256;
    private static final int WINDOW_SIZE = 16;
    private static final int STEP_OVERLAP = 2;
    private static final int IMG_DRAWABLE = R.drawable.girl1;
    private static final int BLOCK_SIZE = 16;
    private static final int CELL_SIZE = 8;
    private static final int PADDING_SIZE = 0;
    private static final int PATCH_SIZE = 32;
    private static final int OVERLAP_SIZE = PATCH_SIZE / STEP_OVERLAP;

    private final Context context;

    inputHandler(Context current) {
        this.context = current;
    }

    Mat getImg() {
        // need to resize image before creating the matrix

        // Image has been automatically resized because of high DPI of screen on device.
        // To avoid this we had to set inScaled option to false:
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), IMG_DRAWABLE, options);
        Mat sourceImage = new Mat(bMap.getWidth(), bMap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bMap, sourceImage);

        return sourceImage;
    }

    void splitToPatches(Mat imgMat) {
        Mat forHOGim = new Mat();
        Size sz = new Size(WIDTH_SIZE, HEIGHT_SIZE);
        Imgproc.resize(imgMat, imgMat, sz);
        Imgproc.cvtColor(imgMat, forHOGim, Imgproc.COLOR_RGB2GRAY);

        // base on http://stackoverflow.com/questions/38233753/android-opencv-why-hog-descriptors-are-always-zero
        Bitmap bm = Bitmap.createBitmap(imgMat.cols(), imgMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat.clone(), bm);

        MatOfFloat patch_descriptor = new MatOfFloat(); //an empty vector of descriptors
        Size winSize = new Size(WINDOW_SIZE, WINDOW_SIZE);
        Size blockSize = new Size(BLOCK_SIZE, BLOCK_SIZE);
        Size cellSize = new Size(CELL_SIZE, CELL_SIZE);
        Size winStride = new Size(WINDOW_SIZE / STEP_OVERLAP, WINDOW_SIZE / STEP_OVERLAP); //50% overlap in the sliding window
        Size padding = new Size(PADDING_SIZE,PADDING_SIZE); //no padding around the image
        MatOfPoint locations = new MatOfPoint(); // an empty vector of locations, so perform full search

        HOGDescriptor hog = new HOGDescriptor(winSize, blockSize, cellSize, cellSize, 9);
        Log.i(TAG, "Constructed");

        HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors = new HashMap<Integer, HashMap<Integer,MatOfFloat>>();

        for (int col = 0; col < (forHOGim.cols() - OVERLAP_SIZE); col += OVERLAP_SIZE) {
            HashMap<Integer, MatOfFloat> innerMap = new HashMap<Integer, MatOfFloat>();

            for (int row = 0; row < (forHOGim.rows() - OVERLAP_SIZE); row += OVERLAP_SIZE) {
                Rect roi = new Rect(col, row, PATCH_SIZE, PATCH_SIZE);
                Mat patch = forHOGim.submat(roi);

                hog.compute(patch, patch_descriptor);
                Log.i(TAG, "Computed");
                innerMap.put(row, patch_descriptor);
            }

            img_descriptors.put(col, innerMap);
        }
    }
}
