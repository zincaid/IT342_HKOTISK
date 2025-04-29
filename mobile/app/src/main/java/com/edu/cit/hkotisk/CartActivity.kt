package com.edu.cit.hkotisk

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.GetCartResponse
import com.edu.cit.hkotisk.data.model.OrderResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {
    private lateinit var checkoutButton: Button
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartRecyclerView: RecyclerView
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
        cartAdapter = CartAdapter(emptyList())
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

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCheckoutButton() {
        checkoutButton = findViewById(R.id.checkout_button)
        checkoutButton.setOnClickListener {
            checkoutButton.isEnabled = false
            RetrofitClient.createProductService(applicationContext).createOrder()
                .enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CartActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                            // Navigate to Orders screen
                            startActivity(android.content.Intent(this@CartActivity, OrdersActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@CartActivity, "Failed to place order", Toast.LENGTH_SHORT).show()
                            checkoutButton.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        Toast.makeText(this@CartActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
}
