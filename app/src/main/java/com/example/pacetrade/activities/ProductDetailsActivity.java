package com.example.pacetrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pacetrade.R;
import com.example.pacetrade.adapters.ReviewAdapter;
import com.example.pacetrade.configs.Constants;
import com.example.pacetrade.models.Review;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    String itemId;

    FirebaseDatabase firebaseDatabase;

    ReviewAdapter reviewAdapter;

    DatabaseReference databaseReference;

    TextView itemNameTextView, uploaderNameTextView, itemStatusTextView;
    ImageView imageView;

    EditText commentEditTextView;

    Button tradeItemBtn, chatWithUserBtn;

    String uploaderId;
    RecyclerView recyclerView;

    List<Review> reviews;

    String reviewerFullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_PATH_UPLOADS);

        reviews = new ArrayList<>();


        itemNameTextView = findViewById(R.id.itemDetailsName);
        uploaderNameTextView = findViewById(R.id.uploaderName);
        itemStatusTextView = findViewById(R.id.itemStatusTextView);
        imageView = findViewById(R.id.imageView);

        commentEditTextView = findViewById(R.id.commentEditTextView);

        chatWithUserBtn = findViewById(R.id.chatWithUserBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");

        getItemDetails();
    }

    /*
    * Get details for the item
    * */
    private void getItemDetails() {

        Query query = databaseReference.orderByChild("itemId").equalTo(itemId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image = "" + ds.child("itemImageUrl").getValue();
                    String uploaderName = "" + ds.child("uploadedByFullName").getValue();
                    uploaderId = "" + ds.child("uploadedById").getValue();
                    String title = "" + ds.child("itemName").getValue();


                    itemNameTextView.setText(title);
                    uploaderNameTextView.setText(uploaderName);

                    if (ds.child("availableForTrade").getValue().equals(true)) {
                        itemStatusTextView.setText("Available");
                        itemStatusTextView.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        itemStatusTextView.setText("Out of Stock");
                        itemStatusTextView.setTextColor(getResources().getColor(R.color.red));

                    }


                    if (!image.isEmpty()) {
                        Picasso.get().load(image).into(imageView);
                    }

                }

                getReviewsForItem(itemId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /*
    * Get reviews for item
    * */
    private void getReviewsForItem(final String itemId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_REVIEWS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                reviews.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Review review = ds.getValue(Review.class);


                    if (review.getItemId().equals(itemId)) {
                        reviews.add(review);

                    }

                    reviewAdapter = new ReviewAdapter(ProductDetailsActivity.this, reviews);
                    reviewAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(reviewAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void initiateChatWithUser(View view) {

        makeChatNotificationEntry();

        Intent intent = new Intent(ProductDetailsActivity.this, ChatActivity.class);
        intent.putExtra("userUid", uploaderId);
        startActivity(intent);

    }

    public void tradeItem(View view) {


    }

    /*
    * Adds an entry to the notification table to notify the user about chat from new user
    * */
    private void makeChatNotificationEntry() {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();

        DatabaseReference reference = firebaseDatabase.getReference(Constants.DATABASE_PATH_Notifications);

        NotificationData notificationData = new NotificationData(currentUserId, uploaderId);
        reference.child(uploaderId).setValue(notificationData);


    }

    public void addComment(View v) {
        final String commentString = commentEditTextView.getText().toString();

        if (commentString.trim().isEmpty()) {
            Toast.makeText(ProductDetailsActivity.this, getString(R.string.review_error), Toast.LENGTH_SHORT).show();

            return;
        }

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference databaseReferenceReviews = firebaseDatabase.getReference(Constants.DATABASE_PATH_REVIEWS);
        Query getUserDetailsByUserIdQuery = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS).orderByChild("uid").equalTo(fUser.getUid());
        getUserDetailsByUserIdQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //mUploads.clear();
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    User user = postsnap.getValue(User.class);
                    reviewerFullName = user.getFirstName() + " " + user.getLastName();
                }

                //saveFileToDatabse();
                final Review review = new Review(commentString, fUser.getUid(), itemId, reviewerFullName);
                String reviewId = databaseReferenceReviews.push().getKey();
                databaseReferenceReviews.child(reviewId).setValue(review);

                commentEditTextView.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
