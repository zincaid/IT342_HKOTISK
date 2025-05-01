package com.edu.cit.hkotisk.data.model

data class CartRequest(
    val productId: Int,
    val quantity: Int,
    val price: Double
)

data class CartResponse(
    val status: String,
    val message: String
)
