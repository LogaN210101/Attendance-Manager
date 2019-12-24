package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class TeacherPage extends AppCompatActivity {
    Button logout,proceed;
    private RadioGroup r;
    private RadioButton opt;
    private Spinner dpt;
    Button nxt;
    EditText paper, year,sec;
    String dept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachermain1);
        logout=findViewById(R.id.lg);
        proceed=findViewById(R.id.next);
        r=findViewById(R.id.radiogroup);
        dpt=findViewById(R.id.department);
        nxt=findViewById(R.id.next);
        paper=findViewById(R.id.Papercode);
        year=findViewById(R.id.batchyear);
        sec=findViewById(R.id.section);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Department, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dpt.setAdapter(adapter1);
        dpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dept = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherPage.this,TakeAttendance.class));
            }
        });
        SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
        obj.putString("Type","Teacher");
        obj.commit();
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
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opt = findViewById(r.getCheckedRadioButtonId());
                if(opt==null)
                {
                    Toast.makeText(getApplicationContext(),"Select any one task and proceed",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(opt.getText().toString().equals("Take Attendance"))
                {
                    startActivity(new Intent(TeacherPage.this,Teachermain2.class));
                    finish();
                }
                if(opt.getText().toString().equals("View Attendance"))
                {
                   /*
                    startActivity(TeacherPage.this,ViewAttendance.class);
                    finish();
                    */
                }

            }
        });

    }
}
