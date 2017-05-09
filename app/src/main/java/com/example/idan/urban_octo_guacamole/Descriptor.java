package com.example.idan.urban_octo_guacamole;

/**
 * Created by idan on 08/05/2017.
 */

class Descriptor {
    //private variables
    int _id;
    int _col;
    int _row;
    String _desc;

    // Empty constructor
    public Descriptor(){

    }
    // constructor
    public Descriptor(int id, int col, int row, String desc){
        this._id = id;
        this._col = col;
        this._row = row;
        this._desc = desc;
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

    // getting descriptor
    public String getDescriptor(){
        return this._desc;
    }

    // setting descriptor
    public void setDesc(String desc){
        this._desc = desc;
    }

    // TODO
    public float distanceFrom(Descriptor other){
        return 0;
    }

}
