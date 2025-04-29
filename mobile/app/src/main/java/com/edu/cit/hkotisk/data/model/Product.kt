package com.edu.cit.hkotisk.data.model

data class Product(
    val productId: Int,
    val description: String,
    val productName: String,
    val prices: List<Double>,
    val quantity: List<Int>,
    val sizes: List<String>,
    val category: String,
    val productImage: String
)

data class ProductResponse(
    val status: String,
    val message: String,
    val oblist: List<Product>,
    val auth_TOKEN: String?
)
