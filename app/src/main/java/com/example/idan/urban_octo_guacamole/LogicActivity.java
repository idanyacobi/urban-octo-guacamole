package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class LogicActivity extends AppCompatActivity {

    private dbHelper dbh;
    private ImageView imgView;
    private inputHandler inputHandler;
    private Mat imgMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        imgView = (ImageView)this.findViewById(R.id.faceImage);
        byte[] byteArray = getIntent().getByteArrayExtra("Face");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgView.setImageBitmap(bmp);
        inputHandler = new inputHandler();
        getMatFromBitmap(bmp);
        inputHandler.splitToPatches(imgMat);
        dbh = new dbHelper(this);

    }

    private void getMatFromBitmap(Bitmap bmp){
        Mat sourceImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, sourceImage);
        imgMat = sourceImage;
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    public dbHelper initDB() {
        dbh = new dbHelper(this);

        try {
            dbh.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            dbh.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        return dbh;
    }
}
