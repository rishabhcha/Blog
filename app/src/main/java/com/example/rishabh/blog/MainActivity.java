package com.example.rishabh.blog;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    RecyclerView mBlogList;
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUsers;
    DatabaseReference mDatabaseLikes;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;

    boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLikes.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        checkUserExist();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.blog_row,
                PostViewHolder.class,
                mDatabase){
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.post_title.setText(model.getTitle());
                viewHolder.post_desc.setText(model.getDesc());
                Picasso.with(MainActivity.this).load(model.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.post_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                        Picasso.with(MainActivity.this).load(model.getImage()).into(viewHolder.post_image);

                    }
                });

                viewHolder.post_username.setText(model.getUsername());

                viewHolder.setLikeBtn(post_key);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent singleBlogIntent = new Intent(MainActivity.this,BlogSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singleBlogIntent);

                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;

                        mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                   if (mProcessLike){
                                       if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                                           mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                           mProcessLike = false;

                                       }else {

                                           mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RISHABH");
                                           mProcessLike = false;

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

        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView post_title;
        TextView post_desc;
        ImageView post_image;
        TextView post_username;
        ImageButton mLikeBtn;
        TextView mNoOfLikes;

        DatabaseReference mDatabaseLikes;
        FirebaseAuth mAuth;

        public PostViewHolder(View itemView) {
            super(itemView);

            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_desc = (TextView) itemView.findViewById(R.id.post_desc);
            post_image = (ImageView) itemView.findViewById(R.id.post_image);
            post_username = (TextView) itemView.findViewById(R.id.post_username);
            mLikeBtn = (ImageButton) itemView.findViewById(R.id.like_btn);
            mNoOfLikes = (TextView) itemView.findViewById(R.id.noOfLikesField);

            mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseLikes.keepSynced(true);
            mAuth = FirebaseAuth.getInstance();

        }

        public void setLikeBtn(final String post_key){

            mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mNoOfLikes.setText(String.valueOf((int) dataSnapshot.child(post_key).getChildrenCount()));

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        mLikeBtn.setImageResource(R.mipmap.ic_thumb_up_black_24dp);

                    }else{

                        mLikeBtn.setImageResource(R.mipmap.ic_thumb_up_white_24dp);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser()!=null){
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)){

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if (item.getItemId()==R.id.action_logout){
            logout();
        }
        if (item.getItemId()==R.id.action_profile){
            startActivity(new Intent(MainActivity.this,UserProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();

    }

    //handling on back pressed
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

}
