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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        imgView = (ImageView) this.findViewById(R.id.faceImage);
        InputStream stream = getResources().openRawResource( R.raw.face2 );
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

        ArrayList<DepthPatch> depth_patches = processDescriptors(img_descriptors);

        createDepthMap(depth_patches);

        databaseAccess.close();
    }

    private void createDepthMap(ArrayList<DepthPatch> depth_patches) {
//        Collections.sort(depth_patches, getCompByName());
        System.out.println("here");
    }

    private ArrayList<DepthPatch> processDescriptors(HashMap<Integer, HashMap<Integer, MatOfFloat>> img_descriptors) {
        // create an array list to store all the depth patches
        ArrayList<DepthPatch> dps = new ArrayList<>();

        int i = 0;
        //run on all the patches
        for (Map.Entry<Integer, HashMap<Integer, MatOfFloat>> col2hashmap : img_descriptors.entrySet()) {
            for (Map.Entry<Integer, MatOfFloat> row2descriptor : col2hashmap.getValue().entrySet()) {
                MatOfFloat input_desc = row2descriptor.getValue();

                // send the col, row to get all the descruptors in the environment
                List<Descriptor> env_descs = getPatchEnvDescs(col2hashmap.getKey(), row2descriptor.getKey());

                Descriptor min_desc = new Descriptor();
                float min_dist = MAX_FLOAT_NUM;

                // run on all the descriptor and save the one with the smallest distance
                for (Descriptor db_desc:env_descs) {
                    float dis_res = db_desc.distanceFrom(input_desc);
                    if (dis_res < min_dist) {
                        min_desc.setID(db_desc.getID());
                        min_desc.setCol(db_desc.getCol());
                        min_desc.setRow(db_desc.getRow());
                        min_desc.setDesc(db_desc.getDescriptor());

                        min_dist = dis_res;
                    }
                }
                // get the patch depth map
                dps.add(getDepthPatch(min_desc.getID(), min_desc.getCol(), min_desc.getRow()));
                System.out.println(String.format("done processing %d queries", i++));
            }
        }

        return dps;
    }

    private DepthPatch getDepthPatch(int id, int col, int row) {
        String query = String.format("select * from depth_patches where id == %d and col == %d and row == %d;", id, col, row);
        DepthPatch query_res = databaseAccess.exeDepthPatchesQuery(query);

        return query_res;
    }

    private List<Descriptor> getPatchEnvDescs(Integer col, Integer row) {
        // create an array to store all the relevant descriptors
        ArrayList<Descriptor> res_descs = new ArrayList<>();
        Integer upper_col = col + ENV_SIZE + PATCH_SIZE;
        Integer lower_col = col - ENV_SIZE;
        Integer upper_row = row + ENV_SIZE + PATCH_SIZE;
        Integer lower_row = row - ENV_SIZE;

        String query = String.format("select * from descriptors where col > %d and col < %d and row > %d and row < %d;", lower_col, upper_col, lower_row, upper_row);
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
