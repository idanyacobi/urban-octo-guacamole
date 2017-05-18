package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

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
        Mat depth = Mat.zeros(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);
        for (DepthPatch dp : _patches) {
            if ((dp.getCol() % Settings.PATCH_SIZE != 0) || (dp.getRow() % Settings.PATCH_SIZE != 0)){
                continue;
            }
            dp.getDepthPatch().copyTo(
                    depth
                    .colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                    .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE))
            );
        }
        return depth;
    }

    public Mat newConstruct() {

        Mat depth = Mat.zeros(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);
        Mat denom = Mat.zeros(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);

        for (DepthPatch dp : _patches) {
            Mat sMat = depth.colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                    .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE));
            Mat sMatDenom = denom.colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                    .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE));

            Core.add(sMat, dp.getDepthPatch(), sMat);

            Core.add(sMatDenom, new Scalar(1), sMatDenom);
        }

        Core.divide(depth, denom, depth);
        return depth;
    }
}
