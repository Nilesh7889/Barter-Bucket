package com.example.pacetrade.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pacetrade.activities.ProductDetailsActivity;
import com.example.pacetrade.R;
import com.example.pacetrade.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TradeFeedAdapter extends RecyclerView.Adapter<TradeFeedAdapter.ViewHolder> {

    private Context context;
    private List<Product> uploads;

    public TradeFeedAdapter(Context context, List<Product> uploads) {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_trade_feed_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product upload = uploads.get(position);


        holder.textViewName.setText(upload.getItemName());
        holder.uploaderNameTextView.setText("By: "+upload.getUploadedByFullName());

        Picasso.get().load(upload.getItemImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, ProductDetailsActivity.class);
                intent.putExtra("itemId", upload.getItemId());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView uploaderNameTextView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            uploaderNameTextView = (TextView) itemView.findViewById(R.id.uploaderName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}

