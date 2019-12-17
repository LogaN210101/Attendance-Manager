package com.example.attendancemanager;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;



import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;

import android.os.Handler;

import android.text.TextUtils;

import android.view.View;

import android.widget.AdapterView;

import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.EditText;

import android.widget.RadioButton;

import android.widget.RadioGroup;

import android.widget.Spinner;

import android.widget.TextView;

import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends AppCompatActivity {
    private EditText uname, upass, nm,clgname,clgroll,sec,yer,teacherdept;
    private TextView hd,next;
    private RadioGroup r;
    private RadioButton teacher, student, op;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private Spinner dpt;
    private Button sv;
    String name, dept, sc, clg, yr,clgr;
    DatabaseReference ft,fs,fu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progress = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        ft=FirebaseDatabase.getInstance().getReference().child("Types"); //Database for Account Type
        fs=FirebaseDatabase.getInstance().getReference().child("Student"); //Database for students
        fu=FirebaseDatabase.getInstance().getReference().child("Users"); //Store user info

        //First UI details to appear
        uname = findViewById(R.id.email);
        upass = findViewById(R.id.password);
        r = findViewById(R.id.radiogroup);
        teacher = findViewById(R.id.rteacher);
        student = findViewById(R.id.rstudent);
        hd=findViewById(R.id.heading);
        next = findViewById(R.id.proceed);
        next.setTextColor(Color.GREEN);

        //For details part after registration
        nm = findViewById(R.id.name);
        clgname=findViewById(R.id.college);
        sec=findViewById(R.id.section);
        clgroll=findViewById(R.id.rollnumber);
        yer=findViewById(R.id.year);
        dpt = findViewById(R.id.department);
        teacherdept=findViewById(R.id.Teacherdepartment);
        sv=findViewById(R.id.save);

        //Setting detail entries invisible
        nm.setVisibility(View.INVISIBLE);
        clgname.setVisibility(View.INVISIBLE);
        clgroll.setVisibility(View.INVISIBLE);
        sec.setVisibility(View.INVISIBLE);
        yer.setVisibility(View.INVISIBLE);
        dpt.setVisibility(View.INVISIBLE);
        sv.setVisibility(View.INVISIBLE);
        teacherdept.setVisibility(View.INVISIBLE);

        //For drop down list
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
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extradata();
            }
        });
    }

    void check() {
        String Email = uname.getText().toString().trim();
        String Password = upass.getText().toString().trim();
        op = findViewById(r.getCheckedRadioButtonId());
        if(op==null)
        {
            Toast.makeText(getApplicationContext(),"Select any one account type and proceed",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Registering user...");
        progress.show();
        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Registered user successfully", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    load();
                                }
                            },1000);
                        } else
                            Toast.makeText(Registration.this, "Please try again", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                });

    }

    void load() {

        if (op.getText().toString().equals("Teacher"))
        {
            show();
            teacherdept.setVisibility(View.VISIBLE);
            return;
        }
        else if(op.getText().toString().equals("Student"))
        {
            show();
            dpt.setVisibility(View.VISIBLE);
            clgroll.setVisibility(View.VISIBLE);
            sec.setVisibility(View.VISIBLE);
            yer.setVisibility(View.VISIBLE);
            return;

        }
    }
    void show()
    {
        //Removing previous things
        uname.setVisibility(View.INVISIBLE);
        upass.setVisibility(View.INVISIBLE);
        r.setVisibility(View.INVISIBLE);
        teacher.setVisibility(View.INVISIBLE);
        student.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);

        nm.setVisibility(View.VISIBLE);
        clgname.setVisibility(View.VISIBLE);

        sv.setVisibility(View.VISIBLE);
        hd.setText("Enter Details to complete registration");
        hd.setTextSize(20f);
    }
    void extradata()
    {
        name=nm.getText().toString().trim();
        clg=clgname.getText().toString().trim();
        sc=sec.getText().toString().trim();
        clgr=clgroll.getText().toString().trim();
        yr=yer.getText().toString().trim();
        Spinner spinner=findViewById(R.id.department);
        String dep=spinner.getSelectedItem().toString().trim();
        if(op.getText().toString().equals("Teacher"))
        {
            dep=teacherdept.getText().toString();
            if(dep.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Select your department",Toast.LENGTH_SHORT).show();
                return;
            }
            if(clg.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please enter college name",Toast.LENGTH_SHORT).show();
                return;
            }

            if(name.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter your name",Toast.LENGTH_SHORT).show();
                return;
            }
            AddS a=new AddS(name,clg,dep,"","","");
            fu.child((uname.getText().toString()).substring(0,(uname.getText().toString()).indexOf('@'))).setValue(a);
            add a1=new add((uname.getText().toString()).substring(0,(uname.getText().toString()).indexOf('@'))+"Teacher");
            ft.push().setValue(a1);
            Toast.makeText(getApplicationContext(),"Details successfully noted!",Toast.LENGTH_SHORT).show();
            nm.setText("");
            clgname.setText("");
            finish();
            Intent it=new Intent(Registration.this,MainActivity.class);
            startActivity(it);
        }
        if(op.getText().toString().equals("Student"))
        {
            if(yr.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter your year of joining",Toast.LENGTH_SHORT).show();
                return;
            }
            if(clgr.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter Roll number",Toast.LENGTH_SHORT).show();
                return;
            }
            if(sc.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Section cannot be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            if(dep.equals("Department") || dep.equals(("")))
            {
                Toast.makeText(getApplicationContext(),"Please Select Your Department",Toast.LENGTH_SHORT).show();
                return;
            }
            if(clg.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please enter your college name",Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter your name",Toast.LENGTH_SHORT).show();
                return;
            }
            AddS ad=new AddS(name,clg,dep,sc,clgr,yr);
            fu.child((uname.getText().toString()).substring(0,(uname.getText().toString()).indexOf('@'))).setValue(ad);
            fs.child(clg).child(""+dep+sc+yr).child(clgr).setValue(ad);
            add a=new add((uname.getText().toString()).substring(0,(uname.getText().toString()).indexOf('@'))+"Student");
            ft.push().setValue(a);
            Toast.makeText(getApplicationContext(),"Details successfully noted!",Toast.LENGTH_SHORT).show();

        }
        startActivity(new Intent(getApplicationContext(),MainActivity.class));

    }
}

