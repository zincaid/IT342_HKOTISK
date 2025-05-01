package com.edu.cit.hkotisk.data.model

data class UserProfile(
    val email: String,
    val username: String
)

data class UpdateProfileRequest(
    val email: String,
    val username: String,
    val password: String
)
