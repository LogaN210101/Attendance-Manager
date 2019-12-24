package com.example.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Teachermain2 extends AppCompatActivity {

    String clg,info,sub,s1="";
    String email;
    FirebaseAuth auth;
    DatabaseReference db,dbs;
    TableLayout t;
    int i=0;
    String student[][];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachermain2);
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(email.substring(0,email.indexOf('@')));
        Intent intent=getIntent();
        String test=intent.getStringExtra(TeacherPage.s);
        info=test.substring(test.indexOf('!')+1,test.indexOf('@'));
        sub=test.substring(test.indexOf('@')+1);
        t=findViewById(R.id.table);
        getCollege();
    }
    void show()
    {
        i=0;
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.removeAllViewsInLayout();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String a = ds.getKey();
                    final TableRow tr=new TableRow(Teachermain2.this);
                    tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.MATCH_PARENT));
                    TextView tv1=new TextView(Teachermain2.this);
                    tv1.setText(a);
                    tv1.setTextSize(30);
                    CheckBox cb=new CheckBox(Teachermain2.this);
                    tr.addView(cb);
                    tr.addView(tv1);
                    t.addView(tr);
                    student[0][i]=a;
                    i++;
                }
                defAttendance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getCollege()
    {
        dbs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS ads=dataSnapshot.getValue(AddS.class);
                clg=ads.college;
                db= FirebaseDatabase.getInstance().getReference().child("Students").child(clg).child(info);
                count();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void count()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    i++;
                }
                student=new String[3][i];
                show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public  void defAttendance()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    final String a=ds.getKey();
                    db.child(a).child(sub).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            add a2=dataSnapshot.getValue(add.class);
                            String s2=a2.uname;
                            String present=s2.substring(0,s2.indexOf('/'));
                            String total=s2.substring(s2.indexOf('/')+1);
                            int j=getIndex(a);
                            student[1][j]=present;
                            student[2][j]=total;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public int getIndex(String n)
    {
        int fl=0;
        for(int j=0;j<student[0].length;j++)
        {
            if(student[0][j].equals(n))
            {
                fl=j;
                break;
            }
        }
        return fl;
    }
}
