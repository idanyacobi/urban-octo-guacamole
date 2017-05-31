package com.example.idan.urban_octo_guacamole;



import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "MainActivity";

    ImageView mImageView;
    CameraImage cam_img;
    Bitmap mImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        cam_img = new CameraImage(this);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        //Get the image from gallery.
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != intent) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mImageBitmap = BitmapFactory.decodeFile(picturePath);
        }
        //Get image from camera.
        if(requestCode == CameraImage.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mImageBitmap = cam_img.setPic(mImageView);
        }else if(requestCode == CameraImage.REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
        }
        mImageView.setImageBitmap(mImageBitmap);
    }

    public void imageFromCamera(View view){
        //Open Camera and return to onActivityResult.
        cam_img.dispatchTakePictureIntent();

    }

    public void imageFromGallery(View view){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void faceDetection(View view){
        SparseArray<Face> mFaces;
        FaceDetector detector = new FaceDetector.Builder(this).setTrackingEnabled(true).setLandmarkType(FaceDetector.ALL_LANDMARKS).setMode(FaceDetector.ACCURATE_MODE).build();
        if (!detector.isOperational()) {
            Log.i(TAG,"FaceDetector Failed");
        } else {
            Frame frame = new Frame.Builder().setBitmap(mImageBitmap).build();
            mFaces = detector.detect(frame);
            if(mFaces.size() != 0){
                Rectangle rect = drawFaceBorder(mFaces);
                Log.d(TAG,""+rect.x+" "+rect.y+" " + (rect.width+rect.x)+  " " + (rect.height+rect.y) +" mBitMapWidth:" +mImageBitmap.getWidth()+ " mBitMapHeight:" + mImageBitmap.getHeight());
                mImageBitmap = Bitmap.createBitmap(mImageBitmap,rect.x, rect.y, Math.min(rect.width,mImageBitmap.getWidth()), Math.min(rect.height,mImageBitmap.getHeight()));
                mImageView.setImageBitmap(mImageBitmap);
                detector.release();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find face", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    public void imageFromDataBase(View view){
        InputStream stream = getResources().openRawResource( R.raw.face2 );
        mImageBitmap = BitmapFactory.decodeStream(stream);
        mImageView.setImageBitmap(mImageBitmap);
    }

        public void nextActivity(View view){

        Intent intent = new Intent(this, LogicActivity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("Debug",false);
        intent.putExtra("Face",byteArray);
        startActivity(intent);
    }

    private Rectangle drawFaceBorder(SparseArray<Face> mFaces) {

        Rectangle rect = new Rectangle();
        Face face = mFaces.valueAt(0);
        rect.setBounds(Math.max((int)face.getPosition().x,0),Math.max((int)face.getPosition().y,0),(int)face.getWidth(),(int)face.getHeight());

        return rect;

    }
}


//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.net.Uri;
//import android.os.Environment;
//import android.provider.*;
//import android.provider.Settings;
//import android.support.constraint.solver.widgets.Rectangle;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.face.Face;
//import com.google.android.gms.vision.face.FaceDetector;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.JavaCameraView;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Serializable;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import static android.content.ContentValues.TAG;
//
//
//public class MainActivity extends AppCompatActivity{
//
//    private static final String TAG = "MainActivity";
////    JavaCameraView javaCameraView;
////    Mat mRgba,imgGray,imgCanny;
//    private Bitmap mImageBitmap;
//    private String mCurrentPhotoPath;
//    private ImageView mImageView;
//
//
//    private FaceOverlayView mFaceOverlayView;
//    static final int REQUEST_TAKE_PHOTO = 1;
//
//    static {
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "open cv not loaded");
//        }
//        else {
//            Log.d(TAG, "open cv loaded");
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        dispatchTakePictureIntent();
//        setContentView(R.layout.activity_main);
//        mFaceOverlayView = (FaceOverlayView) findViewById( R.id.face_overlay );
//        InputStream stream = getResources().openRawResource( R.raw.face2 );
//        Bitmap bitmap = BitmapFactory.decodeStream(stream);
////        dbh = initDB();
////        dbh.getAllDescriptors();
//    }
//
//    public void nextActivity(View view){
//
//        Intent intent = new Intent(this, LogicActivity.class);
//        mImageBitmap = mFaceOverlayView.getImage();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
////        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        intent.putExtra("Debug",false);
//        intent.putExtra("Face",byteArray);
//        startActivity(intent);
//    }
//
//    static final int RESULT_LOAD_IMAGE = 1;
//    public void pickImage(View view) {
//        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, RESULT_LOAD_IMAGE);
//    }
//
//    public void debugMode(View view){
//        Intent intent = new Intent(this, LogicActivity.class);
//        intent.putExtra("Debug",true);
//        startActivity(intent);
//    }
//
//
//    public void dispatchTakePictureIntent(View view) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
////                Uri photoURI = FileProvider.getUriForFile(this,
////                        "com.example.android.fileprovider",
////                        photoFile);
//                Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }
////    public void dispatchTakePictureIntent(View view) {
////        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
////            // Create the File where the photo should go
////            File photoFile = null;
////            try {
////                photoFile = createImageFile();
////            } catch (IOException ex) {
////                // Error occurred while creating the File
////                Log.i(TAG, "IOException");
////            }
////            // Continue only if the File was successfully created
////            if (photoFile != null) {
////                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
////                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
////            }
////        }
////    }
//
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  // prefix
//                ".jpg",         // suffix
//                storageDir      // directory
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//
//
//    public void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
////            galleryAddPic();
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
////            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//            faceDetection();
////            if(mFaceOverlayView.setBitmap(BitmapFactory.decodeFile(picturePath))==0){
//
//
////            };
////            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//        }
//
//
//
//
//
//
//
//
//
//
////        if (resultCode != RESULT_CANCELED) {
////            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
////                try {
////                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
//////                mImageView.setImageBitmap(mImageBitmap);
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
//
////        }
//    }
//
//
//    public void faceDetection(){
//        SparseArray<Face> mFaces;
//        FaceDetector detector = new FaceDetector.Builder(this)
//                .setTrackingEnabled(true)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                .setMode(FaceDetector.ACCURATE_MODE)
//                .build();
//
//        if (!detector.isOperational()) {
//            //Handle contingency
//        } else {
//            mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//            Frame frame = new Frame.Builder().setBitmap(mImageBitmap).build();
//            mFaces = detector.detect(frame);
//            if(mFaces.size() != 0){
//                Rectangle rect = drawFaceBox(mFaces,1);
//                Log.d(TAG,""+rect.x+" "+rect.y+" " + (rect.width+rect.x)+  " " + (rect.height+rect.y) +" mBitMapWidth:" +mImageBitmap.getWidth()+ " mBitMapHeight:" + mImageBitmap.getHeight());
//                mImageBitmap = Bitmap.createBitmap(mImageBitmap,rect.x, rect.y, Math.min(rect.width,mImageBitmap.getWidth()), Math.min(rect.height,mImageBitmap.getHeight()));
//                detector.release();
//            }else{
//                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find face", Toast.LENGTH_SHORT);
//                toast.show();
//                View view = findViewById(R.id.button2);
//                pickImage(view);
//                faceDetection();
//            }
//
//        }
//    }
//
//    private Rectangle drawFaceBox(SparseArray<Face> mFaces, double scale) {
//        //This should be defined as a member variable rather than
//        //being created on each onDraw request, but left here for
//        //emphasis.
//        Paint paint = new Paint();
//        paint.setColor(Color.GREEN);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5);
//        Rectangle rect = new Rectangle();
//        float left = 0;
//        float top = 0;
//        float right = 0;
//        float bottom = 0;
//
//        for( int i = 0; i < mFaces.size(); i++ ) {
//            Face face = mFaces.valueAt(i);
//
//            left = (float) ( face.getPosition().x * scale );
//            top = (float) ( face.getPosition().y * scale );
//            right = (float) scale * ( face.getPosition().x + face.getWidth() );
//            bottom = (float) scale * ( face.getPosition().y + face.getHeight() );
//
////            canvas.drawRect( left, top, right, bottom, paint );
//            rect.setBounds(Math.max((int)face.getPosition().x,0),Math.max((int)face.getPosition().y,0),(int)face.getWidth(),(int)face.getHeight());
//        }
//        return rect;
//
//    }
//
//
//
////    @Override
////    protected void onPause(){
////        super.onPause();
////        if(javaCameraView != null)
////            javaCameraView.disableView();;
////
////    }
////
////    @Override
////    protected void onDestroy(){
////        super.onDestroy();
////        if(javaCameraView != null)
////            javaCameraView.disableView();
////    }
////    @Override
////    protected void onResume(){
////        super.onResume();
////        if (!OpenCVLoader.initDebug()) {
////            Log.d(TAG, "open cv not loaded");
////            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this, nLoadCallBack);
////        }
////        else {
////            Log.d(TAG, "open cv loaded");
////            nLoadCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
////
////        }
////    }
////
////    @Override
////    public void onCameraViewStarted(int width, int height) {
////        mRgba = new Mat(height, width, CvType.CV_8UC4);
////        imgGray = new Mat(height, width, CvType.CV_8UC1);
////        imgCanny = new Mat(height, width, CvType.CV_8UC1);
////    }
////
////    @Override
////    public void onCameraViewStopped() {
////        mRgba.release();
////    }
////
////    @Override
////    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
////        mRgba = inputFrame.rgba();
////        Imgproc.cvtColor(mRgba, imgGray, Imgproc.COLOR_RGB2GRAY);
////        Imgproc.Canny(imgGray,imgCanny,50,150);
////        return imgCanny;
////    }
//
//
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
////            Bundle extras = data.getExtras();
////            Bitmap imageBitmap = (Bitmap) extras.get("data");
////            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
////            mImageView.setImageBitmap(imageBitmap);
////        }
////    }
//
//    //    private FaceOverlayView mFaceOverlayView;
////    BaseLoaderCallback nLoadCallBack = new BaseLoaderCallback(this) {
////        @Override
////        public void onManagerConnected(int status) {
////            switch(status){
////                case BaseLoaderCallback.SUCCESS:{
////                    javaCameraView.enableView();
////                    break;
////                }
////                default:
////                    super.onManagerConnected(status);
////                    break;
////            }
////
////
////        }
////    };
//
//
//
////    Button button_take_img;
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
//////        javaCameraView = (JavaCameraView)findViewById(R.id.java_camera_view);
//////        javaCameraView.setVisibility(SurfaceView.VISIBLE);
//////        javaCameraView.setCvCameraViewListener(this);
//////
//////        // take a picture button
//////        // button_take_img = (Button) findViewById(R.id.button);
//////
//////        inputHandler ih = new inputHandler(this);
//////        Mat img = ih.getImg();
//////        ih.splitToPatches(img);
////
////        mFaceOverlayView = (FaceOverlayView) findViewById( R.id.face_overlay );
////
////        InputStream stream = getResources().openRawResource( R.raw.face );
////        Bitmap bitmap = BitmapFactory.decodeStream(stream);
////
////        mFaceOverlayView.setBitmap(bitmap);
////
////    }
//}
