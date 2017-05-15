package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LogicActivity extends AppCompatActivity {

    private ImageView imgView;
    private inputHandler inputHandler;
    private Mat imgMat;
    private dbHelper dbh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        if(!getIntent().getBooleanExtra("Debug",true)) {
            imgView = (ImageView) this.findViewById(R.id.faceImage);
            InputStream stream = getResources().openRawResource( R.raw.face2 );
            Bitmap bmp = BitmapFactory.decodeStream(stream);
//            byte[] byteArray = getIntent().getByteArrayExtra("Face");
//            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgView.setImageBitmap(bmp);
            imgMat = getMatFromBitmap(bmp);

            inputHandler = new inputHandler();
            // Database initialization
            dbh = initDB();
            dbh.getAllDescriptors();
        }

        // split the input img into patches
        HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors = inputHandler.splitToPatches(imgMat);

        processDescriptors(img_descriptors);
    }

    private void processDescriptors(HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors) {
        //run on all the patches
        for (Map.Entry<Integer, HashMap<Integer, MatOfFloat>> col_to_row : img_descriptors.entrySet()) {
            for (Map.Entry<Integer, MatOfFloat> row_descriptor : col_to_row.getValue().entrySet()) {
                System.out.println("here");
                // send the col, row
                findPatchEnv(col_to_row.getKey(),row_descriptor.getKey());
            }
        }
    }

    private void findPatchEnv(Integer col, Integer row) {

    }

    private Mat getMatFromBitmap(Bitmap bmp){
        Mat sourceImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, sourceImage);
        return sourceImage;
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

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
