package com.example.pacetrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.pacetrade.R;
import com.example.pacetrade.adapters.TradeFeedAdapter;
import com.example.pacetrade.configs.Constants;
import com.example.pacetrade.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;

import com.google.firebase.database.Query;

public class TradeFeedActivity extends AppCompatActivity {
    //recyclerview object
    private RecyclerView recyclerView;

    //adapter object
    private TradeFeedAdapter adapter;

    //database reference
    private DatabaseReference mDatabase;

    SearchView searchView;


    //progress dialog
    private ProgressDialog progressDialog;

    //list to hold all the uploaded images
    private List<Product> uploads;

    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_feed);

        searchView = findViewById(R.id.searchItem);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uploads = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        setupSearchItem();
    }

    private void setupSearchItem() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchItem(s);
                } else {
                    getAllItem();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchItem(s);
                } else {
                    getAllItem();
                }
                return false;
            }
        });


        getAllItem();

    }

    /*
    * Set up item search
    * */
    private void searchItem(final String query) {


        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                uploads.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);

                    if (product.getItemName().contains(query)) {
                        if (!product.getUploadedById().equals(fUser.getUid())) {
                            uploads.add(product);
                        }

                    }
                }

                adapter = new TradeFeedAdapter(TradeFeedActivity.this, uploads);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /*
    * Get all items apart from the current users items.
    * */
    private void getAllItem() {

        progressDialog = new ProgressDialog(this);


        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        Query query = mDatabase.child(Constants.DATABASE_PATH_UPLOADS);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                uploads.clear();

                //dismissing the progress dialog
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product upload = postSnapshot.getValue(Product.class);

                    if (!upload.getUploadedById().equals(fUser.getUid())) {
                        uploads.add(upload);
                    }

                }

                //creating adapter
                adapter = new TradeFeedAdapter(getApplicationContext(), uploads);

                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error");
                progressDialog.dismiss();
            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    Intent intent = new Intent(TradeFeedActivity.this, TradeFeedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_search:
                    Intent intent2 = new Intent(TradeFeedActivity.this, UserListActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.nav_inventory:
                    Intent intent3 = new Intent(TradeFeedActivity.this, InventoryActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.nav_wishlist:
                    Intent intent4 = new Intent(TradeFeedActivity.this, NotificationActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.nav_profile:
                    Intent intent5 = new Intent(TradeFeedActivity.this, ProfileActivity.class);
                    startActivity(intent5);
                    break;

            }
            return false;
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}