package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherPage extends AppCompatActivity {
    Button logout,proceed;
    private RadioGroup r;
    private RadioButton opt;
    private Spinner dpt;
    EditText paper, year,sec;
    String sub,sc,yr,dept,clg;
    DatabaseReference db;
    String email;
    static String s="";
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachermain1);
        logout=findViewById(R.id.lg);
        proceed=findViewById(R.id.next);
        r=findViewById(R.id.radiogroup);
        dpt=findViewById(R.id.department);
        paper=findViewById(R.id.Papercode);
        year=findViewById(R.id.batchyear);
        sec=findViewById(R.id.section);
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        db= FirebaseDatabase.getInstance().getReference().child("Users").child(email.substring(0,email.indexOf('@')));
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
                getInfo();
                if(opt==null)
                {
                    Toast.makeText(getApplicationContext(),"Select any one task and proceed",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(opt.getText().toString().equals("Take Attendance"))
                {
                    Intent intent=new Intent(TeacherPage.this,Teachermain2.class);
                    intent.putExtra(s,s);
                    startActivity(intent);
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
    public void getInfo()
    {
        Spinner spinner =findViewById(R.id.department);
        String dep=spinner.getSelectedItem().toString().trim();
        sub=paper.getText().toString();
        sc=sec.getText().toString();
        yr=year.getText().toString();
        if(dep.equals("Department") || sub.equals("") || sc.equals("") || yr.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please Enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        getCollege();
        s=s+"!"+dep+sc+yr;
    }
    public void getCollege()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS ads=dataSnapshot.getValue(AddS.class);
                clg=ads.college;
                s=s+clg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
