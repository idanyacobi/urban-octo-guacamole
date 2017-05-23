package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Scalar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import java.util.List;

import static com.example.idan.urban_octo_guacamole.Settings.ENV_SIZE;
import static com.example.idan.urban_octo_guacamole.Settings.IMAGE_CVTYPE;
import static com.example.idan.urban_octo_guacamole.Settings.K_NEAREST;
import static com.example.idan.urban_octo_guacamole.Settings.PATCH_SIZE;
import static org.opencv.core.Core.add;
import static org.opencv.core.Core.divide;
import static org.opencv.core.Core.multiply;

public class LogicActivity extends AppCompatActivity {

    public static final Float MAX_FLOAT_NUM = Float.POSITIVE_INFINITY;
    private dbHelper dbh;
    private ImageView imgView;

    private inputHandler inputHandler;
    private Mat imgMat;
    DatabaseAccess databaseAccess;
    private Comparator<? super DepthPatch> compByName;
    ArrayList<DepthPatch> depth_patches;
    Bitmap depthbmp;
    Bitmap orgBmp;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        // Connect to image view.
        imgView = (ImageView) this.findViewById(R.id.faceImage);
        //Open image.
        InputStream stream = getResources().openRawResource( R.raw.face56 );
        orgBmp = BitmapFactory.decodeStream(stream);
        imgMat = getMatFromBitmap(orgBmp);

        inputHandler = new inputHandler();

        // split the input img into patches
        HashMap<Integer, HashMap<Integer, Mat>> img_descriptors = inputHandler.splitToPatches(imgMat);

        depth_patches = processDescriptors(img_descriptors);

        Mat depth = createDepthMap(depth_patches);

        depthbmp = utils.mat2bmp(depth);

        imgView.setImageBitmap(depthbmp);

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

    private ArrayList<DepthPatch> processDescriptors(HashMap<Integer, HashMap<Integer, Mat>> img_descriptors) {
        // create an array list to store all the depth patches
        ArrayList<DepthPatch> dps = new ArrayList<>();

        int i = 0;
        //run on all the patches
        for (Map.Entry<Integer, HashMap<Integer, Mat>> col2hashmap : img_descriptors.entrySet()) {
            for (Map.Entry<Integer, Mat> row2descriptor : col2hashmap.getValue().entrySet()) {
                Mat input_descriptor = row2descriptor.getValue();
                Integer input_col = col2hashmap.getKey();
                Integer input_row = row2descriptor.getKey();

                // send the col, row to get all the descriptors in the environment
                List<Descriptor> env_descs = getPatchEnvDescs(input_col, input_row);

                Descriptor min_desc = new Descriptor();
                float min_dist = MAX_FLOAT_NUM;
                Map<Descriptor,Float> k_nearest = new HashMap<>();

                // run on all the descriptor and save the one with the smallest distance
                for (Descriptor db_desc:env_descs) {
                    float dis_res = db_desc.distanceFrom(input_descriptor);
                    k_nearest.put(db_desc, dis_res);
                }
                //Sorting All results.
                k_nearest = sortByValue(k_nearest);

                Mat avg = Mat.zeros(PATCH_SIZE, PATCH_SIZE, CvType.CV_32F);
                int k_count = 0;

                for(Map.Entry<Descriptor,Float> desc2float : k_nearest.entrySet()){
                    Descriptor curr_desc = desc2float.getKey();
                    // get the patch depth map
                    DepthPatch dp = getDepthPatch(curr_desc.getID(), curr_desc.getCol(), curr_desc.getRow());
                    Mat float_mat = Mat.zeros(PATCH_SIZE, PATCH_SIZE, CvType.CV_32F);
                    dp.getDepthPatch().convertTo(float_mat,CvType.CV_32F);
                    add(float_mat,avg, avg);

                    k_count++;
                    if (k_count >= K_NEAREST) break;
                }
                Scalar s = new Scalar((double)1/Settings.K_NEAREST);
                multiply(avg,s, avg);
                avg.convertTo(avg,Settings.IMAGE_CVTYPE);
                DepthPatch res_dp = new DepthPatch(-1, input_col, input_row, avg);
                dps.add(res_dp);
                Log.i("Queries", String.format("done processing %d queries", ++i));
            }
        }
        Log.i("Queries", "done processing all the queries");

        return dps;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
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

        String query = String.format("select * from descriptors where col > %d and col < %d and row > %d and row < %d;", lower_col, upper_col, lower_row, upper_row);
        List<Descriptor> query_res = databaseAccess.exeDescriptorsQuery(query);

        return query_res;
    }

    private Mat getMatFromBitmap(Bitmap bmp){
        Mat sourceImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_16UC1);
        Utils.bitmapToMat(bmp, sourceImage);
        return sourceImage;
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        depthbmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        intent.putExtra("bitmapbytes",bytes);

        ByteArrayOutputStream streamOrg = new ByteArrayOutputStream();
        orgBmp.compress(Bitmap.CompressFormat.JPEG, 100, streamOrg);
        byte[] bytesOrg = streamOrg.toByteArray();
        intent.putExtra("bitmapbytesOrg",bytesOrg);

        startActivity(intent);
    }

    public Comparator<? super DepthPatch> getCompByName() {
        return compByName;
    }

    public ArrayList<Float> MatOftoList(Mat input_mat) {
        ArrayList<Float> res_list = new ArrayList<>();
        for(int col =0; col < input_mat.cols(); col++) {
            for (int row = 0; row < input_mat.rows(); row++) {
                res_list.add((float) input_mat.get(row, col)[0]);
            }
        }

        return res_list;
    }
}
