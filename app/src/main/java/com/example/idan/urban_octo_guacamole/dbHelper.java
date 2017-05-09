package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idan on 08/05/2017.
 */

public class dbHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "imgs_and_depths.db";
    public static final String TABLE_DESCS = "descriptors";
    public static final String TABLE_DEPTHS = "depth_patches";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Getting single contact TODO
    public Descriptor getDescriptor(int id) {return new Descriptor();}

    // Getting All Descriptors
    public List<Descriptor> getAllDescriptors() {
        List<Descriptor> descList = new ArrayList<Descriptor>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DESCS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Descriptor desc = new Descriptor();
                desc.setID(Integer.parseInt(cursor.getString(0)));
                desc.setCol(Integer.parseInt(cursor.getString(1)));
                desc.setRow(Integer.parseInt(cursor.getString(2)));
                desc.setDesc(cursor.getString(3));
                // Adding descriptor to list
                descList.add(desc);
            } while (cursor.moveToNext());
        }

        // return contact list
        return descList;
    }

    public List<DepthPatch> getAllDepthPatches() {
        List<DepthPatch> dpList = new ArrayList<DepthPatch>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEPTHS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DepthPatch desc = new DepthPatch();
                desc.setID(Integer.parseInt(cursor.getString(0)));
                desc.setCol(Integer.parseInt(cursor.getString(1)));
                desc.setRow(Integer.parseInt(cursor.getString(2)));
                desc.setDP(cursor.getString(3));
                // Adding depth patch to list
                dpList.add(desc);
            } while (cursor.moveToNext());
        }

        // return contact list
        return dpList;
    }

    // Getting descriptors Count TODO
    public int getDescriptorsCount() {return 0;}

    // Getting depth patches Count TODO
    public int getDepthPatchesCount() {return 0;}

}
