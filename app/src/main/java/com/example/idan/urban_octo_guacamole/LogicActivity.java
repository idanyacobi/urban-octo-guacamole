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
import org.opencv.core.MatOfFloat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import static com.example.idan.urban_octo_guacamole.Settings.ENV_SIZE;
import static com.example.idan.urban_octo_guacamole.Settings.PATCH_SIZE;

public class LogicActivity extends AppCompatActivity {

    public static final Float MAX_FLOAT_NUM = Float.POSITIVE_INFINITY;
    private dbHelper dbh;
    private ImageView imgView;
    private inputHandler inputHandler;
    private Mat imgMat;
    DatabaseAccess databaseAccess;
    private Comparator<? super DepthPatch> compByName;
    ArrayList<DepthPatch> depth_patches;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        imgView = (ImageView) this.findViewById(R.id.faceImage);
        InputStream stream = getResources().openRawResource( R.raw.face22 );
        Bitmap bmp = BitmapFactory.decodeStream(stream);
        imgView.setImageBitmap(bmp);
        imgMat = getMatFromBitmap(bmp);

        inputHandler = new inputHandler();

//        if(!getIntent().getBooleanExtra("Debug",true)) {
//            imgView = (ImageView) this.findViewById(R.id.faceImage);
//            byte[] byteArray = getIntent().getByteArrayExtra("Face");
//            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            imgView.setImageBitmap(bmp);
//            inputHandler = new inputHandler();
//            getMatFromBitmap(bmp);
//        }
//        dbh = (dbHelper)getIntent().getSerializableExtra("DB");

        // split the input img into patches
        HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors = inputHandler.splitToPatches(imgMat);

        depth_patches = processDescriptors(img_descriptors);

        Mat depth = createDepthMap(depth_patches);

        Bitmap depthbmp = utils.mat2bmp(depth);

        imgView.setImageBitmap(depthbmp);

        /////
//        for (DepthPatch dp: depth_patches) {
//            Bitmap dpbmp = utils.mat2bmp(dp.getDepthPatch());
//            imgView.setImageBitmap(dpbmp);
//        }
        /////

        databaseAccess.close();
    }

    public void showDepthPatch(View view){
        DepthPatch d = depth_patches.get(i);
        Bitmap dpbmp = utils.mat2bmp(d.getDepthPatch());
        imgView.setImageBitmap(dpbmp);
        i++;
    }

    private Mat createDepthMap(ArrayList<DepthPatch> depth_patches) {
//        Collections.sort(depth_patches, getCompByName());
        DepthConstructor dc = new DepthConstructor(depth_patches);
        return dc.Construct();
    }

    private ArrayList<DepthPatch> processDescriptors(HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors) {
        // create an array list to store all the depth patches
        ArrayList<DepthPatch> dps = new ArrayList<>();

        int i = 0;
        //run on all the patches
        for (Map.Entry<Integer, HashMap<Integer, MatOfFloat>> col2hashmap : img_descriptors.entrySet()) {
            for (Map.Entry<Integer, MatOfFloat> row2descriptor : col2hashmap.getValue().entrySet()) {
                MatOfFloat input_descriptor = row2descriptor.getValue();
                Integer input_col = col2hashmap.getKey();
                Integer input_row = row2descriptor.getKey();

                // send the col, row to get all the descriptors in the environment
                List<Descriptor> env_descs = getPatchEnvDescs(input_col, input_row);

                Descriptor min_desc = new Descriptor();
                float min_dist = MAX_FLOAT_NUM;

                // run on all the descriptor and save the one with the smallest distance
                for (Descriptor db_desc:env_descs) {
                    float dis_res = db_desc.distanceFrom(input_descriptor);
                    if (dis_res < min_dist) {

                        min_desc.setID(db_desc.getID());
                        min_desc.setCol(db_desc.getCol());
                        min_desc.setRow(db_desc.getRow());
                        min_desc.setDesc(db_desc.getDescriptor());

                        min_dist = dis_res;
                    }
                }
                // get the patch depth map
                DepthPatch dp = getDepthPatch(min_desc.getID(), min_desc.getCol(), min_desc.getRow());
                DepthPatch res_dp = new DepthPatch(-1, input_col, input_row, dp.getDepthPatch());
                dps.add(res_dp);
                System.out.println(String.format("done processing %d queries", i++));
            }
        }

//        for(int f = 0; f < 128; f += 32){
//            for (int j = 0; j < 128; j += 32){
//
//                dps.add(getDepthPatch(2,f,j));
//            }
//        }

        return dps;
    }

    private DepthPatch getDepthPatch(int id, int col, int row) {
        String query = String.format("select * from depth_patches where id == %d and col == %d and row == %d;", id, col, row);
        DepthPatch query_res = databaseAccess.exeDepthPatchesQuery(query);

        return query_res;
    }

    private List<Descriptor> getPatchEnvDescs(Integer col, Integer row) {
        // create an array to store all the relevant descriptors
        Integer upper_col = col + ENV_SIZE + PATCH_SIZE;
        Integer lower_col = col - ENV_SIZE;
        Integer upper_row = row + ENV_SIZE + PATCH_SIZE;
        Integer lower_row = row - ENV_SIZE;

//        String query = String.format("select * from descriptors where col > %d and col < %d and row > %d and row < %d;", lower_col, upper_col, lower_row, upper_row);
        String query = String.format("select * from descriptors where col == %d and row == %d", col, row);
        List<Descriptor> query_res = databaseAccess.exeDescriptorsQuery(query);

        return query_res;
    }

    private Mat getMatFromBitmap(Bitmap bmp){
        Mat sourceImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, sourceImage);
        return sourceImage;
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    public Comparator<? super DepthPatch> getCompByName() {
        return compByName;
    }
}
