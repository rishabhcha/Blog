package com.example.rishabh.blog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    ImageView mUserProfilePic;
    TextView mUserProfileName;

    DatabaseReference mDatabaseUsers;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();

        mUserProfilePic = (ImageView) findViewById(R.id.userProfilePic);
        mUserProfileName = (TextView) findViewById(R.id.userProfileName);

        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Picasso.with(UserProfileActivity.this).load((String) dataSnapshot.child("image").getValue()).into(mUserProfilePic);
                mUserProfileName.setText((String) dataSnapshot.child("name").getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
