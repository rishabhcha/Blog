package com.example.rishabh.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    ImageButton mProfilePic;
    EditText mProfileDisplayName;
    Button mFinishSetupBtn;

    Uri profilePicUri = null;

    private static final int GALLERY_REQUEST_CODE = 111;

    StorageReference mStorageProfile;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseUsers;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mStorageProfile = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mProfilePic = (ImageButton) findViewById(R.id.profilePic);
        mProfileDisplayName = (EditText) findViewById(R.id.profileDisplayName);
        mFinishSetupBtn = (Button) findViewById(R.id.finishSetupBtn);

        mProgress = new ProgressDialog(this);

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);

            }
        });

        mFinishSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finishProfileSetup();

            }
        });

    }

    private void finishProfileSetup() {

        final String name = mProfileDisplayName.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && profilePicUri!=null){

            mProgress.setMessage("Finishing Setup...");
            mProgress.show();

            StorageReference filepath = mStorageProfile.child("Profile_Pics").child(profilePicUri.getLastPathSegment());
            filepath.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri profilePicUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newUser = mDatabaseUsers.child(mAuth.getCurrentUser().getUid());
                    newUser.child("name").setValue(name);
                    newUser.child("image").setValue(profilePicUrl.toString());

                    mProgress.dismiss();

                    Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }
            });

        }else{
            Toast.makeText(SetupActivity.this, "Please Select All Fields", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK){

            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                profilePicUri = result.getUri();
                mProfilePic.setImageURI(profilePicUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
