package com.edu.cit.hkotisk.data.api

import com.edu.cit.hkotisk.data.model.AuthRequest
import com.edu.cit.hkotisk.data.model.AuthResponse
import com.edu.cit.hkotisk.data.model.UserProfile
import com.edu.cit.hkotisk.data.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfile>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfile>

    @POST("auth/signin")
    suspend fun signIn(@Body request: AuthRequest.SignInRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body request: AuthRequest.SignUpRequest): Response<AuthResponse>
}
