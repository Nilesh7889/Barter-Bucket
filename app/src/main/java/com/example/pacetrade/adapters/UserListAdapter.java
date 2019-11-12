package com.example.pacetrade.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pacetrade.activities.ChatActivity;
import com.example.pacetrade.R;
import com.example.pacetrade.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyHolder> {

    Context context;
    List<User> userList;

    public UserListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        final String userUid = userList.get(i).getUid();

        String userImage = userList.get(i).getProfilePictureUrl();
        String userName = userList.get(i).getFirstName();
        final String userEmail = userList.get(i).getEmail();

        myHolder.mUserListName.setText(userName);
        myHolder.mUserListEmail.setText(userEmail);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_userroww).into(myHolder.mUserPic);

        } catch (Exception e) {

        }

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userUid", userUid);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView mUserPic;
        TextView mUserListName;
        TextView mUserListEmail;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mUserPic = itemView.findViewById(R.id.userPic);
            mUserListName = itemView.findViewById(R.id.userListName);
            mUserListEmail = itemView.findViewById(R.id.userListEmail);

        }
    }
}
