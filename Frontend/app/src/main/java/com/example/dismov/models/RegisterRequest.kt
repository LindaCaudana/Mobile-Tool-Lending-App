package com.example.dismov.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)
