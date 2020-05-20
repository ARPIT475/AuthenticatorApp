package com.example.authenticatorapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.proto.TargetGlobal;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

public class Register extends AppCompatActivity {


    public static final String ASK = "ASK";
    EditText Fullname,Fathername,Mothername,Collegeusn,Emailid,Password,Dob,Address,Branch,Year, Classsection;
   Button RegisterBtn;
   TextView LoginBtn;
   FirebaseAuth fAuth;
   FirebaseFirestore fStore;
   String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fullname=findViewById(R.id.yourfullname);
        Fathername=findViewById(R.id.yourfathername);
        Mothername=findViewById(R.id.yourmothername);
        Collegeusn=findViewById(R.id.USN);
        Emailid=findViewById(R.id.youremailid);
        Password=findViewById(R.id.Password);
        Dob=findViewById(R.id.yourdob);
        Address=findViewById(R.id.homeaddress);
        Branch=findViewById(R.id.Branch);
        Year=findViewById(R.id.Year);
        Classsection=findViewById(R.id.section);
        RegisterBtn=findViewById(R.id.submitform);
        LoginBtn=findViewById(R.id.loginform);

        fAuth =FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailid=Emailid.getText().toString().trim();
                String password=Password.getText().toString().trim();
                final String fullname=Fullname.getText().toString();
                final String fathername=Fathername.getText().toString();
                final String mothername=Mothername.getText().toString();
                final String collegeusn=Collegeusn.getText().toString();
                final String dob=Dob.getText().toString();
                final String address=Address.getText().toString();
                final String branch=Branch.getText().toString();
                final String year=Year.getText().toString();
                final String classsection=Classsection.getText().toString();

            if(TextUtils.isEmpty(emailid)){
                Emailid.setError("id is required");
                return;
            }

                if(TextUtils.isEmpty(password)){
                    Password.setError("Password is required");
                    return;
                }

                if(password.length()<6)
                {
                    Password.setError("Password must be more than 6 characters");
                    return;
                }

                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()) {
                          Toast.makeText(Register.this,"user created", Toast.LENGTH_SHORT).show();
                          userID= fAuth.getCurrentUser().getUid();
                           DocumentReference documentReference=fStore.collection( "users").document(userID);
                           Map<String,Object> user=new HashMap<>();
                           user.put("name",fullname);
                           user.put("email",emailid);
                           user.put("father",fathername);
                           user.put("mother",mothername);
                           user.put("USn",collegeusn);
                           user.put("dateOB",dob);
                           user.put("ADD",address);
                           user.put("BRNCH",branch);
                           user.put("yr",year);
                           user.put("SEC",classsection);
                           documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Log.d(ASK, "onSuccess: user profile is created for " + userID);
                               }
                           });
                         startActivity(new Intent(getApplicationContext(),MainActivity.class));
                       }else
                       {
                           Toast.makeText(Register.this,"error! " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                    }
                });
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}
