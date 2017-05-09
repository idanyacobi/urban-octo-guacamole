package com.example.idan.urban_octo_guacamole;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.util.Log;

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

    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME = "imgs_and_depths.db";// Database name
    private SQLiteDatabase mDataBase;
    private final Context mContext;


    public dbHelper(Context context) {
        super(context, DB_NAME, null, 1);// 1? Its database Version
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
        getAllDescriptors();
    }


    public void createDataBase() throws IOException
    {
        //If the database does not exist, copy it from the assets.

        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
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
        String selectQuery = "SELECT * FROM " + TABLE_DESCS;

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
