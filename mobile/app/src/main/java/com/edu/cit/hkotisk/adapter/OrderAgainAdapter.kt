package com.edu.cit.hkotisk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.cit.hkotisk.databinding.ItemOrderAgainBinding
import com.edu.cit.hkotisk.model.OrderAgainItem

class OrderAgainAdapter(private val orderAgainItems: List<OrderAgainItem>) : 
    RecyclerView.Adapter<OrderAgainAdapter.OrderAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAgainViewHolder {
        val binding = ItemOrderAgainBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return OrderAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAgainViewHolder, position: Int) {
        val item = orderAgainItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = orderAgainItems.size

    class OrderAgainViewHolder(private val binding: ItemOrderAgainBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: OrderAgainItem) {
            binding.orderAgainImage.setImageResource(item.imageResourceId)
            binding.orderAgainName.text = item.name
            binding.orderAgainAvailability.text = "Available: ${item.available}"
            binding.orderAgainSold.text = "Sold: ${item.sold}"
            binding.orderAgainPrice.text = String.format("₱%.2f", item.price)
        }
    }
} 