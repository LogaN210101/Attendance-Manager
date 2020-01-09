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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.BLUE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
    EditText ue,up;
    Button sign;
    String useremail,userpassword;
    ProgressDialog pd;
    TextView tvreg;
    int fl=0;
    static String ty="";
    CheckInternet checkInternet;
    private DatabaseReference db;
    final String ft="First";
    TextView forgetpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Login Page");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        forgetpass=findViewById(R.id.textView9);
        forgetpass.setOnClickListener(this);

        SharedPreferences set=getSharedPreferences(ft,0);
        if(set.getBoolean("firsttime",true)) {
            AlertDialog.Builder alt = new AlertDialog.Builder(this);
            alt.setTitle("Important!")
                    .setCancelable(false)
                    .setMessage("Thank you for downloading this application. This application shares the data with the online storage and all other accounts are directly or indirectly linked to each other's account, so please follow all the instructions provided by the college while registering and using this app. A small mistake on your side will hamper your own attendance and neither college nor developers shall be responsible for this loss. Its a humble request to you all users to abide by the rules set by your college. This app was designed to lubricate the process of taking attendance by the teacher and such can be acquired by the cooperation of the teachers and the students.\n" +
                            "Thank you,\n" +
                            "Team Developers")
                    .setPositiveButton("Yes, I Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog a1 = alt.create();
            a1.show();
            set.edit().putBoolean("firsttime",false).commit();
        }

        checkInternet=new CheckInternet();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternet,intentFilter);
        sign=findViewById(R.id.signin);
        ue=findViewById(R.id.email);
        up=findViewById(R.id.password);
        sign.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        tvreg=findViewById(R.id.register);
        tvreg.setOnClickListener(this);
        SharedPreferences myobj =getSharedPreferences("MyData",MODE_PRIVATE);
        String un=myobj.getString("Type",null);

        if(un!=null) {
            if (un.equals("Teacher")) {
                finish();
                Intent i = new Intent(MainActivity.this, TeacherPage.class);
                startActivity(i);
            }
            if (un.equals("Student")) {
                finish();
                Intent i = new Intent(MainActivity.this, StudentPage.class);
                startActivity(i);
            }
        }
        db= FirebaseDatabase.getInstance().getReference().child("Types");
    }

    @Override
    public void onClick(View v) {
        if(v==forgetpass)
        {
            useremail=ue.getText().toString().trim();
            if(useremail.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Enter the Email ID",Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(useremail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Reset Password link sent to your email.",Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        if(v==sign)
        {
            useremail=ue.getText().toString().trim();
            userpassword=up.getText().toString().trim();
            if(useremail.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Email Id cannot be Blank",Toast.LENGTH_SHORT).show();
                return;
            }
            if(userpassword.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Password cannot be Blank",Toast.LENGTH_SHORT).show();
                return;
            }
            if(useremail.indexOf('@')<0)
            {
                Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            pd.setMessage("Logging in...");
            pd.show();
            auth.signInWithEmailAndPassword(useremail,userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    calllogin();//Call User Page
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Please Verify your Email address",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }
        if(v==tvreg)
        {
            tvreg.setVisibility(View.INVISIBLE);
            Intent it=new Intent(MainActivity.this,CreateAccount.class);
            finish();
            startActivity(it); //Registration Activity
        }

    }
    private void calllogin()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    add a=ds.getValue(add.class);
                    if(a.uname.contains(useremail.substring(0,useremail.indexOf('@'))+"Student"))
                    {
                        finish();
                        startActivity(new Intent(getApplicationContext(),StudentPage.class));

                    }
                    if(a.uname.contains(useremail.substring(0,useremail.indexOf('@'))+"Teacher"))
                    {
                        finish();
                        startActivity(new Intent(getApplicationContext(),TeacherPage.class));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        finishAffinity();
                    }
                });
        AlertDialog a=alt.create();
        a.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkInternet);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf=getMenuInflater();
        inf.inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1: { AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Credits")
                        .setCancelable(false)
                        .setMessage("This app was made as a project for ACM Hackaday by Team Techie (we chose the name ourselves). Our team consisted of three students from Heritage Institute of Technology, 1st years. The team was headed towards success by the developers Subhopriyo Sadhukhan, Ankit Verma and Shahil Singh. The project was started on 14th December,2019 and continued till 28th December,2019 for making of the base model work. Further updates are being worked on and being deployed till date. The project was developed in Java, XML and Firebase. Thank you users for using this beautiful android application which makes your day-to-day work easier. Please contact the developers in case of any query or feedback.")
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
            case R.id.item2:
            {
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Readme")
                        .setCancelable(false)
                        .setMessage("Thank You for Installing Attendance Manager. If you are a new user, at first register yourself. After successful registration, you will receive a mail in your registered email id. Verify the email id from the link given in the mail. After verification, sign in using your email id and password. Teachers can give attendance and view attendance of any particular section at a time. Students need to be very careful while registering their subjects for that semester in the beginning. If you do not have any data for a particular field, do fill it with a \"N/A\"  Students have got option to change their subjects after semester, which will reset the last attendances. In case of any abnormal behaviour of the android application or feedback, please contact the developers. Your participation in further developing this app is highly appreciated.\n" +
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
            case R.id.item3:
            {
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Alert!")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
