package com.example.idan.urban_octo_guacamole;

/**
 * Created by idan on 08/05/2017.
 */

class DepthPatch {

    //private variables
    int _id;
    int _col;
    int _row;
    String _dp;

    // Empty constructor
    public DepthPatch(){

    }
    // constructor
    public DepthPatch(int id, int col, int row, String dp){
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
    public void setID(int id){
        this._id = id;
    }

    // getting col
    public int getCol(){
        return this._col;
    }

    // setting col
    public void setCol(int col){
        this._col = col;
    }

    public int getRow(){
        return this._row;
    }

    // setting row
    public void setRow(int row){
        this._row = row;
    }

    // getting depth patch
    public String getDepthPatch(){
        return this._dp;
    }

    // setting depth patch
    public void setDP(String dp){
        this._dp = dp;
    }
    
}
