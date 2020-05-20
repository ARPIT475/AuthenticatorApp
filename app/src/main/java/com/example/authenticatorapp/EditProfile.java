package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText Fullname,Fathername,Mothername,Collegeusn,Emailid,Dob,Address,Branch,Year, Classsection;
    ImageView profileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data=getIntent();
        final String fullname=data.getStringExtra("fullname");
        final String emailid=data.getStringExtra("emailid");
        final String fathername=data.getStringExtra("fathername");
        final String mothername=data.getStringExtra("mothername");
        final String collegeusn=data.getStringExtra("collegeusn");
        final String dob=data.getStringExtra("dob");
        final String address=data.getStringExtra("address");
        final String branch=data.getStringExtra("branch");
        final String year=data.getStringExtra("year");
        final String classsection=data.getStringExtra("classsection");

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        user=fAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        Fullname=findViewById(R.id.profilefullname);
        Fathername=findViewById(R.id.profilefathername);
        Mothername=findViewById(R.id.profilemothername);
        Collegeusn=findViewById(R.id.profileUSN);
        Emailid=findViewById(R.id.profileemailid);
        Dob=findViewById(R.id.profiledob);
        Address=findViewById(R.id.profilehomeaddress);
        Branch=findViewById(R.id.profileBranch);
        Year=findViewById(R.id.profileYear);
        Classsection=findViewById(R.id.profilesection);
        profileImageView=findViewById(R.id.imageView);
        saveBtn=findViewById(R.id.saveform);

        StorageReference profileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Fullname.getText().toString().isEmpty()|| Fathername.getText().toString().isEmpty()|| Mothername.getText().toString().isEmpty()|| Collegeusn.getText().toString().isEmpty()|| Emailid.getText().toString().isEmpty()|| Dob.getText().toString().isEmpty()|| Address.getText().toString().isEmpty()|| Year.getText().toString().isEmpty()|| Branch.getText().toString().isEmpty()|| Classsection.getText().toString().isEmpty())
                {
                    Toast.makeText(EditProfile.this,"one or many fields are empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                final String emailid= Emailid.getText().toString();
                user.updateEmail(emailid).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef=fStore.collection("users").document(user.getUid());
                        Map<String,Object> edited=new HashMap<>();
                        edited.put("email",emailid);
                        edited.put("name",Fullname.getText().toString());
                        edited.put("father",Fathername.getText().toString());
                        edited.put("mother",Mothername.getText().toString());
                        edited.put("dateOB", Dob.getText().toString());
                        edited.put("ADD",Address.getText().toString().isEmpty());
                        edited.put("BRNCH",Branch.getText().toString());
                        edited.put("yr",Year.getText().toString());
                        edited.put("USn",Collegeusn.getText().toString());
                        edited.put("SEC",Classsection.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this,"profile",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(EditProfile.this,"email is changed",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        Fullname.setText(fullname);
        Fathername.setText(fathername);
        Mothername.setText(mothername);
        Collegeusn.setText(collegeusn);
        Emailid.setText(emailid);
        Dob.setText(dob);
        Address.setText(address);
        Branch.setText(branch);
        Year.setText(year);
        Classsection.setText(classsection);


        Log.d(TAG,"onCreate: "+fullname+ " "+emailid+ " "+fathername+ " "+mothername+ " "+collegeusn+ " "+dob+ " "+address+ " "+branch+ " "+year+ " "+classsection+ " ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                Uri imageUri=data.getData();

                // profileimage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);

            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        final StorageReference fileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"image not uploaded",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
