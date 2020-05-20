package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText Emailid,Password;
    Button Loginbtn;
    TextView Createbtn,forgotTextLink;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Emailid=findViewById(R.id.youremailid);
        Password=findViewById(R.id.password);
        fAuth=FirebaseAuth.getInstance();
        Loginbtn=findViewById(R.id.lform);
        Createbtn=findViewById(R.id.newform);
        forgotTextLink=findViewById(R.id.forget);

        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailid=Emailid.getText().toString().trim();
                String password=Password.getText().toString().trim();

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
//authenticate the user
                fAuth.signInWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(Login.this,"error! " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

       forgotTextLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final EditText resetMail= new EditText(v.getContext());
               AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(v.getContext());
               passwordResetDialog.setTitle("reset password");
               passwordResetDialog.setMessage("enter your email to recieve reset link ");
               passwordResetDialog.setView(resetMail);
               passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //extract the mail and send the new one

                       String email=resetMail.getText().toString();
                       fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(Login.this,"reset link sent to your mail",Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Login.this,"reset link not sent to your mail"+e.getMessage(),Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });

                passwordResetDialog.create().show();
           }
       });
    }
}
