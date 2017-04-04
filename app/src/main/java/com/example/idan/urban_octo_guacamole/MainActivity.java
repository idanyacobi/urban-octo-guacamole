package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "open cv not loaded");
        }
        else {
            Log.d(TAG, "open cv loaded");
        }
    }

    Button button_take_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_take_img = (Button) findViewById(R.id.button);
        inputHandler ih = new inputHandler(this);
        ih.getImg();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(imageBitmap);
        }
    }
}
