package com.example.idan.urban_octo_guacamole;


import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class MatSerializer {

    private static final int WIDTH = Settings.PATCH_SIZE;
    private static final int HEIGHT = Settings.PATCH_SIZE;

    public static Mat string2Mat(String matStr) {
        Mat m = new Mat();
        String[] rows = matStr.split(";");

        for (int i=0; i < HEIGHT; i++) {
            String[] vals =  rows[i].split(",");
            for (int j=0; j < WIDTH; j++) {
                m.put(j, i, Float.parseFloat(vals[j]));
            }
        }

        return m;
    }

    public static MatOfFloat string2MatOfFloat(String matStr) {
        MatOfFloat m = new MatOfFloat();
        Float[] desc = new Float[324];

        String[] floats_list_str = matStr.replace("{\"patch_descriptor\":\"[", "").replace("]\"}", "").split(",");

        List<Float> floats_list = new ArrayList<Float>();

        for (String aFloats_list_str : floats_list_str) {
            floats_list.add(Float.parseFloat(aFloats_list_str));
        }

        m.fromList(floats_list);

        return m;
    }


}
