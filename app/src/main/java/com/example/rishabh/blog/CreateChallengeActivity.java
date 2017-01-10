package com.example.rishabh.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateChallengeActivity extends AppCompatActivity {

    ImageButton selectChallengePic;
    FloatingActionButton selectChallengerBtn;

    private static final int GALLERY_REQUEST_CODE = 198;

    Uri uri=null;

    ProgressDialog mDialog;

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        mStorage = FirebaseStorage.getInstance().getReference();

        selectChallengePic = (ImageButton) findViewById(R.id.selectChallengePic);
        selectChallengerBtn = (FloatingActionButton) findViewById(R.id.selectChallengerBtn);

        mDialog = new ProgressDialog(this);

        selectChallengePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);

            }
        });

        selectChallengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendChallenge();

            }
        });

    }

    private void sendChallenge() {

        if (uri!=null){

            mDialog.setMessage("Creating Challenge...");
            mDialog.show();

            StorageReference filepath = mStorage.child("Challenge_Images").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    mDialog.dismiss();
                    Intent selectChallenger = new Intent(CreateChallengeActivity.this,SelectChallengerActivity.class);
                    selectChallenger.putExtra("challengerSelectedPic",downloadUrl.toString());
                    startActivity(selectChallenger);


                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK){

            uri = data.getData();
            selectChallengePic.setImageURI(uri);

        }else{
            Toast.makeText(CreateChallengeActivity.this, "Some Error while taking photo", Toast.LENGTH_SHORT).show();
        }

    }
}
