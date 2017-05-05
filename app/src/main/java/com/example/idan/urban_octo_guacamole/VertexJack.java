package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;
import java.net.ContentHandler;

/**
 * Created by avrni on 4/24/2017.
 */

public class VertexJack {
    Context context;
    Mat imgMat;
    Bitmap imgOrg;
    Size s;
    public float[][] colors;

    public VertexJack(Context context){
        this.context = context;
        this.imgMat = getImgGrayScale();
//        InputStream stream = context.getResources().openRawResource(R.raw.jack);

    }

    public Mat getImgGrayScale() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),R.raw.jack, options);
        Mat sourceImage = new Mat(bMap.getWidth(), bMap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bMap, sourceImage);
        this.s = sourceImage.size();
        Mat imgGrayScale = new Mat((int)s.height, (int)s.width, CvType.CV_8UC1);
        Imgproc.cvtColor(sourceImage, imgGrayScale, Imgproc.COLOR_BGR2GRAY);
        return imgGrayScale;
    }
    public void getRgb(Bitmap myImage){
        this.imgOrg = myImage;
    }

    public float[] getVertices(){
        final String TAG = "VertexJack";
        int row = 0;
        int col = 0;

        this.colors = new float[25000][4];
        float[] vertices = new float[300000];
        for(int i=0; i < 300000; i+=12){
            int color =imgOrg.getPixel(col,row);
//            Log.e(TAG,":color-"+color);
            this.colors[i/12][0] = (float)Color.red(color)/255;
            this.colors[i/12][1] = (float)Color.green(color)/255;
            this.colors[i/12][2] = (float)Color.blue(color)/255;
            this.colors[i/12][3] = 1.0f;
            row+=2;
            vertices[i] = ((float)col/300)-0.5f;
            vertices[i+1] = ((float)-row/300)+0.67f;
            vertices[i+2] = ((float)imgMat.get(row,col)[0]/255)-0.5f;
            col+=2;
            vertices[i+3] = ((float)col/300)-0.5f;
            vertices[i+4] = ((float)-row/300)+0.67f;
            vertices[i+5] = ((float)imgMat.get(row,col)[0]/255)-0.5f;
            row-=2;
            col-=2;
            vertices[i+6] = ((float)col/300)-0.5f;
            vertices[i+7] = ((float)-row/300)+0.67f;
            vertices[i+8] = ((float)imgMat.get(row,col)[0]/255)-0.5f;
            col+=2;

            vertices[i+9] = ((float)col/300)-0.5f;
            vertices[i+10] = ((float)-row/300)+0.67f;
            vertices[i+11] = ((float)imgMat.get(row,col)[0]/255)-0.5f;

            if(col>=298){
                col=0;
                row+=2;
            }

        }
    return vertices;
    }




}
