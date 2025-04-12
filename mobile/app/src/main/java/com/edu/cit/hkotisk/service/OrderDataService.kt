package com.edu.cit.hkotisk.service

import com.edu.cit.hkotisk.R
import com.edu.cit.hkotisk.model.OrderAgainItem
import com.edu.cit.hkotisk.model.TopSellingItem

class OrderDataService private constructor() {
    // User ID for the current user (in a real app, this would come from authentication)
    private val currentUserId = "user123"
    
    // Map to store order history by user ID
    private val userOrderHistory = mutableMapOf<String, List<OrderHistory>>()
    
    // Map to store product statistics
    private val productStats = mutableMapOf<String, ProductStats>()
    
    // Available products in the system
    private val availableProducts = mutableListOf<Product>()
    
    init {
        // Initialize with sample data
        initializeSampleData()
    }
    
    companion object {
        @Volatile
        private var instance: OrderDataService? = null
        
        fun getInstance(): OrderDataService {
            return instance ?: synchronized(this) {
                instance ?: OrderDataService().also { instance = it }
            }
        }
    }
    
    private fun initializeSampleData() {
        // Add sample products
        availableProducts.add(Product("hotdog", "Hotdog with Cheese", R.drawable.hotdog, 20.00))
        //availableProducts.add(Product("pizza", "Pizza Margherita", R.drawable.pizza, 150.00))
        availableProducts.add(Product("dessert", "Ice Cream", R.drawable.dessert, 25.00))
        //availableProducts.add(Product("burger", "Cheeseburger", R.drawable.burger, 45.00))
        //availableProducts.add(Product("fries", "French Fries", R.drawable.fries, 30.00))
        
        // Initialize product stats
        availableProducts.forEach { product ->
            productStats[product.id] = ProductStats(product.id)
        }
        
        // Add sample order history for current user
        val userOrders = mutableListOf<OrderHistory>()
        userOrders.add(OrderHistory(currentUserId, "hotdog", 2, System.currentTimeMillis() - 86400000)) // 1 day ago
        userOrders.add(OrderHistory(currentUserId, "pizza", 1, System.currentTimeMillis() - 172800000)) // 2 days ago
        userOrders.add(OrderHistory(currentUserId, "dessert", 3, System.currentTimeMillis() - 259200000)) // 3 days ago
        userOrders.add(OrderHistory(currentUserId, "burger", 1, System.currentTimeMillis() - 345600000)) // 4 days ago
        
        userOrderHistory[currentUserId] = userOrders
        
        // Add sample order history for other users (to simulate top selling)
        val otherUser1 = "user456"
        val otherUser1Orders = mutableListOf<OrderHistory>()
        otherUser1Orders.add(OrderHistory(otherUser1, "pizza", 5, System.currentTimeMillis() - 86400000))
        otherUser1Orders.add(OrderHistory(otherUser1, "dessert", 2, System.currentTimeMillis() - 172800000))
        userOrderHistory[otherUser1] = otherUser1Orders
        
        val otherUser2 = "user789"
        val otherUser2Orders = mutableListOf<OrderHistory>()
        otherUser2Orders.add(OrderHistory(otherUser2, "pizza", 3, System.currentTimeMillis() - 86400000))
        otherUser2Orders.add(OrderHistory(otherUser2, "hotdog", 4, System.currentTimeMillis() - 172800000))
        otherUser2Orders.add(OrderHistory(otherUser2, "burger", 2, System.currentTimeMillis() - 259200000))
        userOrderHistory[otherUser2] = otherUser2Orders
        
        // Update product stats based on order history
        updateProductStats()
    }
    
    private fun updateProductStats() {
        // Reset stats
        productStats.values.forEach { it.reset() }
        
        // Count orders for each product
        userOrderHistory.values.forEach { orders ->
            orders.forEach { order ->
                productStats[order.productId]?.incrementOrderCount(order.quantity)
            }
        }
    }
    
    fun getOrderAgainItems(userId: String): List<OrderAgainItem> {
        val userOrders = userOrderHistory[userId] ?: return emptyList()
        if (userOrders.isEmpty()) {
            return emptyList()
        }
        
        // Get unique products ordered by the user, sorted by most recent
        val latestOrders = userOrders
            .groupBy { it.productId }
            .mapValues { (_, orders) -> orders.maxByOrNull { it.orderTime } }
            .filterValues { it != null }
            .mapValues { it.value!! }
        
        // Convert to OrderAgainItem objects
        return latestOrders.mapNotNull { (productId, order) ->
            // Find the product details
            val product = availableProducts.find { it.id == productId } ?: return@mapNotNull null
            
            // Get current availability from product stats
            val stats = productStats[productId]
            val available = 50 - (stats?.orderCount ?: 0) // Assuming max 50 items
            val sold = stats?.orderCount ?: 0
            
            OrderAgainItem(
                name = product.name,
                imageResourceId = product.imageResourceId,
                available = available,
                sold = sold,
                price = product.price
            )
        }
    }
    
    fun getTopSellingItems(): List<TopSellingItem> {
        // Sort products by order count
        val sortedStats = productStats.entries
            .sortedByDescending { it.value.orderCount }
        
        // Convert to TopSellingItem objects
        return sortedStats.mapNotNull { (productId, _) ->
            // Find the product details
            val product = availableProducts.find { it.id == productId } ?: return@mapNotNull null
            
            TopSellingItem(
                name = product.name,
                imageResourceId = product.imageResourceId
            )
        }
    }
    
    // Inner data classes for data modeling
    data class Product(
        val id: String,
        val name: String,
        val imageResourceId: Int,
        val price: Double
    )
    
    data class OrderHistory(
        val userId: String,
        val productId: String,
        val quantity: Int,
        val orderTime: Long
    )
    
    class ProductStats(val productId: String) {
        var orderCount: Int = 0
        
        fun incrementOrderCount(quantity: Int) {
            orderCount += quantity
        }
        
        fun reset() {
            orderCount = 0
        }
    }
} 