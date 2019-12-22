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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.graphics.Color.BLUE;

public class ChangeSub extends AppCompatActivity {
    private Button save;
    private CheckBox MATHS,PHYS,PHLAB,CHEM,CHLAB,ELEC,ELECLAB,ECE,ECELAB,MECH,MECHLAB,HMTS,HMTSLAB,CSEN,CSLAB;
    String subjects[]=new String[15],Subs="";
    int i=0,a=0;
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
        MATHS=findViewById(R.id.maths);
        PHYS=findViewById(R.id.physics);
        PHLAB=findViewById(R.id.phylab);
        CHEM=findViewById(R.id.chemistry);
        CHLAB=findViewById(R.id.chemlab);
        ELEC=findViewById(R.id.electrical);
        ELECLAB=findViewById(R.id.eleclab);
        ECE=findViewById(R.id.electronics);
        ECELAB=findViewById(R.id.ecelab);
        MECH=findViewById(R.id.mechanical);
        MECHLAB=findViewById(R.id.mechlab);
        HMTS=findViewById(R.id.humanities);
        HMTSLAB=findViewById(R.id.hmtslab);
        CSEN=findViewById(R.id.computer);
        CSLAB=findViewById(R.id.cslab);

        Intent intent=getIntent();
        String test=intent.getStringExtra(StudentPage.g);
        info= test.substring(0,test.indexOf('@'));
        clg=test.substring((test.indexOf('@')+1),test.indexOf('!'));
        clgr=test.substring(test.indexOf('!')+1);
        fd= FirebaseDatabase.getInstance().getReference().child("Students");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

    }
    public void Checkon() {
        i=0;
        if (MATHS.isChecked())
            subjects[i++] = MATHS.getText().toString();
        if (PHYS.isChecked())
            subjects[i++] = PHYS.getText().toString();
        if (PHLAB.isChecked())
            subjects[i++] = PHLAB.getText().toString();
        if (CHEM.isChecked())
            subjects[i++] = CHEM.getText().toString();
        if (CHLAB.isChecked())
            subjects[i++] = CHLAB.getText().toString();
        if (ELEC.isChecked())
            subjects[i++] = ELEC.getText().toString();
        if (ELECLAB.isChecked())
            subjects[i++] = ELECLAB.getText().toString();
        if (ECE.isChecked())
            subjects[i++] = ECE.getText().toString();
        if (ECELAB.isChecked())
            subjects[i++] = ECELAB.getText().toString();
        if (CSEN.isChecked())
            subjects[i++] = CSEN.getText().toString();
        if (CSLAB.isChecked())
            subjects[i++] = CSLAB.getText().toString();
        if (MECH.isChecked())
            subjects[i++] = MECH.getText().toString();
        if (MECHLAB.isChecked())
            subjects[i++] = MECHLAB.getText().toString();
        if (HMTS.isChecked())
            subjects[i++] = HMTS.getText().toString();
        if (HMTSLAB.isChecked())
            subjects[i++] = HMTSLAB.getText().toString();
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
        startActivity(new Intent(ChangeSub.this,StudentPage.class));
        Toast.makeText(this,"Welcome",Toast.LENGTH_LONG).show();
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
