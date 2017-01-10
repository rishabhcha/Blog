package com.example.rishabh.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AcceptChallenge extends AppCompatActivity {

    private RecyclerView accept_challenge_list;
    public static final int GALLERY_REQUEST_CODE = 1098;
    Uri uri = null;
    ProgressDialog mDialog;

    private DatabaseReference mChallengeRef;
    private DatabaseReference mChallenge;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_challenge);

        accept_challenge_list = (RecyclerView) findViewById(R.id.accept_challenge_list);
        accept_challenge_list.setHasFixedSize(true);
        accept_challenge_list.setLayoutManager(new LinearLayoutManager(this));

        mChallengeRef = FirebaseDatabase.getInstance().getReference().child("ChallengeTo");
        mChallengeRef.keepSynced(true);
        mChallenge = FirebaseDatabase.getInstance().getReference().child("Challenge");
        mChallenge.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDialog = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Accept,AcceptViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Accept, AcceptViewHolder>(
                Accept.class,
                R.layout.accept_challenge_row,
                AcceptViewHolder.class,
                mChallengeRef
        ) {
            @Override
            protected void populateViewHolder(final AcceptViewHolder viewHolder, final Accept model, int position) {

                final String challenge_key = getRef(position).getKey();

                viewHolder.accept_challenger_name.setText(model.getChallenger_name());
                Picasso.with(AcceptChallenge.this).load(model.getChallenger_pic()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.accept_challenger_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                        Picasso.with(AcceptChallenge.this).load(model.getChallenger_pic()).into(viewHolder.accept_challenger_pic);

                    }
                });
                viewHolder.accept_acceptor_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);
                        if (uri!=null){
                            viewHolder.accept_acceptor_pic.setImageURI(uri);
                        }

                    }
                });
                viewHolder.accept_challenge_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        acceptChallenge(challenge_key);

                    }
                });

            }
        };

        accept_challenge_list.setAdapter(firebaseRecyclerAdapter);

    }

    private void acceptChallenge(final String challenge_key) {

        if (uri!=null){

            mDialog.setMessage("Shutting up the challenger... ");
            mDialog.show();

            StorageReference filepath = mStorage.child("Challenge_Images").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mChallengeRef.child(challenge_key).child("acceptor_pic").setValue(downloadUrl.toString());
                    final DatabaseReference newChallenge = mChallenge.push();

                    mChallengeRef.child(challenge_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newChallenge.child("acceptor_name").setValue(dataSnapshot.child("acceptor_name").getValue());
                            newChallenge.child("acceptor_pic").setValue(downloadUrl.toString());
                            newChallenge.child("challenger_name").setValue(dataSnapshot.child("challenger_name").getValue());
                            newChallenge.child("challenger_pic").setValue(dataSnapshot.child("challenger_pic").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        mDialog.dismiss();
                                        Intent intent = new Intent(AcceptChallenge.this,ChallengeActivity.class);
                                        intent.putExtra("challenge_key",challenge_key);
                                        startActivity(intent);
                                        //mChallengeRef.child(challenge_key).removeValue();
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
            });

        }


    }

    public static class AcceptViewHolder extends RecyclerView.ViewHolder {

        ImageButton accept_challenger_pic;
        ImageButton accept_acceptor_pic;
        TextView accept_challenger_name;
        FloatingActionButton accept_challenge_btn;

        public AcceptViewHolder(View itemView) {
            super(itemView);

            accept_challenger_pic = (ImageButton) itemView.findViewById(R.id.accept_challenger_pic);
            accept_acceptor_pic = (ImageButton) itemView.findViewById(R.id.accept_acceptor_pic);
            accept_challenger_name = (TextView) itemView.findViewById(R.id.accept_challenger_name);
            accept_challenge_btn = (FloatingActionButton) itemView.findViewById(R.id.accept_challenge_btn);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK){

            uri = data.getData();

        }else{
            Toast.makeText(AcceptChallenge.this, "Some Error while taking photo", Toast.LENGTH_SHORT).show();
        }


    }
}
