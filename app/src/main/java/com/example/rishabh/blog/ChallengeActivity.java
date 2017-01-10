package com.example.rishabh.blog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ChallengeActivity extends AppCompatActivity {

    RecyclerView mChallengeList;
    private boolean mProcessLike;

    private DatabaseReference mChallengeRef;
    private DatabaseReference mDatabaseLikes1;
    private DatabaseReference mDatabaseLikes2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mChallengeRef = FirebaseDatabase.getInstance().getReference().child("Challenge");
        mChallengeRef.keepSynced(true);
        mDatabaseLikes1 = FirebaseDatabase.getInstance().getReference().child("Challenger_Likes");
        mDatabaseLikes1.keepSynced(true);
        mDatabaseLikes2 = FirebaseDatabase.getInstance().getReference().child("Acceptor_Likes");
        mDatabaseLikes2.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        mChallengeList = (RecyclerView) findViewById(R.id.challenge_list);
        mChallengeList.setHasFixedSize(true);
        mChallengeList.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference removeChallengeTo = FirebaseDatabase.getInstance().getReference().child("ChallengeTo");
        removeChallengeTo.child(getIntent().getStringExtra("challenge_key")).removeValue();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Challenge,ChallengeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder>(
                Challenge.class,
                R.layout.challenge_row,
                ChallengeViewHolder.class,
                mChallengeRef
        ) {
            @Override
            protected void populateViewHolder(final ChallengeViewHolder viewHolder, final Challenge model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.challenge_challenger_name.setText(model.getChallenger_name());
                viewHolder.challenge_acceptor_name.setText(model.getAcceptor_name());
                Picasso.with(ChallengeActivity.this).load(model.getChallenger_pic()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.challenge_challenger_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                        Picasso.with(ChallengeActivity.this).load(model.getChallenger_pic()).into(viewHolder.challenge_challenger_pic);

                    }
                });
                Picasso.with(ChallengeActivity.this).load(model.getAcceptor_pic()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.challenge_acceptor_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                        Picasso.with(ChallengeActivity.this).load(model.getAcceptor_pic()).into(viewHolder.challenge_acceptor_pic);

                    }
                });
                viewHolder.countLikes(post_key);
                viewHolder.challenge_likebtn_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;

                        mDatabaseLikes1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike){
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                                        mDatabaseLikes1.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                        viewHolder.challenge_likebtn_2.setEnabled(true);

                                    }else {

                                        mDatabaseLikes1.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RISHABH");
                                        mProcessLike = false;
                                        viewHolder.challenge_likebtn_2.setEnabled(false);


                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder.challenge_likebtn_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;

                        mDatabaseLikes2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike){
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                                        mDatabaseLikes2.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                        viewHolder.challenge_likebtn_1.setEnabled(true);

                                    }else {

                                        mDatabaseLikes2.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RISHABH");
                                        mProcessLike = false;
                                        viewHolder.challenge_likebtn_1.setEnabled(false);

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        mChallengeList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {

        ImageView challenge_challenger_pic;
        ImageView challenge_acceptor_pic;
        TextView challenge_challenger_name;
        TextView challenge_acceptor_name;
        ImageButton challenge_likebtn_1;
        ImageButton challenge_likebtn_2;
        TextView challenge_like_1;
        TextView challenge_like_2;

        DatabaseReference mDatabaseLikes1;
        DatabaseReference mDatabaseLikes2;
        FirebaseAuth mAuth;

        public ChallengeViewHolder(View itemView) {
            super(itemView);

            challenge_challenger_pic = (ImageView) itemView.findViewById(R.id.challenge_challenger_pic);
            challenge_acceptor_pic = (ImageView) itemView.findViewById(R.id.challenge_acceptor_pic);
            challenge_challenger_name = (TextView) itemView.findViewById(R.id.challenge_challenger_name);
            challenge_acceptor_name = (TextView) itemView.findViewById(R.id.challenge_acceptor_name);
            challenge_likebtn_1 = (ImageButton) itemView.findViewById(R.id.challenge_likebtn_1);
            challenge_likebtn_2 = (ImageButton) itemView.findViewById(R.id.challenge_likebtn_2);
            challenge_like_1 = (TextView) itemView.findViewById(R.id.challenge_like_1);
            challenge_like_2 = (TextView) itemView.findViewById(R.id.challenge_like_2);

            mAuth = FirebaseAuth.getInstance();
            mDatabaseLikes1 = FirebaseDatabase.getInstance().getReference().child("Challenger_Likes");
            mDatabaseLikes1.keepSynced(true);
            mDatabaseLikes2 = FirebaseDatabase.getInstance().getReference().child("Acceptor_Likes");
            mDatabaseLikes2.keepSynced(true);

        }

        public void countLikes(final String post_key) {

            mDatabaseLikes1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    challenge_like_1.setText(String.valueOf((int) dataSnapshot.child(post_key).getChildrenCount()));

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        challenge_likebtn_1.setImageResource(R.mipmap.ic_favorite_black_24dp);

                    }else{

                        challenge_likebtn_1.setImageResource(R.mipmap.ic_favorite_border_black_24dp);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabaseLikes2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    challenge_like_2.setText(String.valueOf((int) dataSnapshot.child(post_key).getChildrenCount()));

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        challenge_likebtn_2.setImageResource(R.mipmap.ic_favorite_black_24dp);

                    }else{

                        challenge_likebtn_2.setImageResource(R.mipmap.ic_favorite_border_black_24dp);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}
