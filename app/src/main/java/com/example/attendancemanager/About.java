package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import static android.graphics.Color.BLUE;

public class About extends AppCompatActivity {
    static String type="";
    String readme="add readme";
    String credit="add credit";
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about);
        tv=findViewById(R.id.abt);
        Intent intent=getIntent();
        type=intent.getStringExtra(MainActivity.ty);
        if(type.equals("Credits"))
        {
            tv.setText(credit);
        }
        else
            tv.setText(readme);
        getSupportActionBar().setTitle(type);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
