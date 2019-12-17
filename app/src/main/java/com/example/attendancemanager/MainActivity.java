package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
    EditText ue,up;
    Button sign;
    String useremail,userpassword;
    ProgressDialog pd;
    TextView tvreg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign=findViewById(R.id.signin);
        ue=findViewById(R.id.email);
        up=findViewById(R.id.password);
        sign.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        tvreg=findViewById(R.id.register);
        tvreg.setOnClickListener(this);
        /*if(auth.getCurrentUser()!=null){
            finish();
            Intent it=new Intent(MainActivity.this,User.class);
            startActivity(it);
        }*/
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
            pd.setMessage("Logging in...");
            pd.show();
            auth.signInWithEmailAndPassword(useremail,userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                finish();
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                calllogin();//Call User Page
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Unsuccessful. Please Check email and password", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
        if(v==tvreg)
        {
            Intent it=new Intent(MainActivity.this,Registration.class);
            startActivity(it); //Registration Activity
        }

    }
    private void calllogin()
    {
       /*Intent it=new Intent(MainActivity.this,Login.class);
       startActivity(it);
       */
    }
}
