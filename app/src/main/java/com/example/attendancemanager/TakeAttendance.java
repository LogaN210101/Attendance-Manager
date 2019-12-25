package com.example.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

public class TakeAttendance extends AppCompatActivity {
    String clg,info,sub,s1="";
    String email;
    FirebaseAuth auth;
    DatabaseReference db,dbs;
    TableLayout t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachermain3);
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
    public void getCollege()
    {
        dbs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddS ads=dataSnapshot.getValue(AddS.class);
                clg=ads.college;
                db=FirebaseDatabase.getInstance().getReference().child("Students").child(clg).child(info);
                show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void show()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    final String a=ds.getKey();
                    db.child(a).child(sub).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            add a2=dataSnapshot1.getValue(add.class);
                            final TableRow tr=new TableRow(TakeAttendance.this);
                            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.MATCH_PARENT));
                            TextView tv1=new TextView(TakeAttendance.this);
                            String s2=a2.uname;
                            tv1.setTextSize(25);
                            tv1.setText(a+"\n"+s2+"\n");
                            tr.addView(tv1);
                            t.addView(tr);
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
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),TeacherPage.class));
        finish();
    }
}
