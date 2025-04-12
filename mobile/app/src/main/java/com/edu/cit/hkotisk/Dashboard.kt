package com.edu.cit.hkotisk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.cit.hkotisk.adapter.OrderAgainAdapter
import com.edu.cit.hkotisk.adapter.TopSellingAdapter
import com.edu.cit.hkotisk.databinding.ActivityDashboardBinding
import com.edu.cit.hkotisk.service.OrderDataService

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var orderDataService: OrderDataService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the order data service
        orderDataService = OrderDataService.getInstance()

        setupSearchBar()
        setupRecyclerViews()
    }

    private fun setupSearchBar() {
        binding.searchBarContainer.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun setupRecyclerViews() {
        // Set up Top Selling RecyclerView
        binding.topSellingRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val topSellingItems = orderDataService.getTopSellingItems()
        val topSellingAdapter = TopSellingAdapter(topSellingItems)
        binding.topSellingRecycler.adapter = topSellingAdapter

        // Set up Order Again RecyclerView
        binding.orderAgainRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val orderAgainItems = orderDataService.getOrderAgainItems("user123") // Using the current user ID
        val orderAgainAdapter = OrderAgainAdapter(orderAgainItems)
        binding.orderAgainRecycler.adapter = orderAgainAdapter
    }
}
