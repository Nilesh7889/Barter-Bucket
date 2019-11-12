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
import com.example.pacetrade.adapters.UserListAdapter;
import com.example.pacetrade.configs.Constants;
import com.example.pacetrade.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserListAdapter userListAdapter;
    List<User> userList;

    FirebaseAuth firebaseAuth;

    //SearchView searchView;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.userListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_search).setChecked(true);


        setUpSearchView();

        getAllUsers();

    }

    /*
    * Set up user search
    * */
    private void setUpSearchView() {
        searchView = findViewById(R.id.searchUser);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUser(s);
                } else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUser(s);
                } else {
                    getAllUsers();
                }
                return false;
            }
        });
    }

    private void searchUser(final String query) {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    if (!user.getUid().equals(fUser.getUid())) {

                        if (user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                                user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(user);


                        }

                        userListAdapter = new UserListAdapter(UserListActivity.this, userList);
                        userListAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(userListAdapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*
    * Get all the registered users apart from current user.
     * */
    private void getAllUsers() {

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (!user.getUid().equals(fUser.getUid())) {
                        userList.add(user);
                    }

                    userListAdapter = new UserListAdapter(UserListActivity.this, userList);
                    recyclerView.setAdapter(userListAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    Intent intent = new Intent(UserListActivity.this, TradeFeedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_search:
                    Intent intent2 = new Intent(UserListActivity.this, UserListActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.nav_inventory:
                    Intent intent3 = new Intent(UserListActivity.this, InventoryActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.nav_wishlist:
                    Intent intent4 = new Intent(UserListActivity.this, NotificationActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.nav_profile:
                    Intent intent5 = new Intent(UserListActivity.this, ProfileActivity.class);
                    startActivity(intent5);
                    break;

            }
            return false;
        }

    };

}
