package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import static android.content.ContentValues.TAG;

class inputHandler {
    private final Context context;
    String IMG_PATH = "drawable/girl1.png";

    public inputHandler(Context current) {
        this.context = current;
    }

    Mat getImg() {
        // need to resize image before creating the matrix

        // TODO: read it directly to Mat
        // Image has been automatically resized because of high DPI of screen on device.
        // To avoid this we had to set inScaled option to false:
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),R.drawable.girl1, options);
        Mat sourceImage = new Mat(bMap.getWidth(), bMap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bMap, sourceImage);

        return sourceImage;
    }

    void splitToPatches(Mat imgMat) {
        // base on http://stackoverflow.com/questions/38233753/android-opencv-why-hog-descriptors-are-always-zero
        Bitmap bm = Bitmap.createBitmap(imgMat.cols(), imgMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat.clone(), bm);
        // find the imageview and draw it!
//        ImageView iv = (ImageView) getRootView().findViewById(R.id.imageView);
//        this.setVisibility(SurfaceView.GONE);
//        iv.setVisibility(ImageView.VISIBLE);

        Mat forHOGim = new Mat();
        org.opencv.core.Size sz = new org.opencv.core.Size(64, 128);
        Imgproc.resize(imgMat, imgMat, sz);
        Imgproc.cvtColor(imgMat, forHOGim, Imgproc.COLOR_RGB2GRAY);

        MatOfFloat descriptors = new MatOfFloat(); //an empty vector of descriptors
        org.opencv.core.Size winStride = new org.opencv.core.Size(64/2,128/2); //50% overlap in the sliding window
        org.opencv.core.Size padding = new org.opencv.core.Size(0,0); //no padding around the image
        MatOfPoint locations = new MatOfPoint(); // an empty vector of locations, so perform full search
        //HOGDescriptor hog = new HOGDescriptor();
        HOGDescriptor hog = new HOGDescriptor(sz, new org.opencv.core.Size(16,16), new org.opencv.core.Size(8,8), new org.opencv.core.Size(8,8), 9);
        Log.i(TAG, "Constructed");

//        with or without thw custom window size
//        hog.compute(forHOGim, descriptors, new org.opencv.core.Size(16,16), padding, locations);
        hog.compute(forHOGim, descriptors, winStride, padding, locations);
        Log.i(TAG,"Computed");
        Log.i(TAG,String.valueOf(hog.getDescriptorSize())+" "+descriptors.size());
        Log.i(TAG,String.valueOf(descriptors.get(12,0)[0]));
        double dd=0.0;
        for (int i=0;i<3780;i++){
            if (descriptors.get(i,0)[0]!=dd) Log.i(TAG,"NOT ZERO");
        }

        Bitmap bm2 = Bitmap.createBitmap(forHOGim.cols(), forHOGim.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(forHOGim,bm2);
//        iv.setImageBitmap(bm2);
    }
}
