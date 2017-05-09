package com.example.idan.urban_octo_guacamole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LogicActivity extends AppCompatActivity {

    private dbHelper dbh;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
        imgView = (ImageView)this.findViewById(R.id.faceImage);
        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("Face");
        imgView.setImageBitmap(bitmap);
        dbh = new dbHelper(this);

    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
