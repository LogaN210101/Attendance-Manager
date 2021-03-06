package com.example.attendancemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.BLUE;

public class Teachermain2 extends AppCompatActivity implements View.OnClickListener {

    String clg,info,sub;
    String email;
    FirebaseAuth auth;
    DatabaseReference db,dbs;
    TableLayout t;
    int i=0;
    Button save;
    String student[][];
    TextView view_attend;
    CheckBox gcb;
    int total2=0;
    Switch edt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teachermain2);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(email.substring(0,email.indexOf('@')));
        Intent intent=getIntent();
        final String test=intent.getStringExtra(TeacherPage.s);
        info=test.substring(test.indexOf('!')+1,test.indexOf('@'));
        getSupportActionBar().setTitle("Attendance for "+ info);
        sub=test.substring(test.indexOf('@')+1);
        t=findViewById(R.id.table);
        save=findViewById(R.id.button);
        edt=findViewById(R.id.editbtn);
        view_attend=findViewById(R.id.view_attendance);
        getCollege();
        save.setOnClickListener(this);
        edt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked)
               {
                   AlertDialog.Builder alt=new AlertDialog.Builder(Teachermain2.this);
                   alt.setTitle("Note")
                           .setCancelable(false)
                           .setMessage("This mode is meant for rectification of the attendance,it will not increase the total count of classes")
                           .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                               }
                           });
                   AlertDialog a=alt.create();
                   a.show();
               }
            }

        });
        view_attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor obj =getSharedPreferences("MyData",MODE_PRIVATE).edit();
                obj.putString("From",test);
                obj.apply();
                startActivity(new Intent(Teachermain2.this,TakeAttendance.class));
                finish();
            }
        });
    }
    void show()
    {
        i=0;
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.removeAllViewsInLayout();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String a = ds.getKey();

                    db.child(a).child(sub).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            add a2 = dataSnapshot.getValue(add.class);
                            if (a2 != null) {
                                final TableRow tr=new TableRow(Teachermain2.this);
                                tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.MATCH_PARENT));
                                TextView tv1=new TextView(Teachermain2.this);
                                tv1.setText(a);
                                tv1.setTextSize(30);
                                CheckBox cb=new CheckBox(Teachermain2.this);
                                cb.setId(Integer.parseInt(a));
                                tr.addView(cb);
                                tr.addView(tv1);
                                t.addView(tr);
                                try {
                                    student[0][i++] = a;
                                }
                                catch(Exception e){}
                            }
                            else
                            {
                                try{
                                    //t.setVisibility(View.INVISIBLE);
                                    //error();
                                }
                                catch(Exception e){}
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





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
                if(i==0)
                    errorNoStudent();
                show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void errorNoStudent() {
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Attention!")
                .setCancelable(false)
                .setMessage("No Students found  in this class for this subject code.")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(getApplicationContext(),TeacherPage.class));
                    }
                });
        AlertDialog a=alt.create();
        a.show();
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
                            add a2 = dataSnapshot.getValue(add.class);
                            if (a2 != null) {
                                String s2 = a2.uname;
                                String present = s2.substring(0, s2.indexOf('/'));
                                String total = s2.substring(s2.indexOf('/') + 1);
                                int j = getIndex(a);
                                student[1][j] = present;
                                student[2][j] = total;
                                if(Integer.parseInt(total)>total2)
                                    total2=Integer.parseInt(total);

                                updateTotal();
                            }

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
    public void updateTotal()
    {
        for(int t=0;t<student[0].length;t++)
            student[2][t]=""+total2;
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

    @Override
    public void onClick(View v) {
        if(v==save) {
            try {
                for (int j = 0; j < student[0].length; j++) {
                    gcb = findViewById(Integer.parseInt(student[0][j]));
                    if (gcb.isChecked()) {

                        if ((Integer.parseInt(student[1][j])) >= (Integer.parseInt(student[2][j])) && edt.isChecked()) {
                            Toast.makeText(getApplicationContext(), "Error! No. of present days exceeds No. of total days. Check row no. " + (j + 1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                            student[1][j] = "" + ((Integer.parseInt(student[1][j])) + 1);
                    }
                    if (!edt.isChecked()) {
                        student[2][j] = "" + ((Integer.parseInt(student[2][j])) + 1);
                    }

                }

            } catch (Exception e) {
            } finally {
                saveit();
            }
        }
    }
    public void saveit() {
        for (int j = 0; j < student[0].length; j++) {
            if (student[1][j] != null) {
                add a3 = new add((student[1][j] + "/" + student[2][j]));
                db.child(student[0][j]).child(sub).setValue(a3);
            }
        }
        t.removeAllViews();
        getCollege();
        Toast.makeText(getApplicationContext(), "Attendance for this class completed", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Teachermain2.this,TeacherPage.class));
        finish();
    }
}