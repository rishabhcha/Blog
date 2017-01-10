package com.example.rishabh.blog;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectChallengerActivity extends AppCompatActivity {

    ListView mChallengerList;
    FloatingActionButton mSendChallengeBtn;

    private DatabaseReference mUserRef;
    private DatabaseReference mChallengeRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    private String selectedChallengerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_challenger);

        mChallengeRef = FirebaseDatabase.getInstance().getReference().child("ChallengeTo");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mChallengerList = (ListView) findViewById(R.id.challenger_list);
        mSendChallengeBtn = (FloatingActionButton) findViewById(R.id.sendChallengeButton);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Challenger");

        FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                SelectChallengerActivity.this,
                String.class,
                android.R.layout.simple_list_item_1,
                mUserRef
        ) {
            @Override
            protected void populateView(View v, String model, int position) {

                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model);

            }
        };

        mChallengerList.setAdapter(firebaseListAdapter);

        mChallengerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedChallengerName = adapterView.getItemAtPosition(i).toString();
                view.setBackgroundColor(Color.BLUE);


            }
        });

        mSendChallengeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(selectedChallengerName)){

                    final DatabaseReference newChallenge = mChallengeRef.push();

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newChallenge.child("challenger_pic").setValue(getIntent().getStringExtra("challengerSelectedPic"));
                            newChallenge.child("challenger_name").setValue(dataSnapshot.child("name").getValue());
                            newChallenge.child("acceptor_name").setValue(selectedChallengerName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(SelectChallengerActivity.this, "Wait For Challenger To Accept It", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SelectChallengerActivity.this,MainActivity.class));
                                        finish();

                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });

    }

}
