package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idan on 15/05/2017.
 */

public class DepthConstructor {

    ArrayList<DepthPatch> _patches;

    public DepthConstructor(ArrayList patches) {
        _patches = patches;
    }

    public Mat Construct() {
        Mat depth = new Mat(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);
        for (DepthPatch dp : _patches) {
            dp.getDepthPatch().copyTo(
                    depth
                    .colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                    .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE))
            );
        }
        return depth;
    }
}
