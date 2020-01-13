package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.BLUE;

public class ChangeSub extends AppCompatActivity {
    private Button save;
    private EditText[] subs=new EditText[12];
    String subjects[]=new String[15],Subs="";
    int j,i=0,a=0;
    String alsub="";
    String info,clgr,clg;
    DatabaseReference fd;
    CheckInternet checkInternet;
    int c=0;
    String sub[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_studentsubs);
        getSupportActionBar().setTitle("Change Subjects");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        save=findViewById(R.id.saveinfo);

        checkInternet=new CheckInternet();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternet,intentFilter);

        Intent intent=getIntent();
        String test=intent.getStringExtra(StudentPage.g);
        info= test.substring(0,test.indexOf('@'));
        clg=test.substring((test.indexOf('@')+1),test.indexOf('!'));
        clgr=test.substring(test.indexOf('!')+1);
        fd= FirebaseDatabase.getInstance().getReference().child("Students");
        fd.child(clg).child(info).child(clgr).child("All").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                add d=dataSnapshot.getValue(add.class);
                getSubject(d.uname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Linking all the subjects
        for(j=1;j<=12;j++)
        {
            String edtid="sub"+j;
            int resId=getResources().getIdentifier(edtid,"id",getPackageName());
            subs[j-1]=findViewById(resId);
        }
        getSub();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

    }
    public void getSubject(String n)
    {
        int c1=0;
        for(i=0;i<n.length();i++)
        {
            if(n.charAt(i)=='/')
                c1++;
        }
        sub=new String[c1];
        String  s1="";
        c1=0;
        for(i=0;i<n.length();i++)
        {
            char ch=n.charAt(i);
            if(ch=='/')
            {
                sub[c1]=s1;
                subs[c1++].setText(s1);
                s1="";
            }
            else
            {
                s1=s1+ch;
            }
        }
    }
    public void getSub()
    {
        String c2="";
        for(int i=0;i<alsub.length();i++)
        {
            char ch=alsub.charAt(i);
            if(ch!='/')
                c2=c2+ch;
            else
            {
                subs[c].setText(c2);
                c++;
                c2="";
            }
        }
    }
    public void Checkon() {
        i=0;
        for(j=0;j<12;j++)
            if (!subs[j].getText().toString().trim().equals(""))
            {  subjects[i] = subs[j].getText().toString();
                i++;}

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
            int fl=0;
            add nsub=new add("0/0");
            for(int j=0;j<sub.length;j++)
            {
                if(sub[j].equals(subjects[q]))
                    fl=1;
            }
            if(fl==0){
                fd.child(clg).child(info).child(clgr).child(subjects[q]).setValue(nsub);
            }
            fl=0;
        }
        String allsubject="";
        for(int q=0;q<i;q++)
        {
            allsubject=allsubject+subjects[q]+"/";
        }
        add aa=new add(allsubject);
        fd.child(clg).child(info).child(clgr).child("All").setValue(aa);
        Toast.makeText(getApplicationContext(),"All Set-up. You have successfully changed the subjects",Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(ChangeSub.this,StudentPage.class));
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(),StudentPage.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkInternet);
    }
}

