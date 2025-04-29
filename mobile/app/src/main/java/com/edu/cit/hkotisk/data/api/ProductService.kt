package com.edu.cit.hkotisk.data.api

import com.edu.cit.hkotisk.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Headers

interface ProductService {
    @GET("user/product")
    fun getProducts(): Call<ProductResponse>

    @POST("user/cart")
    fun addToCart(@Body cartRequest: CartRequest): Call<CartResponse>

    @GET("user/cart")
    fun getCart(): Call<GetCartResponse>

    @POST("user/order")
    fun createOrder(): Call<OrderResponse>
}
