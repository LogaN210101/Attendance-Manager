package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import static android.graphics.Color.BLUE;

public class StudentEditAccount extends AppCompatActivity {
    private EditText nm,clgname,clgroll,sec,yer,teacherdept;
    private Spinner dpt;
    private Button sv;
    private ImageView profile;
    private StorageReference mStorageRef;
    public static final int PICK_IMAGE=1;
    private Uri imageuri;
    private ProgressDialog progress;
    static String g="",img_url="";
    String f,test,uname, name, dept, sc, clg, yr,clgr,dep;
    DatabaseReference ft,fs,fu;
    CheckInternet checkInternet;
    FirebaseAuth auth;
    String infos="";
    private TextView tv4;
    int fl=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("Edit Account");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLUE));
        auth=FirebaseAuth.getInstance();
        uname=auth.getCurrentUser().getEmail();
        test="Student";
        Intent inte=getIntent();
        infos=inte.getStringExtra(StudentPage.infos);

        checkInternet=new CheckInternet();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternet,intentFilter);
        tv4=findViewById(R.id.textView4);
        tv4.setText("Tap and Edit the Fields you want to update");
        //for storing images
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profilepics");

        ft= FirebaseDatabase.getInstance().getReference().child("Types"); //Database for Account Type
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
        imageuri=null;
        progress=new ProgressDialog(this);

        nm.setText(infos.substring(0,infos.indexOf('!')));
        clgname.setText(infos.substring(infos.indexOf('!')+1,infos.indexOf('@')));
        sec.setText(infos.substring(infos.indexOf('@')+1,infos.indexOf('#')));
        clgroll.setText(infos.substring(infos.indexOf('#')+1,infos.indexOf('$')));
        yer.setText(infos.substring(infos.indexOf('$')+1,infos.indexOf('%')));
        Glide.with(getApplicationContext()).load(infos.substring(infos.indexOf('%')+1,infos.indexOf('^'))).into(profile);
        img_url=infos.substring(infos.indexOf('%')+1,infos.indexOf('^'));
        //try {imageuri=Uri.parse(img_url);}catch(Exception e){}
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
                name=nm.getText().toString().trim();
                clg=clgname.getText().toString().trim().toUpperCase();
                sc=sec.getText().toString().trim().toUpperCase();
                clgr=clgroll.getText().toString().trim();
                yr=yer.getText().toString().trim();
                Spinner spinner=findViewById(R.id.department);
                dep=spinner.getSelectedItem().toString().trim();
                if(test.equals("Student")) {
                    if (yr.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please Enter your year of joining", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (yr.length()!=4) {
                        Toast.makeText(getApplicationContext(), "Please Enter year in YYYY Format", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (clgr.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please Enter Roll number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sc.equals("")) {
                        Toast.makeText(getApplicationContext(), "Section cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (dep.equals("Department") || dep.equals((""))) {
                        Toast.makeText(getApplicationContext(), "Please Select Your Department", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (clg.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter your college name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please Enter your name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                fl=0;
                if(!infos.substring(infos.indexOf('!')+1,infos.indexOf('@')).equals(clgname.getText().toString().trim().toUpperCase()))
                    fl=1;
                if(!sec.getText().toString().trim().toUpperCase().equals(infos.substring(infos.indexOf('@')+1,infos.indexOf('#'))))
                    fl=1;
                if(!infos.substring(infos.indexOf('%')+1).equals(dep))
                    fl=1;
                if(!infos.substring(infos.indexOf('$')+1,infos.indexOf('%')).equals(yr))
                    fl=1;
                if(!infos.substring(infos.indexOf('#')+1,infos.indexOf('$')).equals(clgr))
                    fl=1;
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
        name = nm.getText().toString().trim();
        try {
            progress.setTitle("Uploading");
            progress.setCancelable(false);
            progress.show();
            if (imageuri != null) {
                final StorageReference user_profile = mStorageRef.child( uname+".jpg");
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
                progress.dismiss();
                extradata();
            }
        }catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void extradata() {

        AddS ad = new AddS(name, clg, dep, sc, clgr, yr, img_url);
        fu.child((uname).substring(0, (uname).indexOf('@'))).setValue(ad);
        if (fl >-1) {
            Toast.makeText(getApplicationContext(), "Account Updated", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), StudentPage.class);
            finish();
            startActivity(i);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE) {
                imageuri = data.getData();
            }
        }
        catch (Exception e){
            imageuri=null;
        }
        finally
        {
            if(imageuri!=null)
                profile.setImageURI(imageuri);
        }
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