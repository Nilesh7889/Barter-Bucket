package com.example.pacetrade.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pacetrade.R;
import com.example.pacetrade.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder> {


    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    public ChatAdapter(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        if (i==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, viewGroup, false);
            return new MyHolder(view);
        }
        else {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, viewGroup, false);
            return new MyHolder(view);

        }


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        String message = chatList.get(i).getMessage();
        myHolder.messageTv.setText(message);
        try {
            Picasso.get().load(imageUrl).into(myHolder.chatIcon);

        }catch (Exception e){

        }
        if (i==chatList.size()-1) {
            if (chatList.get(i).isSeen()) {
                myHolder.isSeenTv.setText("Seen");
            } else {
                myHolder.isSeenTv.setText("Delivered");
            }
        }
        else {

            myHolder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView chatIcon;
        TextView messageTv, isSeenTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            chatIcon = itemView.findViewById(R.id.chatIcon);
            messageTv = itemView.findViewById(R.id.messageTv);
            isSeenTv = itemView.findViewById(R.id.seenTv);

        }
    }
}
