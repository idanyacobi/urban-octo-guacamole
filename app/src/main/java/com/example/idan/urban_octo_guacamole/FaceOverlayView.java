package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import static android.content.ContentValues.TAG;

/**
 * Created by Paul on 11/4/15.
 */
public class FaceOverlayView extends View {

    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;

    public FaceOverlayView(Context context) {
        this(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }
    final static int FAILURE_DETECT = 0;
    final static int SUCCESS_DETECT = 1;
    public int setBitmap( Bitmap bitmap ) {
//        mBitmap = bitmap;
        mBitmap = convert(bitmap, Bitmap.Config.RGB_565);
        FaceDetector detector = new FaceDetector.Builder( getContext() )
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        if (!detector.isOperational()) {
            //Handle contingency
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            if(mFaces.size() == 0){
                detector.release();
                return FAILURE_DETECT;
            }

        }
        logFaceData();
        invalidate();
        return SUCCESS_DETECT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if ((mBitmap != null) && (mFaces != null)) {
            if(mFaces.size()==0)
                return;
//            double scale = drawBitmap(canvas);
//            If we get unaligned image.
//            Matrix matrix = new Matrix();
//            matrix.postRotate(-90);
//            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            Rectangle rect = drawFaceBox(canvas, 1);
            Log.d(TAG,""+rect.x+" "+rect.y+" " + (rect.width+rect.x)+  " " + (rect.height+rect.y) +" mBitMapWidth:" +mBitmap.getWidth()+ " mBitMapHeight:" + mBitmap.getHeight());
            mBitmap = Bitmap.createBitmap(mBitmap,rect.x, rect.y, Math.min(rect.width,mBitmap.getWidth()), Math.min(rect.height,mBitmap.getHeight()));
            drawBitmap(canvas);
        }
    }

    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    private Rectangle drawFaceBox(Canvas canvas, double scale) {
        //This should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        Rectangle rect = new Rectangle();
        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;

        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);

            left = (float) ( face.getPosition().x * scale );
            top = (float) ( face.getPosition().y * scale );
            right = (float) scale * ( face.getPosition().x + face.getWidth() );
            bottom = (float) scale * ( face.getPosition().y + face.getHeight() );

//            canvas.drawRect( left, top, right, bottom, paint );
            rect.setBounds(Math.max((int)face.getPosition().x,0),Math.max((int)face.getPosition().y,0),(int)face.getWidth(),(int)face.getHeight());
        }
        return rect;

    }

    private void drawFaceLandmarks( Canvas canvas, double scale ) {
        Paint paint = new Paint();
        paint.setColor( Color.GREEN );
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeWidth( 5 );

        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);

            for ( Landmark landmark : face.getLandmarks() ) {
                int cx = (int) ( landmark.getPosition().x * scale );
                int cy = (int) ( landmark.getPosition().y * scale );
                canvas.drawCircle( cx, cy, 10, paint );
            }

        }
    }

    private void logFaceData() {
//        float smilingProbability;
//        float leftEyeOpenProbability;
//        float rightEyeOpenProbability;
        float eulerY;
        float eulerZ;
        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);

//            smilingProbability = face.getIsSmilingProbability();
//            leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
//            rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
            eulerY = face.getEulerY();
            eulerZ = face.getEulerZ();
//
//            Log.e( "Tuts+ Face Detection", "Smiling: " + smilingProbability );
//            Log.e( "Tuts+ Face Detection", "Left eye open: " + leftEyeOpenProbability );
//            Log.e( "Tuts+ Face Detection", "Right eye open: " + rightEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Euler Y: " + eulerY );
            Log.e( "Tuts+ Face Detection", "Euler Z: " + eulerZ );
        }
    }
}
