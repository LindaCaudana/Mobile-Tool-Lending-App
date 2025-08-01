package com.example.dismov.network

import com.example.dismov.models.LoginRequest
import com.example.dismov.models.LoginResponse
import com.example.dismov.models.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Any
}
