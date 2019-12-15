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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
    EditText e,p;
    Button b;
    String email,password;
    ProgressDialog pd;
    TextView tvreg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b=findViewById(R.id.button);
        e=findViewById(R.id.editText);
        p=findViewById(R.id.edittext2);
        b.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        tvreg=findViewById(R.id.textView2);
        tvreg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==b)
        {
            email=e.getText().toString().trim();
            password=p.getText().toString().trim();
            if(email.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Email Id cannot be Blank",Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Password cannot be Blank",Toast.LENGTH_SHORT).show();
                return;
            }
            pd.setMessage("Logging in...");
            pd.show();
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                finish();
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                //Call User Page
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Unsuccessful. Please Check email and password", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
        if(v==tvreg)
        {
            //Registration Activity
        }
    }
}
