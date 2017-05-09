package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.util.List;

public class LogicActivity extends AppCompatActivity {

    private dbHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        dbh = initDB();
        List<Descriptor> descs =  dbh.getAllDescriptors();
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    public dbHelper initDB() {
        dbh = new dbHelper(this);

        try {
            dbh.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            dbh.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        return dbh;
    }
}
