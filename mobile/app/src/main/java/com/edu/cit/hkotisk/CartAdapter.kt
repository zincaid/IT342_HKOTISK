package com.edu.cit.hkotisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edu.cit.hkotisk.data.model.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productSize: TextView = view.findViewById(R.id.product_size)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val quantity: TextView = view.findViewById(R.id.quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val context = holder.itemView.context
        
        holder.productName.text = cartItem.productName
        holder.productSize.text = "Size: ${cartItem.productSize}"
        holder.productPrice.text = "₱${cartItem.price}"
        holder.quantity.text = cartItem.quantity.toString()
        
        // Load image using Glide
        Glide.with(context)
            .load("http://10.0.2.2:8080/productImages/${cartItem.productId}.png")
            .centerCrop()
            .into(holder.productImage)
    }

    override fun getItemCount() = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
}
