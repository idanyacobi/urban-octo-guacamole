package com.example.idan.urban_octo_guacamole;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;

class Descriptor {
    //private variables
    private int _id;
    private int _col;
    private int _row;
    private MatOfFloat _desc;

    // Empty constructor
    Descriptor(){

    }

    // constructor
    public Descriptor(int id, int col, int row, String desc){
        this._id = id;
        this._col = col;
        this._row = row;
        this._desc =  parseDescriptor(desc);
    }

    private MatOfFloat parseDescriptor(String desc) {
        MatOfFloat d = new MatOfFloat(Settings.DESC_LENGTH);
        JSONObject reader = null;
        try {
            reader = new JSONObject(desc);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return d;
        }
        return d;
    }

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
    void setDesc(String desc){
        this._desc = parseDescriptor(desc);
    }

    // TODO
    public float distanceFrom(Descriptor other){
        return 0;
    }

}
