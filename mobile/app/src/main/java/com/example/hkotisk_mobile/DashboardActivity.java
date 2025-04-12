package com.example.hkotisk_mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hkotisk_mobile.adapter.OrderAgainAdapter;
import com.example.hkotisk_mobile.adapter.TopSellingAdapter;
import com.example.hkotisk_mobile.model.OrderAgainItem;
import com.example.hkotisk_mobile.model.TopSellingItem;
import com.example.hkotisk_mobile.service.OrderDataService;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView topSellingRecyclerView;
    private RecyclerView orderAgainRecyclerView;
    private TopSellingAdapter topSellingAdapter;
    private OrderAgainAdapter orderAgainAdapter;
    private OrderDataService orderDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize the order data service
        orderDataService = OrderDataService.getInstance();

        // Initialize RecyclerViews
        topSellingRecyclerView = findViewById(R.id.top_selling_recycler);
        orderAgainRecyclerView = findViewById(R.id.order_again_recycler);

        // Set up Top Selling RecyclerView
        topSellingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<TopSellingItem> topSellingItems = orderDataService.getTopSellingItems();
        topSellingAdapter = new TopSellingAdapter(topSellingItems);
        topSellingRecyclerView.setAdapter(topSellingAdapter);

        // Set up Order Again RecyclerView
        orderAgainRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<OrderAgainItem> orderAgainItems = orderDataService.getOrderAgainItems("user123"); // Using the current user ID
        orderAgainAdapter = new OrderAgainAdapter(orderAgainItems);
        orderAgainRecyclerView.setAdapter(orderAgainAdapter);
    }
} 