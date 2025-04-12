package com.example.hkotisk_mobile.service;

import com.example.hkotisk_mobile.model.OrderAgainItem;
import com.example.hkotisk_mobile.model.TopSellingItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderDataService {
    // Singleton instance
    private static OrderDataService instance;
    
    // User ID for the current user (in a real app, this would come from authentication)
    private String currentUserId = "user123";
    
    // Map to store order history by user ID
    private Map<String, List<OrderHistory>> userOrderHistory = new HashMap<>();
    
    // Map to store product statistics
    private Map<String, ProductStats> productStats = new HashMap<>();
    
    // Available products in the system
    private List<Product> availableProducts = new ArrayList<>();
    
    private OrderDataService() {
        // Initialize with sample data
        initializeSampleData();
    }
    
    public static OrderDataService getInstance() {
        if (instance == null) {
            instance = new OrderDataService();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        // Add sample products
        availableProducts.add(new Product("hotdog", "Hotdog with Cheese", R.drawable.hotdog, 20.00));
        availableProducts.add(new Product("pizza", "Pizza Margherita", R.drawable.pizza, 150.00));
        availableProducts.add(new Product("dessert", "Ice Cream", R.drawable.dessert, 25.00));
        availableProducts.add(new Product("burger", "Cheeseburger", R.drawable.burger, 45.00));
        availableProducts.add(new Product("fries", "French Fries", R.drawable.fries, 30.00));
        
        // Initialize product stats
        for (Product product : availableProducts) {
            productStats.put(product.getId(), new ProductStats(product.getId()));
        }
        
        // Add sample order history for current user
        List<OrderHistory> userOrders = new ArrayList<>();
        userOrders.add(new OrderHistory(currentUserId, "hotdog", 2, System.currentTimeMillis() - 86400000)); // 1 day ago
        userOrders.add(new OrderHistory(currentUserId, "pizza", 1, System.currentTimeMillis() - 172800000)); // 2 days ago
        userOrders.add(new OrderHistory(currentUserId, "dessert", 3, System.currentTimeMillis() - 259200000)); // 3 days ago
        userOrders.add(new OrderHistory(currentUserId, "burger", 1, System.currentTimeMillis() - 345600000)); // 4 days ago
        
        userOrderHistory.put(currentUserId, userOrders);
        
        // Add sample order history for other users (to simulate top selling)
        String otherUser1 = "user456";
        List<OrderHistory> otherUser1Orders = new ArrayList<>();
        otherUser1Orders.add(new OrderHistory(otherUser1, "pizza", 5, System.currentTimeMillis() - 86400000));
        otherUser1Orders.add(new OrderHistory(otherUser1, "dessert", 2, System.currentTimeMillis() - 172800000));
        userOrderHistory.put(otherUser1, otherUser1Orders);
        
        String otherUser2 = "user789";
        List<OrderHistory> otherUser2Orders = new ArrayList<>();
        otherUser2Orders.add(new OrderHistory(otherUser2, "pizza", 3, System.currentTimeMillis() - 86400000));
        otherUser2Orders.add(new OrderHistory(otherUser2, "hotdog", 4, System.currentTimeMillis() - 172800000));
        otherUser2Orders.add(new OrderHistory(otherUser2, "burger", 2, System.currentTimeMillis() - 259200000));
        userOrderHistory.put(otherUser2, otherUser2Orders);
        
        // Update product stats based on order history
        updateProductStats();
    }
    
    private void updateProductStats() {
        // Reset stats
        for (ProductStats stats : productStats.values()) {
            stats.reset();
        }
        
        // Count orders for each product
        for (List<OrderHistory> orders : userOrderHistory.values()) {
            for (OrderHistory order : orders) {
                ProductStats stats = productStats.get(order.getProductId());
                if (stats != null) {
                    stats.incrementOrderCount(order.getQuantity());
                }
            }
        }
    }
    
    public List<OrderAgainItem> getOrderAgainItems(String userId) {
        List<OrderHistory> userOrders = userOrderHistory.get(userId);
        if (userOrders == null || userOrders.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get unique products ordered by the user, sorted by most recent
        Map<String, OrderHistory> latestOrders = userOrders.stream()
                .collect(Collectors.groupingBy(
                        OrderHistory::getProductId,
                        Collectors.reducing((o1, o2) -> o1.getOrderTime() > o2.getOrderTime() ? o1 : o2)
                ))
                .values()
                .stream()
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toMap(
                        OrderHistory::getProductId,
                        order -> order
                ));
        
        // Convert to OrderAgainItem objects
        List<OrderAgainItem> orderAgainItems = new ArrayList<>();
        for (Map.Entry<String, OrderHistory> entry : latestOrders.entrySet()) {
            String productId = entry.getKey();
            OrderHistory order = entry.getValue();
            
            // Find the product details
            Product product = availableProducts.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);
            
            if (product != null) {
                // Get current availability from product stats
                ProductStats stats = productStats.get(productId);
                int available = 50 - stats.getOrderCount(); // Assuming max 50 items
                int sold = stats.getOrderCount();
                
                orderAgainItems.add(new OrderAgainItem(
                        product.getName(),
                        product.getImageResourceId(),
                        available,
                        sold,
                        product.getPrice()
                ));
            }
        }
        
        return orderAgainItems;
    }
    
    public List<TopSellingItem> getTopSellingItems() {
        // Sort products by order count
        List<Map.Entry<String, ProductStats>> sortedStats = productStats.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getOrderCount(), e1.getValue().getOrderCount()))
                .collect(Collectors.toList());
        
        // Convert to TopSellingItem objects
        List<TopSellingItem> topSellingItems = new ArrayList<>();
        for (Map.Entry<String, ProductStats> entry : sortedStats) {
            String productId = entry.getKey();
            
            // Find the product details
            Product product = availableProducts.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);
            
            if (product != null) {
                topSellingItems.add(new TopSellingItem(
                        product.getName(),
                        product.getImageResourceId()
                ));
            }
        }
        
        return topSellingItems;
    }
    
    // Inner classes for data modeling
    public static class Product {
        private String id;
        private String name;
        private int imageResourceId;
        private double price;
        
        public Product(String id, String name, int imageResourceId, double price) {
            this.id = id;
            this.name = name;
            this.imageResourceId = imageResourceId;
            this.price = price;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public int getImageResourceId() {
            return imageResourceId;
        }
        
        public double getPrice() {
            return price;
        }
    }
    
    public static class OrderHistory {
        private String userId;
        private String productId;
        private int quantity;
        private long orderTime;
        
        public OrderHistory(String userId, String productId, int quantity, long orderTime) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
            this.orderTime = orderTime;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public String getProductId() {
            return productId;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public long getOrderTime() {
            return orderTime;
        }
    }
    
    public static class ProductStats {
        private String productId;
        private int orderCount;
        
        public ProductStats(String productId) {
            this.productId = productId;
            this.orderCount = 0;
        }
        
        public void incrementOrderCount(int quantity) {
            this.orderCount += quantity;
        }
        
        public void reset() {
            this.orderCount = 0;
        }
        
        public int getOrderCount() {
            return orderCount;
        }
    }
} 