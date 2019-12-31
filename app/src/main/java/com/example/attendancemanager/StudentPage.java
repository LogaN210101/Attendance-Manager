package com.example.attendancemanager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
String s="";
private TextView tv;
private FirebaseAuth auth;
private DatabaseReference db,dbs;
String sub[];
private String clg,clgr,dep,sec,yr;
private int i=0;
String email;
private ProgressDialog pd;
static  String g;
CheckInternet checkInternet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_page);
        getSupportActionBar().setTitle("Hello Student");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        checkInternet=new CheckInternet();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternet,intentFilter);
        Toast.makeText(this,"Welcome",Toast.LENGTH_LONG).show();
        auth=FirebaseAuth.getInstance();
        SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
        obj.putString("Type","Student");
        obj.apply();
        pd=new ProgressDialog(this);
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
                int pre=Integer.parseInt(als.uname.substring(0,als.uname.indexOf('/')));
                int tot=Integer.parseInt(als.uname.substring(als.uname.indexOf('/')+1));
                int percent;
                if(tot==0)
                    percent=0;
                else
                {
                    percent=pre*100/tot;
                }

                s=s+" = "+percent+"%";
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
                s=s+clg+"\n"+dep+"\n"+sec+"\n"+yr+"\n"+clgr+"\n"+"\n"+"YOUR ATTENDANCE";
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
                        obj.apply();
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
                .setMessage("Are you sure you want to exit without logging out?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
        AlertDialog a=alt.create();
        a.show();
    }
    public void change()
    {
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Alert!")
                .setCancelable(false)
                .setMessage("Are you sure you want to Change your Subjects? This will reset your attendance")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(StudentPage.this,ChangeSub.class);
                        g=""+dep+sec+yr+"@"+clg+"!"+clgr;
                        intent.putExtra(g,g);
                        finish();
                        startActivity(intent);
                    }
                });
        AlertDialog a1=alt.create();
        a1.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkInternet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf=getMenuInflater();
        inf.inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1: {
                finish();
                startActivity(new Intent(getApplicationContext(),StudentEditAccount.class));
                break;
            }
            case R.id.item2:
            {
                change();
                break;
            }
            case R.id.item3:
            {
                exit();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
