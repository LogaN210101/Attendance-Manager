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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

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
private CircleImageView dp;
static  String g,img_url;
CheckInternet checkInternet;
static String infos="";
int fl=0;
String s5="";
TextView details;
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
        dp=findViewById(R.id.profile_pic);
        SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
        obj.putString("Type","Student");
        obj.apply();
        details=findViewById(R.id.textView10);
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
                if (fl < sub.length) {
                    add als = dataSnapshot.getValue(add.class);
                    s = s + "\n" + subject + ": " + "\t" + als.uname;
                    int pre = Integer.parseInt(als.uname.substring(0, als.uname.indexOf('/')));
                    int tot = Integer.parseInt(als.uname.substring(als.uname.indexOf('/') + 1));
                    int percent;
                    if (tot == 0)
                        percent = 0;
                    else {
                        percent = pre * 100 / tot;
                    }
                    s = s + "\t" + " (" + percent + "%)";
                    tv.setText(s);
                    fl++;
                    pd.dismiss();
                }
                else {
                    add als = dataSnapshot.getValue(add.class);
                    String s1=subject+": "+als.uname;

                    int pre = Integer.parseInt(als.uname.substring(0, als.uname.indexOf('/')));
                    int tot = Integer.parseInt(als.uname.substring(als.uname.indexOf('/') + 1));
                    int percent;
                    if (tot == 0)
                        percent = 0;
                    else {
                        percent = pre * 100 / tot;
                    }
                    s1=s1+" ("+percent+"%)";
                    if(s.contains(subject+": "+"\t"+(pre-1)+"/"+(tot-1)))
                        s1=s1+"Y";
                    else
                        s1=s1+"N";

                    fl++;
                    pd.dismiss();
                    try{currentAttendance(s1);}catch(Exception e){}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void currentAttendance(String n1)
    {
        String p1="";
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        if(n1.charAt(n1.length()-1)=='Y')
            p1="You Attended " +(n1.substring(0,n1.indexOf(':')))+" Class";
        else if(n1.charAt(n1.length()-1)=='N')
            p1="You Missed " +(n1.substring(0,n1.indexOf(':')))+" Class";
        alt.setTitle(p1)
                .setCancelable(false)
                .setMessage("Current Attendance"+"\n"+n1.substring(0,n1.length()-1))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(getApplicationContext(),StudentPage.class));
                    }
                });
        AlertDialog a=alt.create();
        try{a.show();}catch(Exception e){}
    }
    public void subcheck()
    {
        dbs.child("All").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    add a5 = dataSnapshot.getValue(add.class);
                    try{ getSubject(a5.uname);}catch(Exception e) {Toast.makeText(getApplicationContext(),"It seems you have not registered for any subjects yet. Please add subjects at first to get attendance.",Toast.LENGTH_LONG).show();}
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
                if(s.contains(sub[c]))
                {
                    s="";
                    tv.setText("");
                    try{viewnm();}
                    catch(Exception e){}
                }
                show(sub[c++]);//To display attendance
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
        try{pd.setCancelable(false);}
        catch(Exception e){}
        pd.show();
        tv.setTextSize(25);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS ads = dataSnapshot.getValue(AddS.class);
                s = s + ads.name + "\n";
                clg = ads.college;
                clgr = ads.classroll;
                sec = ads.section;
                dep = ads.dept;
                yr = ads.year;
                img_url = ads.imgurl;
                infos = ads.name + "!" + clg + "@" + sec + "#" + clgr + "$" + yr + "%" + img_url + "^" + dep;
                RequestOptions plc= new RequestOptions();
                plc.placeholder(getResources().getDrawable(R.drawable.load));
                Glide.with(getApplicationContext()).setDefaultRequestOptions(plc).load(img_url).into(dp);
                s = s + clg + "\n" + dep + "\n" + sec + "\n" + yr + "\n" + clgr;
                s5 = s;
                details.setText(s);
                details.setTextSize(15f);
                s="YOUR ATTENDANCE";
                dbs = FirebaseDatabase.getInstance().getReference().child("Students").child(clg)
                        .child(dep + sec + yr).child(clgr);
                pd.dismiss();
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
                Intent inte=new Intent(getApplicationContext(),StudentEditAccount.class);
                inte.putExtra(infos,infos);
                finish();
                startActivity(inte);
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
            case R.id.item4:
            {
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Readme")
                        .setCancelable(false)
                        .setMessage("Thank You for Installing Attendance Manager. If you are a new user, at first register yourself. After successful registration, you will receive a mail in your registered email id. Verify the email id from the link given in the mail. After verification, sign in using your email id and password. Teachers can give attendance and view attendance of any particular section at a time. Students need to be very careful while registering their subjects for that semester in the beginning. If you do not have any data for a particular field, do fill it with a \"NA\"  Students have got option to change their subjects after semester, which will reset the last attendances. In case of any abnormal behaviour of the android application or feedback, please contact the developers. Your participation in further developing this app is highly appreciated.\n" +
                                "Thank you,\n" +
                                "Team Developers")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog a1 = alt.create();
                a1.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
