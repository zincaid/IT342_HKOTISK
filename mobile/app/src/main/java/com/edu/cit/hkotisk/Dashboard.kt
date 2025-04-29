package com.edu.cit.hkotisk

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.Product
import com.edu.cit.hkotisk.data.model.ProductResponse
import com.edu.cit.hkotisk.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var productAdapter: ProductAdapter
    private var isActivityActive = true
    private var currentCall: Call<ProductResponse>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            setupRecyclerView()
            fetchProducts()
        }

        binding.bottomNavigation.selectedItemId = R.id.navigation_dashboard
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_orders -> {
                    startActivity(android.content.Intent(this, OrdersActivity::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(android.content.Intent(this, CartActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(android.content.Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList())
        binding.categoryContentRecycler.apply {
            layoutManager = GridLayoutManager(this@Dashboard, 2)
            adapter = productAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityActive = true
    }

    override fun onPause() {
        super.onPause()
        isActivityActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel any pending network requests
        currentCall?.cancel()
        binding.categoryContentRecycler.adapter = null
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle configuration changes without recreating the activity
        binding.categoryContentRecycler.layoutManager = GridLayoutManager(this, 2)
    }

    private fun fetchProducts() {
        if (!isActivityActive) return
        
        // Cancel any existing call
        currentCall?.cancel()
        
        // Store the new call
        currentCall = RetrofitClient.createProductService(applicationContext).getProducts()
        currentCall?.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                Log.d("Dashboard", "Raw Response: $response")
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    Log.d("Dashboard", "Raw Response: $productResponse")
                    
                    if (productResponse?.status == "200") {
                        val products = productResponse.oblist
                        // Store auth token in SharedPreferences
                        productResponse.auth_TOKEN?.let { token ->
                            getSharedPreferences("auth_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("token", token)
                                .apply()
                        }
                        if (products.isNotEmpty()) {
                            Log.d("Dashboard", "Products fetched successfully. Count: ${products.size}")
                            productAdapter = ProductAdapter(products)
                            binding.categoryContentRecycler.adapter = productAdapter
                        } else {
                            Log.d("Dashboard", "No products found in response")
                            if (isActivityActive) {
                                Toast.makeText(this@Dashboard, "No products available", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.e("Dashboard", "API Error: Status ${productResponse?.status}, Message: ${productResponse?.message}")
                        if (isActivityActive) {
                            Toast.makeText(this@Dashboard, "Error: ${productResponse?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("Dashboard", "Response not successful. Code: ${response.code()}, Error: $errorBody")
                    if (isActivityActive) {
                        Toast.makeText(this@Dashboard, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("Dashboard", "Network error", t)
                if (isActivityActive) {
                    Toast.makeText(this@Dashboard, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
