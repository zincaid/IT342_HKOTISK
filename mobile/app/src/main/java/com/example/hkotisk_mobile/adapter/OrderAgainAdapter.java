package com.example.hkotisk_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hkotisk_mobile.R;
import com.example.hkotisk_mobile.model.OrderAgainItem;

import java.util.List;

public class OrderAgainAdapter extends RecyclerView.Adapter<OrderAgainAdapter.OrderAgainViewHolder> {
    private List<OrderAgainItem> orderAgainItems;

    public OrderAgainAdapter(List<OrderAgainItem> orderAgainItems) {
        this.orderAgainItems = orderAgainItems;
    }

    @NonNull
    @Override
    public OrderAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_again, parent, false);
        return new OrderAgainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAgainViewHolder holder, int position) {
        OrderAgainItem item = orderAgainItems.get(position);
        holder.imageView.setImageResource(item.getImageResourceId());
        holder.nameTextView.setText(item.getName());
        holder.availabilityTextView.setText("Available: " + item.getAvailable());
        holder.soldTextView.setText("Sold: " + item.getSold());
        holder.priceTextView.setText(String.format("₱%.2f", item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderAgainItems.size();
    }

    static class OrderAgainViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView availabilityTextView;
        TextView soldTextView;
        TextView priceTextView;

        OrderAgainViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.order_again_image);
            nameTextView = itemView.findViewById(R.id.order_again_name);
            availabilityTextView = itemView.findViewById(R.id.order_again_availability);
            soldTextView = itemView.findViewById(R.id.order_again_sold);
            priceTextView = itemView.findViewById(R.id.order_again_price);
        }
    }
} 