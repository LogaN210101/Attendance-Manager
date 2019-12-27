package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.graphics.Color.BLUE;

public class CreateAccount extends AppCompatActivity {

    private EditText uname,upass,cpass;
    private RadioGroup r;
    private RadioButton teacher, student, op;
    Button next;
    static String Email,Password,Type;
    private ProgressDialog progress;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().setTitle("Create Your Account");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));

        //First UI details to appear
        uname = findViewById(R.id.email);
        upass = findViewById(R.id.password);
        r = findViewById(R.id.radiogroup);
        teacher = findViewById(R.id.rteacher);
        student = findViewById(R.id.rstudent);
        next = findViewById(R.id.proceed);
        progress = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        cpass=findViewById(R.id.confirmpassword);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  check();
            }
        });

    }
    void check() {
        Email = uname.getText().toString().trim();
        Password = upass.getText().toString().trim();
        if (cpass.getText().toString().equals(Password)) {
            op = findViewById(r.getCheckedRadioButtonId());
            if (op == null) {
                Toast.makeText(getApplicationContext(), "Select any one account type and proceed", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setMessage("Registering user...");
            progress.setCancelable(false);
            progress.show();
            firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateAccount.this, "New Account Created", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getApplicationContext(), Registration.class);
                                        Type = Email + "-" + op.getText().toString().trim();
                                        i.putExtra(Type, Type);
                                        startActivity(i);
                                    }
                                }, 500);
                            } else
                                Toast.makeText(CreateAccount.this, "Oops! Something's not right. Please try again", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "Password Mismatch", Toast.LENGTH_SHORT).show();
            cpass.setText("");
        }
    }
    @Override
    public void onBackPressed() {

        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();

    }
}

