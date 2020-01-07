package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import static android.graphics.Color.BLUE;

public class Registration extends AppCompatActivity {
    private EditText nm,clgname,clgroll,sec,yer,teacherdept;
    private ImageView profile;
    public static final int PICK_IMAGE=1;
    private Uri imageuri;
    private StorageReference mStorageRef;
    private Spinner dpt;
    private Button sv;
    static String g="",img_url="";
    private ProgressDialog progress;
    String f,test,uname, name, dept, sc, clg, yr,clgr;
    DatabaseReference ft,fs,fu;
    CheckInternet checkInternet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("Registration Page");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));

        Intent intent=getIntent();
         f=intent.getStringExtra(CreateAccount.Type);
         uname=f.substring(0,f.indexOf('-'));
         test=f.substring(f.indexOf('-')+1);

        checkInternet=new CheckInternet();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternet,intentFilter);

        //for storing images
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profilepics");

        ft=FirebaseDatabase.getInstance().getReference().child("Types"); //Database for Account Type
        fs=FirebaseDatabase.getInstance().getReference().child("Student"); //Database for students
        fu=FirebaseDatabase.getInstance().getReference().child("Users"); //Store user info

        //For details part after registration
        nm = findViewById(R.id.name);
        clgname=findViewById(R.id.college);
        sec=findViewById(R.id.section);
        clgroll=findViewById(R.id.rollnumber);
        yer=findViewById(R.id.year);
        dpt = findViewById(R.id.department);
        teacherdept=findViewById(R.id.Teacherdepartment);
        sv=findViewById(R.id.save);
        profile=findViewById(R.id.profilepic);
        imageuri= null;

        progress=new ProgressDialog(this);



        //Setting detail entries invisible
        clgroll.setVisibility(View.INVISIBLE);
        sec.setVisibility(View.INVISIBLE);
        yer.setVisibility(View.INVISIBLE);
        dpt.setVisibility(View.INVISIBLE);
        teacherdept.setVisibility(View.INVISIBLE);

        if (test.equals("Teacher"))
        {

            teacherdept.setVisibility(View.VISIBLE);

        }
        else if(test.equals("Student"))
        {

            dpt.setVisibility(View.VISIBLE);
            clgroll.setVisibility(View.VISIBLE);
            sec.setVisibility(View.VISIBLE);
            yer.setVisibility(View.VISIBLE);

        }


        //For drop down list
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Department, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dpt.setAdapter(adapter1);
        dpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dept = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeimage();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a picture"),PICK_IMAGE);
            }
        });

    }
    void storeimage() {//To store the pic
        try {
            if (imageuri != null) {
                final StorageReference user_profile = mStorageRef.child( uname+".jpg");
                progress.setTitle("Uploading...");
                progress.show();
                progress.setCancelable(false);
                user_profile.putFile(imageuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                                    @Override
                                    public void onSuccess(Uri uri) {

                                        img_url = uri.toString();// to store url of the image
                                        extradata();
                                        progress.dismiss();
                                    }
                                });
                            }
                        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        progress.setMessage((int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount())+ " % Completed");
                    }
                });
            } else {
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Note!")
                        .setCancelable(false)
                        .setMessage("You have not selected any profile image.Do you want to proceed with a default image?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                img_url="https://firebasestorage.googleapis.com/v0/b/attendance-dbfaf.appspot.com/o/defaultpic.jpg?alt=media&token=15801193-87ce-4f4f-a873-f5287153c384";
                                extradata();
                            }
                        });

                AlertDialog a1 = alt.create();
                a1.show();

            }
        }catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void extradata()
    {
        name=nm.getText().toString().trim();
        clg=clgname.getText().toString().trim().toUpperCase();
        sc=sec.getText().toString().trim().toUpperCase();
        clgr=clgroll.getText().toString().trim();
        yr=yer.getText().toString().trim();
        Spinner spinner=findViewById(R.id.department);
        String dep=spinner.getSelectedItem().toString().trim();


        if(test.equals("Teacher"))
        {
            dep=teacherdept.getText().toString();
            if(dep.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Select your department",Toast.LENGTH_SHORT).show();
                return;
            }
            if(clg.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please enter college name",Toast.LENGTH_SHORT).show();
                return;
            }

            if(name.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter your name",Toast.LENGTH_SHORT).show();
                return;
            }
            AddS a=new AddS(name,clg,dep,"","","",img_url);
            fu.child((uname).substring(0,(uname).indexOf('@'))).setValue(a);
            add a1=new add((uname).substring(0,(uname).indexOf('@'))+"Teacher");
            ft.push().setValue(a1);
            Toast.makeText(getApplicationContext(),"Details successfully noted!",Toast.LENGTH_SHORT).show();
            nm.setText("");
            clgname.setText("");
            finish();

            Intent it=new Intent(Registration.this,MainActivity.class);
            startActivity(it);

        }
        if(test.equals("Student"))
        {
            if(yr.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter your year of joining",Toast.LENGTH_SHORT).show();
                return;
            }
            if(yr.length()!=4)
            {
                Toast.makeText(getApplicationContext(),"Please Enter your year of joining in 4 digit yyyy format",Toast.LENGTH_SHORT).show();
                yer.setText("");
                return;
            }
            if(clgr.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter Roll number",Toast.LENGTH_SHORT).show();
                return;
            }
            if(sc.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Section cannot be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            if(dep.equals("Department") || dep.equals(("")))
            {
                Toast.makeText(getApplicationContext(),"Please Select Your Department",Toast.LENGTH_SHORT).show();
                return;
            }
            if(clg.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please enter your college name",Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Please Enter your name",Toast.LENGTH_SHORT).show();
                return;
            }
            AddS ad=new AddS(name,clg,dep,sc,clgr,yr,img_url);
            fu.child((uname).substring(0,(uname).indexOf('@'))).setValue(ad);
            add a=new add((uname).substring(0,(uname).indexOf('@'))+"Student");
            ft.push().setValue(a);
            Toast.makeText(getApplicationContext(),"Few more Details remaining...",Toast.LENGTH_SHORT).show();

            Intent i=new Intent(getApplicationContext(),Studentsubs.class);
            g=""+dep+sc+yr+"@"+clg+"!"+clgr;
            i.putExtra(g,g);
            finish();
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{if(requestCode==PICK_IMAGE)
        {
            imageuri=data.getData();
            profile.setImageURI(imageuri);
        }}
        catch (Exception e){
        Toast.makeText(Registration.this, "You have not selected any image.", Toast.LENGTH_SHORT).show();}

    }
    @Override
    public void onBackPressed() {

            AlertDialog.Builder alt = new AlertDialog.Builder(this);
            alt.setTitle("Warning!")
                    .setCancelable(false)
                    .setMessage("You cannot Leave while registering. If you close the app now, all data will be lost.")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog a1 = alt.create();
            a1.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkInternet);
    }
}

