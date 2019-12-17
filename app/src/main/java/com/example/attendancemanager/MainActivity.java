package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {      //MainActivity is the Login Activity
    FirebaseAuth auth;
    EditText ue,up;
    Button sign;
    String useremail,userpassword;
    ProgressDialog pd;
    TextView tvreg;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign=findViewById(R.id.button);
        ue=findViewById(R.id.email);
        up=findViewById(R.id.password);
        sign.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        tvreg=findViewById(R.id.textView2);
        tvreg.setOnClickListener(this);
        if(auth.getCurrentUser()!=null){
            calllogin();
        }
        db=FirebaseDatabase.getInstance().getReference().child("Types");
    }

    @Override
    public void onClick(View v) {
        if(v==sign)
        {
            Login();
        }
        if(v==tvreg)
        {
            Intent it=new Intent(MainActivity.this,Registration.class);
            startActivity(it); //Registration Activity
        }

    }
    private void Login()
    {
        String uem=ue.getText().toString();
        String upa=up.getText().toString();
        if(uem.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please Provide your Email ID",Toast.LENGTH_SHORT).show();
            return;
        }
        if(upa.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please provide a password",Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage("Logging in...");
        pd.show();
        auth.signInWithEmailAndPassword(uem,upa)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            //calllogin();//Call User Page
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Unsuccessful. Please Check email and password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
    private void calllogin()
    {

    }
}

