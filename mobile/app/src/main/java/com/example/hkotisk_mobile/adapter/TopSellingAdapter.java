package com.example.hkotisk_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hkotisk_mobile.R;
import com.example.hkotisk_mobile.model.TopSellingItem;

import java.util.List;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.TopSellingViewHolder> {
    private List<TopSellingItem> topSellingItems;

    public TopSellingAdapter(List<TopSellingItem> topSellingItems) {
        this.topSellingItems = topSellingItems;
    }

    @NonNull
    @Override
    public TopSellingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_selling, parent, false);
        return new TopSellingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopSellingViewHolder holder, int position) {
        TopSellingItem item = topSellingItems.get(position);
        holder.imageView.setImageResource(item.getImageResourceId());
        holder.nameTextView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return topSellingItems.size();
    }

    static class TopSellingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        TopSellingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.top_selling_image);
            nameTextView = itemView.findViewById(R.id.top_selling_name);
        }
    }
} 