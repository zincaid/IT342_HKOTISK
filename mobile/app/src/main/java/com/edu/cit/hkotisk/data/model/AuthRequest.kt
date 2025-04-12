package com.edu.cit.hkotisk.data.model

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val role: String = "student"
)
