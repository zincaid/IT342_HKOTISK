package com.edu.cit.hkotisk

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity(), CartAdapter.CartItemListener {
    private lateinit var checkoutButton: Button
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalAmountTextView: TextView
    private var currentCall: Call<GetCartResponse>? = null
    private var isActivityActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setupRecyclerView()
        setupCheckoutButton()
        loadCartItems()

        // Navigation setup
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_cart
        
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    finish()
                    true
                }
                R.id.navigation_orders -> {
                    startActivity(android.content.Intent(this, OrdersActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(android.content.Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        cartRecyclerView = findViewById(R.id.cart_recycler)
        totalAmountTextView = findViewById(R.id.total_amount_value)
        cartAdapter = CartAdapter(emptyList())
        cartAdapter.setCartItemListener(this)
        cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityActive = true
        loadCartItems()
    }

    override fun onPause() {
        super.onPause()
        isActivityActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        currentCall?.cancel()
        cartRecyclerView.adapter = null
    }

    override fun onQuantityChanged(productId: Int, newQuantity: Int) {
        val service = RetrofitClient.createProductService(applicationContext)
        val currentItem = getCurrentCartItem(productId)
        currentItem?.let { item ->
            if (newQuantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show()
                loadCartItems() // Refresh to show original quantity
                return@let
            }

            val cartRequest = CartRequest(productId = productId, quantity = newQuantity, price = item.price)
            Log.d("CartActivity", "Updating cart with request: $cartRequest")

            service.updateCartQuantity(cartRequest).enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { cartResponse ->
                            Log.d("CartActivity", "Cart update successful: ${cartResponse.message}")
                            if (cartResponse.status == "success") {
                                loadCartItems()
                            } else {
                                Toast.makeText(this@CartActivity, cartResponse.message, Toast.LENGTH_SHORT).show()
                                loadCartItems() // Refresh to show original quantity
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CartActivity", "Failed to update cart: $errorBody")
                        Toast.makeText(this@CartActivity, 
                            "Failed to update quantity. Please try again.", 
                            Toast.LENGTH_SHORT).show()
                        loadCartItems() // Refresh to show original quantity
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Log.e("CartActivity", "Cart update error", t)
                    Toast.makeText(this@CartActivity, 
                        "Network error: Please check your connection", 
                        Toast.LENGTH_SHORT).show()
                    loadCartItems() // Refresh to show original quantity
                }
            })
        }
    }

    override fun onRemoveItem(productId: Int) {
        val service = RetrofitClient.createProductService(applicationContext)
        val currentItem = getCurrentCartItem(productId)
        currentItem?.let { item ->
            val cartRequest = CartRequest(productId = productId, quantity = 0, price = item.price)
            Log.d("CartActivity", "Removing item from cart: $cartRequest")

            service.removeFromCart(cartRequest).enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { cartResponse ->
                            Log.d("CartActivity", "Remove item response: ${cartResponse.message}")
                            if (cartResponse.status == "success") {
                                loadCartItems()
                            } else {
                                Toast.makeText(this@CartActivity, cartResponse.message, Toast.LENGTH_SHORT).show()
                                loadCartItems() // Refresh cart
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CartActivity", "Failed to remove item: $errorBody")
                        Toast.makeText(this@CartActivity, 
                            "Failed to remove item. Please try again.", 
                            Toast.LENGTH_SHORT).show()
                        loadCartItems() // Refresh cart
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Log.e("CartActivity", "Remove item error", t)
                    Toast.makeText(this@CartActivity, 
                        "Network error: Please check your connection", 
                        Toast.LENGTH_SHORT).show()
                    loadCartItems() // Refresh cart
                }
            })
        }
    }

    private fun getCurrentCartItem(productId: Int): CartItem? {
        return cartAdapter.getCurrentItems().find { it.productId == productId }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCheckoutButton() {
        checkoutButton = findViewById(R.id.checkout_button)
        checkoutButton.setOnClickListener {
            val cartItems = cartAdapter.getCurrentItems()
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkoutButton.isEnabled = false
            Log.d("CartActivity", "Starting checkout process")
            
            RetrofitClient.createProductService(applicationContext).createOrder()
                .enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { orderResponse ->
                                Log.d("CartActivity", "Order created successfully")
                                Toast.makeText(this@CartActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                                // Navigate to Orders screen
                                startActivity(android.content.Intent(this@CartActivity, OrdersActivity::class.java))
                                finish()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("CartActivity", "Failed to place order: $errorBody")
                            Toast.makeText(this@CartActivity, 
                                "Failed to place order. Please try again.", 
                                Toast.LENGTH_SHORT).show()
                            checkoutButton.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        Log.e("CartActivity", "Order creation error", t)
                        Toast.makeText(this@CartActivity, 
                            "Network error: Please check your connection", 
                            Toast.LENGTH_SHORT).show()
                        checkoutButton.isEnabled = true
                    }
                })
        }
    }

    private fun loadCartItems() {
        if (!isActivityActive) return

        // Cancel any existing call
        currentCall?.cancel()

        // Store the new call
        currentCall = RetrofitClient.createProductService(applicationContext).getCart()
        currentCall?.enqueue(object : Callback<GetCartResponse> {
            override fun onResponse(call: Call<GetCartResponse>, response: Response<GetCartResponse>) {
                Log.d("CartActivity", "Raw Response: $response")
                if (response.isSuccessful) {
                    response.body()?.oblist?.let { items ->
                        cartAdapter.updateItems(items)
                        updateTotalAmount(items)
                    }
                } else {
                    if (isActivityActive) {
                        Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetCartResponse>, t: Throwable) {
                if (isActivityActive) {
                    Toast.makeText(this@CartActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateTotalAmount(items: List<CartItem>) {
        val total = items.sumOf { item -> item.price * item.quantity }
        totalAmountTextView.text = String.format("₱%.2f", total)
    }
}
