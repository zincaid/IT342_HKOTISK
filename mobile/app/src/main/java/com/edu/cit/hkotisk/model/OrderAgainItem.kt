package com.edu.cit.hkotisk.model

data class OrderAgainItem(
    val name: String,
    val imageResourceId: Int,
    val available: Int,
    val sold: Int,
    val price: Double
) 