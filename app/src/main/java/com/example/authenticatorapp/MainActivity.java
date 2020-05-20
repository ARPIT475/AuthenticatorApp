package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    EditText Fullname,Fathername,Mothername,Collegeusn,Emailid,Dob,Address,Branch,Year, Classsection;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button changeProfileImage;
    ImageView profileimage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        profileimage=findViewById(R.id.imageView);
        changeProfileImage=findViewById(R.id.profilechange);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        StorageReference profileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
             Picasso.get().load(uri).into(profileimage);
            }
        });

        userID=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Fullname.setText(documentSnapshot.getString("name"));
                Fathername.setText(documentSnapshot.getString("father"));
                Mothername.setText(documentSnapshot.getString("mother"));
                Collegeusn.setText(documentSnapshot.getString("USn"));
                Emailid.setText(documentSnapshot.getString("email"));
                Dob.setText(documentSnapshot.getString("dateOB"));
                Address.setText(documentSnapshot.getString("ADD"));
                Branch.setText(documentSnapshot.getString("BRNCH"));
                Year.setText(documentSnapshot.getString("yr"));
                Classsection.setText(documentSnapshot.getString("SEC"));
            }
        });


        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //open gallery
                Intent i=new Intent(v.getContext(),EditProfile.class);
                i.putExtra("fullname",Fullname.getText().toString());
                i.putExtra("emailid",Emailid.getText().toString());
                i.putExtra("fathername",Fathername.getText().toString());
                i.putExtra("mothername",Mothername.getText().toString());
                i.putExtra("collegeusn",Collegeusn.getText().toString());
                i.putExtra("dob",Dob.getText().toString());
                i.putExtra("address",Address.getText().toString());
                i.putExtra("branch",Branch.getText().toString());
                i.putExtra("year",Year.getText().toString());
                i.putExtra("classsection",Classsection.getText().toString());
                startActivity(i);
       //        Intent openGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         //      startActivityForResult(openGalleryIntent,1000);
            }
        });


    }





    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

}
