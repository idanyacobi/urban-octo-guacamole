package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LogicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
