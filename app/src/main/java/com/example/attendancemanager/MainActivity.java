package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    private DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Login Page");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        sign=findViewById(R.id.signin);
        ue=findViewById(R.id.email);
        up=findViewById(R.id.password);
        sign.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        tvreg=findViewById(R.id.register);
        tvreg.setOnClickListener(this);
        if(auth.getCurrentUser()!=null){
            //calllogin();
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
            pd.setMessage("Logging in...");
            pd.show();
            auth.signInWithEmailAndPassword(useremail,userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
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
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    add a=ds.getValue(add.class);
                    if(a.uname.indexOf("Student")>0)
                    {
                        /*Call Student activity class
                        finish();
                        startActivity(new Intent(getApplicationContext(),Studentsubs.class));
                         */
                    }
                    if(a.uname.indexOf("Teacher")>0)
                    {
                        /*Call Teacher activity class
                        finish();
                        startActivity(new Intent(getApplicationContext(),Studentsubs.class));
                         */
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
