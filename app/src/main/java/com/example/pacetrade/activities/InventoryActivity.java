package com.example.pacetrade.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pacetrade.R;
import com.example.pacetrade.configs.Constants;
import com.example.pacetrade.models.Product;
import com.example.pacetrade.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicLong;

public class InventoryActivity extends AppCompatActivity {

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();

    FirebaseAuth firebaseAuth;

    final static int PICK_IMAGE_REQUEST = 1;
    private Button mBtnchooseImage;
    private Button mBtnnUpload;
    private EditText mTextItemName;
    private ImageView mImageItem;
    private Uri mImageUri;

    private StorageReference mStrgRef;
    private DatabaseReference mDataRef;

    private StorageTask mUploadTask;

    String mLooggedInUserId;
    String mUserFullName;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        mLooggedInUserId = firebaseAuth.getCurrentUser().getUid();

        mBtnchooseImage = findViewById(R.id.chooseFile);
        mBtnnUpload = findViewById(R.id.uploadFile);
        mTextItemName = findViewById(R.id.uploadItemName);
        mImageItem = findViewById(R.id.demoImage);

        mStrgRef = FirebaseStorage.getInstance().getReference("uploads");
        mDataRef = FirebaseDatabase.getInstance().getReference("uploads");

        mBtnchooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_inventory).setChecked(true);


        mBtnnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(InventoryActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadfile();
                }
            }
        });

    }

    private void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageItem);
        }
    }

    /*
    * Gets the extension of file
    * */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /*
    * Upload the file to database nd storage
    * */
    private void uploadfile() {
        String itemName = mTextItemName.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(InventoryActivity.this, "Please Enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri != null) {
            //displaying progress dialog while image is uploading
            //progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            //FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS);
            Query getUserDetailsByUserIdQuery = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS).orderByChild("uid").equalTo(mLooggedInUserId);


            //mDBListner = getUploadsByUserIdQuery.addValueEventListener(new ValueEventListener() {
            getUserDetailsByUserIdQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                        User user = postsnap.getValue(User.class);
                        mUserFullName = user.getFirstName() + " " + user.getLastName();
                    }

                    saveFileToDatabse();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(InventoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileToDatabse() {

        final StorageReference fileReference = mStrgRef.child(System.currentTimeMillis() + " " + getFileExtension(mImageUri));


        fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();
                        Long id = uniqueCurrentTimeMS();
                        Product upload = new Product(mTextItemName.getText().toString().trim(),
                                uri.toString(),
                                mLooggedInUserId,
                                mUserFullName,
                                id.toString());

                        //adding an upload to firebase database
                        // String uploadId = mDataRef.push().getKey();
                        mDataRef.child(id.toString()).setValue(upload);

                        mTextItemName.setText("");
                        mImageUri = null;
                        mImageItem.setImageDrawable(null);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //displaying the upload progress
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    Intent intent = new Intent(InventoryActivity.this, TradeFeedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_search:
                    Intent intent2 = new Intent(InventoryActivity.this, UserListActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.nav_inventory:
                    Intent intent3 = new Intent(InventoryActivity.this, InventoryActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.nav_wishlist:
                    Intent intent4 = new Intent(InventoryActivity.this, NotificationActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.nav_profile:
                    Intent intent5 = new Intent(InventoryActivity.this, ProfileActivity.class);
                    startActivity(intent5);
                    break;

            }
            return false;
        }

    };

    public static long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while (true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

}
