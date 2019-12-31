package com.example.attendancemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.graphics.Color.BLUE;

public class Studentsubs extends AppCompatActivity {
    private Button save;
    private EditText[] subs=new EditText[12];
    String subjects[]=new String[15],Subs="";
    int j,i=0,a=0;
    String info,clgr,clg;
    DatabaseReference fd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_studentsubs);
        getSupportActionBar().setTitle("Registration Page");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        save=findViewById(R.id.saveinfo);
        //Linking all the subjects
        for(j=1;j<=12;j++)
        {
            String edtid="sub"+j;
            int resId=getResources().getIdentifier(edtid,"id",getPackageName());
            subs[j-1]=findViewById(resId);
        }

        Intent intent=getIntent();
        String test=intent.getStringExtra(Registration.g);
        info= test.substring(0,test.indexOf('@'));
        clg=test.substring((test.indexOf('@')+1),test.indexOf('!'));
        clgr=test.substring(test.indexOf('!')+1);
        fd=FirebaseDatabase.getInstance().getReference().child("Students");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

    }
    public void Checkon() {
        i=0;
        for(j=0;j<12;j++)
            if (!subs[j].getText().toString().trim().equals(""))
            {  subjects[i] = subs[j].getText().toString();
                i++;
            }


    }
    void Dialog()
    {
        Checkon();
        Subs="";
        for(a=0;a<i;a++)
        { Subs= Subs+"\n"+subjects[a];
        }
        if(i==0)
        {
            Toast.makeText(getApplicationContext(),"Please select your subjects",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Please Recheck your inputs before proceeding.")
                .setCancelable(false)
                .setMessage(Subs)
                .setNegativeButton("Re-Check", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      saveData(); //Saved the subjects to database
                    }
                });
        AlertDialog a1=alt.create();
        a1.show();
    }
    public void saveData()
    {
        for(int q=0;q<i;q++)
        {
            add nsub=new add("0/0");
            fd.child(clg).child(info).child(clgr).child(subjects[q]).setValue(nsub);
        }
        String allsubject="";
        for(int q=0;q<i;q++)
        {
            allsubject=allsubject+subjects[q]+"/";
        }
        add aa=new add(allsubject);
        fd.child(clg).child(info).child(clgr).child("All").setValue(aa);
        Toast.makeText(getApplicationContext(),"Congratulations! You have successfully completed the registration process",Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(Studentsubs.this,MainActivity.class));
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Warning!")
                .setCancelable(false)
                .setMessage("You cannot Leave while registering. If you close the app now, all data will be lost.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();;
                    }
                });
        AlertDialog a=alt.create();
        a.show();
    }
}

