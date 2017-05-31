package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


class DepthConstructor {

    private ArrayList<DepthPatch> _patches;
    Imgproc ip = new Imgproc();

    DepthConstructor(ArrayList patches) {
        _patches = patches;
    }

    Mat Construct() {
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

        ip.pyrMeanShiftFiltering(depth, depth, 4, 4);

        return depth;
    }
}