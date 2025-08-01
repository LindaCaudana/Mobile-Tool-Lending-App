package com.example.dismov.models

data class RegisterResponse(
    val status: String,
    val token: String?,
    val data: RegisteredUser?
)

data class RegisteredUser(
    val _id: String,
    val name: String,
    val email: String,
    val role: String
)
