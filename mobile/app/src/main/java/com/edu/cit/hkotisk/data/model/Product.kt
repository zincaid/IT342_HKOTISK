package com.edu.cit.hkotisk.data.model

data class Product(
    val productId: Int,
    val description: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val category: String,
    val productImage: String,
    val available: Boolean
)

data class ProductResponse(
    val status: String,
    val message: String,
    val oblist: List<Product>,
    val auth_TOKEN: String?
)
