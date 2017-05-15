package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Mat;


class DepthPatch {

    //private variables
    private int _id;
    private int _col;
    private int _row;
    private Mat _dp;

    // Empty constructor
    DepthPatch(){

    }
    // constructor
    public DepthPatch(int id, int col, int row, Mat dp){
        this._id = id;
        this._col = col;
        this._row = row;
        this._dp = dp;
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

    // getting depth patch
    public Mat getDepthPatch(){
        return this._dp;
    }

    // setting depth patch
    void setDP(Mat dp){
        this._dp = dp;
    }

}
