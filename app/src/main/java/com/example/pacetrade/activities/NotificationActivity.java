package com.example.pacetrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.pacetrade.R;
import com.example.pacetrade.adapters.UserListAdapter;
import com.example.pacetrade.configs.Constants;
import com.example.pacetrade.models.NotificationData;
import com.example.pacetrade.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserListAdapter userListAdapter;
    List<NotificationData> notificationList;

    List<User> userList;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_wishlist).setChecked(true);


        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();

        userList = new ArrayList<>();

        loadNotifications();


    }

    /*
    * load user specific notifications
    * */
    private void loadNotifications() {

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_Notifications);

        final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS);


        Query query = databaseReference.orderByChild("to").equalTo(fUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String from = "" + ds.child("from").getValue();
                    String to = "" + ds.child("to").getValue();

                    Query query1 = databaseReferenceUser.orderByChild("uid").equalTo(from);
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                User user = ds.getValue(User.class);

                                userList.add(user);


                                userListAdapter = new UserListAdapter(NotificationActivity.this, userList);
                                recyclerView.setAdapter(userListAdapter);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

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
                    Intent intent = new Intent(NotificationActivity.this, TradeFeedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_search:
                    Intent intent2 = new Intent(NotificationActivity.this, UserListActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.nav_inventory:
                    Intent intent3 = new Intent(NotificationActivity.this, InventoryActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.nav_wishlist:
                    Intent intent4 = new Intent(NotificationActivity.this, NotificationActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.nav_profile:
                    Intent intent5 = new Intent(NotificationActivity.this, ProfileActivity.class);
                    startActivity(intent5);
                    break;

            }
            return false;
        }

    };

}
