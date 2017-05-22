package com.example.idan.urban_octo_guacamole;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by idan on 16/05/2017.
 */

public class utils {
    public static Bitmap mat2bmp(Mat m) {
        Bitmap bmp = null;
        Mat tmp = new Mat (m.rows(), m.cols(), Settings.IMAGE_CVTYPE, new Scalar(4));

        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
            Imgproc.cvtColor(m, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }

        return bmp;
    }
}
