package com.example.dismov.models

data class LoginResponse(
    val status: String,
    val token: String,
    val data: LoginData
)

data class LoginData(
    val user: User
)

data class User(
    val _id: String,
    val name: String,
    val email: String,
    val role: String
)
