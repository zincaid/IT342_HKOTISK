package com.edu.cit.hkotisk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.cit.hkotisk.databinding.ItemTopSellingBinding
import com.edu.cit.hkotisk.model.TopSellingItem

class TopSellingAdapter(private val topSellingItems: List<TopSellingItem>) : 
    RecyclerView.Adapter<TopSellingAdapter.TopSellingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopSellingViewHolder {
        val binding = ItemTopSellingBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return TopSellingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopSellingViewHolder, position: Int) {
        val item = topSellingItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = topSellingItems.size

    class TopSellingViewHolder(private val binding: ItemTopSellingBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: TopSellingItem) {
            binding.topSellingImage.setImageResource(item.imageResourceId)
            binding.topSellingName.text = item.name
        }
    }
} 