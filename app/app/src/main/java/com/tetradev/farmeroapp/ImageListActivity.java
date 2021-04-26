package com.tetradev.farmeroapp;


import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImageListActivity extends AppCompatActivity implements ImageListAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    //longpressed
    private long backPressedTime;
    private Toast backToast;

    //profilename
    private TextView profileName;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    //variables for navigation view
    DrawerLayout drawerLayout;
    NavigationView navigationView;


   

    public static final String  EXTRA_URL ="imageUrl";
    public static final String  EXTRA_PRODUCTNAME ="productName";
    public static final String  EXTRA_DESCRIPTION  ="Description";
    public static final String EXTRA_PRICE ="price";
    public static final String  EXTRA_CONTACT  ="contact";


    private RecyclerView mRecyclerView;
    private ImageListAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<ImageUpload> mUploads;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);




        //profile


        //firebase to retrieve data
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView profileName =(TextView)findViewById(R.id.user_name);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String username = userProfile.getUsername();
                    profileName.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImageListActivity.this,"Something happened",Toast.LENGTH_SHORT).show();

            }
        });

        //navigation
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);

        //navigation drawer
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);





        //fabbutton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(ImageListActivity.this,Cart.class);
                startActivity(cartIntent);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("image");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ImageUpload upload = postSnapshot.getValue(ImageUpload.class);
                    mUploads.add(upload);
                }
                mAdapter = new ImageListAdapter(ImageListActivity.this, mUploads);
                mRecyclerView.setAdapter(mAdapter);



                mAdapter.setOnItemClickListener(ImageListActivity.this);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImageListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);

            }

        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (backPressedTime + 2000 > System.currentTimeMillis() ){
                backToast.cancel();
                super.onBackPressed();
                return;
            }
            else{
            backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
            }
            backPressedTime = System.currentTimeMillis();

        }
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this,ProductDetail.class);

        ImageUpload clickedItem = mUploads.get(getItemCount() - (position + 1));

        detailIntent.putExtra(EXTRA_URL, clickedItem.getUrl());
        detailIntent.putExtra(EXTRA_PRODUCTNAME, clickedItem.getName());
        detailIntent.putExtra(EXTRA_DESCRIPTION, clickedItem.getDesc());
        detailIntent.putExtra(EXTRA_PRICE,clickedItem.getPrice());
        detailIntent.putExtra(EXTRA_CONTACT, clickedItem.getContact());

        startActivity(detailIntent);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.nav_signout:
                Intent intenthome = new Intent(ImageListActivity.this,MainActivity.class);

                startActivity(intenthome);
                finish();
                break;

            case R.id.nav_about:
                Intent intent = new Intent(ImageListActivity.this,aboutus.class);
                startActivity(intent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}

