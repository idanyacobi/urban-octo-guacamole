package com.example.idan.urban_octo_guacamole;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.abs;

class Descriptor {
    //private variables
    private int _id;
    private int _col;
    private int _row;
    private MatOfFloat _desc;

    // Empty constructor
    Descriptor(){}

    // constructor
//    public Descriptor(int id, int col, int row, String desc){
//        this._id = id;
//        this._col = col;
//        this._row = row;
//        this._desc =  parseDescriptor(desc);
//    }

//    private MatOfFloat parseDescriptor(Mat desc) {
//        MatOfFloat d = new MatOfFloat(Settings.DESC_LENGTH);
//        JSONObject reader = null;
//        try {
//            reader = new JSONObject(desc);
//        } catch (JSONException e) {
//            System.out.println(e.getMessage());
//            return d;
//        }
//        return d;
//    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting ID
    void setID(int id){
        this._id = id;
    }

    // getting col
    public int getCol(){
        return this._col;
    }

    // setting col
    void setCol(int col){
        this._col = col;
    }

    public int getRow(){
        return this._row;
    }

    // setting row
    void setRow(int row){
        this._row = row;
    }

    // getting descriptor
    public MatOfFloat getDescriptor(){
        return this._desc;
    }

    // setting descriptor
    void setDesc(MatOfFloat desc){
        this._desc = desc;
    }

    float distanceFrom(MatOfFloat other_desc) {
        float min_val = Float.POSITIVE_INFINITY;

        for(int j = 0; j < this._desc.rows(); j++){
            float curr_distance = 0;
            for(int i=0; i<this._desc.rows(); i++) {
                curr_distance += abs(this._desc.get(i, 0)[0] - other_desc.get((i + j)%this._desc.rows(), 0)[0]);
            }
            if (curr_distance < min_val) {
                min_val = curr_distance;
            }
        }

        return min_val;
    }

}
