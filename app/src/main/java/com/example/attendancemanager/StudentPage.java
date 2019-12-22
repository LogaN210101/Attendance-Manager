package com.example.attendancemanager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.BLUE;
public class StudentPage extends AppCompatActivity {
Button logout;
String s="";
private TextView tv;
private FirebaseAuth auth;
private DatabaseReference db,dbs;
String sub[];
private String clg,clgr,dep,sec,yr;
private int i=0,a=0;
String email;
private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_page);
        getSupportActionBar().setTitle("Hello Student");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        logout=findViewById(R.id.lg);
        auth=FirebaseAuth.getInstance();
        SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
        obj.putString("Type","Student");
        obj.commit();
        pd=new ProgressDialog(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        tv=findViewById(R.id.textView);
        email=auth.getCurrentUser().getEmail();
        db= FirebaseDatabase.getInstance().getReference().child("Users").child(email.substring(0,email.indexOf('@')));
        s="";
        viewnm();//Get Student details
    }
    public void show(String n) {
        final String subject=n;
        dbs.child(n).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                add als = dataSnapshot.getValue(add.class);
                s = s + "\n" + subject + " = " + als.uname;
                tv.setText(s);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void subcheck()
    {
        dbs.child("All").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    add a1 = dataSnapshot.getValue(add.class);
                    getSubject(a1.uname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void getSubject(String n)
    {
        int c=0;
        for(i=0;i<n.length();i++)
        {
            if(n.charAt(i)=='/')
                c++;
        }
        sub=new String[c];
        String  s1="";
        c=0;
        for(i=0;i<n.length();i++)
        {
            char ch=n.charAt(i);
            if(ch=='/')
            {
                sub[c]=s1;
                s1="";
                show(sub[c++]); //To display attendance
            }
            else
            {
                s1=s1+ch;
            }

        }

    }
    public void viewnm()
    {
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        tv.setTextSize(25);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS ads=dataSnapshot.getValue(AddS.class);
                s=s+ads.name+"\n";
                clg=ads.college;
                clgr=ads.classroll;
                sec=ads.section;
                dep=ads.dept;
                yr=ads.year;
                s=s+clg+"\n"+dep+"\n"+sec+"\n"+yr+"\n"+clgr+"\n"+"YOUR ATTENDANCE";
                dbs=FirebaseDatabase.getInstance().getReference().child("Students").child(clg)
                        .child(dep+sec+yr).child(clgr);
                subcheck();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void exit()
    {
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Alert!")
                .setCancelable(false)
                .setMessage("Are you sure you want to Logout?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
                        obj.putString("Type",null);
                        obj.commit();
                        auth.signOut();
                        startActivity(new Intent(StudentPage.this,MainActivity.class));
                        finish();
                    }
                });
        AlertDialog a=alt.create();
        a.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Alert!")
                .setCancelable(false)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog a=alt.create();
        a.show();
    }
}
