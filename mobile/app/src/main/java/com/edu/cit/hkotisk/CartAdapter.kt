package com.edu.cit.hkotisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edu.cit.hkotisk.data.model.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface CartItemListener {
        fun onQuantityChanged(productId: Int, newQuantity: Int)
        fun onRemoveItem(productId: Int)
    }

    private var listener: CartItemListener? = null

    fun setCartItemListener(listener: CartItemListener) {
        this.listener = listener
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val btnDecrease: ImageButton = view.findViewById(R.id.btn_decrease)
        val btnIncrease: ImageButton = view.findViewById(R.id.btn_increase)
        val btnRemove: ImageButton = view.findViewById(R.id.btn_remove)
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
        holder.productPrice.text = "₱${cartItem.price}"
        holder.quantity.text = cartItem.quantity.toString()
        
        // Load image using Glide
        Glide.with(context)
            .load("https://it342-hkotisk.onrender.com/productImages/${cartItem.productId}.png")
            .centerCrop()
            .into(holder.productImage)

        // Setup quantity controls
        holder.btnDecrease.isEnabled = cartItem.quantity > 1
        holder.btnDecrease.alpha = if (cartItem.quantity > 1) 1.0f else 0.5f

        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                listener?.onQuantityChanged(cartItem.productId, cartItem.quantity - 1)
            }
        }

        holder.btnIncrease.setOnClickListener {
            listener?.onQuantityChanged(cartItem.productId, cartItem.quantity + 1)
        }

        holder.btnRemove.setOnClickListener {
            listener?.onRemoveItem(cartItem.productId)
        }
    }

    override fun getItemCount() = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    fun getCurrentItems(): List<CartItem> {
        return cartItems
    }
}
