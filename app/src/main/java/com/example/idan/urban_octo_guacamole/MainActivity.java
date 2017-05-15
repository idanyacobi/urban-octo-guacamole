package com.example.idan.urban_octo_guacamole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.*;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.face.FaceDetector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
//    JavaCameraView javaCameraView;
//    Mat mRgba,imgGray,imgCanny;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;


    private FaceOverlayView mFaceOverlayView;
    static final int REQUEST_TAKE_PHOTO = 1;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "open cv not loaded");
        }
        else {
            Log.d(TAG, "open cv loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dispatchTakePictureIntent();
        setContentView(R.layout.activity_main);
        mFaceOverlayView = (FaceOverlayView) findViewById( R.id.face_overlay );
        InputStream stream = getResources().openRawResource( R.raw.face2 );
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
//        dbh = initDB();
//        dbh.getAllDescriptors();
    }

    public void nextActivity(View view){

        Intent intent = new Intent(this, LogicActivity.class);
        mImageBitmap = mFaceOverlayView.getImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("Debug",false);
        intent.putExtra("Face",byteArray);
        startActivity(intent);
    }

    static final int RESULT_LOAD_IMAGE = 1;
    public void pickImage(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void debugMode(View view){
        Intent intent = new Intent(this, LogicActivity.class);
        intent.putExtra("Debug",true);
        startActivity(intent);
    }

    public void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public void galleryAddPic(View view) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
//            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            if(mFaceOverlayView.setBitmap(BitmapFactory.decodeFile(picturePath))==0){
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find face", Toast.LENGTH_SHORT);
                toast.show();
                View view = findViewById(R.id.button2);
                pickImage(view);
            };
//            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }










//        if (resultCode != RESULT_CANCELED) {
//            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//                try {
//                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
////                mImageView.setImageBitmap(mImageBitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//        }
    }



//    @Override
//    protected void onPause(){
//        super.onPause();
//        if(javaCameraView != null)
//            javaCameraView.disableView();;
//
//    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        if(javaCameraView != null)
//            javaCameraView.disableView();
//    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "open cv not loaded");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this, nLoadCallBack);
//        }
//        else {
//            Log.d(TAG, "open cv loaded");
//            nLoadCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//
//        }
//    }
//
//    @Override
//    public void onCameraViewStarted(int width, int height) {
//        mRgba = new Mat(height, width, CvType.CV_8UC4);
//        imgGray = new Mat(height, width, CvType.CV_8UC1);
//        imgCanny = new Mat(height, width, CvType.CV_8UC1);
//    }
//
//    @Override
//    public void onCameraViewStopped() {
//        mRgba.release();
//    }
//
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        mRgba = inputFrame.rgba();
//        Imgproc.cvtColor(mRgba, imgGray, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.Canny(imgGray,imgCanny,50,150);
//        return imgCanny;
//    }


//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }

    //    private FaceOverlayView mFaceOverlayView;
//    BaseLoaderCallback nLoadCallBack = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch(status){
//                case BaseLoaderCallback.SUCCESS:{
//                    javaCameraView.enableView();
//                    break;
//                }
//                default:
//                    super.onManagerConnected(status);
//                    break;
//            }
//
//
//        }
//    };



//    Button button_take_img;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        javaCameraView = (JavaCameraView)findViewById(R.id.java_camera_view);
////        javaCameraView.setVisibility(SurfaceView.VISIBLE);
////        javaCameraView.setCvCameraViewListener(this);
////
////        // take a picture button
////        // button_take_img = (Button) findViewById(R.id.button);
////
////        inputHandler ih = new inputHandler(this);
////        Mat img = ih.getImg();
////        ih.splitToPatches(img);
//
//        mFaceOverlayView = (FaceOverlayView) findViewById( R.id.face_overlay );
//
//        InputStream stream = getResources().openRawResource( R.raw.face );
//        Bitmap bitmap = BitmapFactory.decodeStream(stream);
//
//        mFaceOverlayView.setBitmap(bitmap);
//
//    }
}
