package com.example.attendancemanager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
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

import static android.graphics.Color.BLUE;

public class TakeAttendance extends AppCompatActivity {   //class to view attendance
    String clg,info,sub;
    String email;
    FirebaseAuth auth;
    DatabaseReference db,dbs;
    TableLayout t;
    TextView tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teachermain3);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(email.substring(0,email.indexOf('@')));
        Intent intent=getIntent();
        String test=intent.getStringExtra(TeacherPage.s);
        info=test.substring(test.indexOf('!')+1,test.indexOf('@'));
        sub=test.substring(test.indexOf('@')+1);
        getSupportActionBar().setTitle("Attendance for "+info);
        t=findViewById(R.id.table);
        tv2=findViewById(R.id.textView2);
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
                            TextView t2=new TextView(TakeAttendance.this);
                            TextView t3=new TextView(TakeAttendance.this);
                            String s2=a2.uname;
                            int present=Integer.parseInt(s2.substring(0,s2.indexOf('/')));
                            int total=Integer.parseInt(s2.substring(s2.indexOf('/')+1));
                            tv1.setTextSize(25);
                            t2.setTextSize(25);
                            t3.setTextSize(25);
                            tv1.setText(a+"\t\t\t");
                            t2.setText("Attended: "+present+"\t\t\t");
                            if(total>0)
                                t3.setText(present*100/total+"%"+"\n");
                            else
                                t3.setText("0%"+"\n");
                            tv2.setText("Total Classes: "+total);
                            tr.addView(tv1);
                            tr.addView(t2);
                            tr.addView(t3);
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
