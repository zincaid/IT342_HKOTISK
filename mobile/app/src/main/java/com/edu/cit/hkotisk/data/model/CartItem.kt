package com.edu.cit.hkotisk.data.model

data class CartItem(
    val cartId: Int,
    val orderId: Int,
    val email: String,
    val dateAdded: String,
    val quantity: Int,
    val price: Double,
    val productId: Int,
    val productName: String,
    val productCategory: String,
    val productSize: String,
    val ordered: Boolean
)

data class GetCartResponse(
    val status: String,
    val message: String,
    val oblist: List<CartItem>,
    val auth_TOKEN: String?
)
