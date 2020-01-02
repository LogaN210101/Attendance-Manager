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
import android.view.KeyEvent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Login Page");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));

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
                        finish();
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
            case R.id.item1: { ty="Credits";

                Intent it=new Intent(getApplicationContext(),About.class);
                it.putExtra(ty,ty);
                startActivity(it);
                break;
            }
            case R.id.item2:
            {
                ty="ReadMe";
                Intent it=new Intent(getApplicationContext(),About.class);
                it.putExtra(ty,ty);
                startActivity(it);
                break;

            }
            case R.id.item3:
            {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
