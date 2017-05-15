package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;



public class MatSerializer {

    private static final int WIDTH = Settings.PATCH_WIDTH;
    private static final int HEIGHT = Settings.PATCH_HEIGHT;

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
        String[] rows = matStr.split(";");

        for (int i=0; i < HEIGHT; i++) {
            String[] vals =  rows[i].split(",");
            for (int j=0; j < WIDTH; j++) {
                m.put(j, i, Float.parseFloat(vals[j]));
            }
        }

        return m;
    }


}
