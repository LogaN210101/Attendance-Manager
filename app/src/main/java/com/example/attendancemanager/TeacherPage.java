package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeacherPage extends AppCompatActivity {
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_page);
        logout=findViewById(R.id.lg);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
                obj.putString("Type",null);
                obj.commit();
                startActivity(new Intent(TeacherPage.this,MainActivity.class));
                finish();

            }
        });
    }
}
