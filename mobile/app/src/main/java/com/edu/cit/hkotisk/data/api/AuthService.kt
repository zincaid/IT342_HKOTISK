package com.edu.cit.hkotisk.data.api

import com.edu.cit.hkotisk.data.model.AuthResponse
import com.edu.cit.hkotisk.data.model.SignInRequest
import com.edu.cit.hkotisk.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signin")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>
}
