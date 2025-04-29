package com.edu.cit.hkotisk.data.api

import com.edu.cit.hkotisk.data.model.AuthRequest
import com.edu.cit.hkotisk.data.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signin")
    suspend fun signIn(@Body request: AuthRequest.SignInRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body request: AuthRequest.SignUpRequest): Response<AuthResponse>
}
