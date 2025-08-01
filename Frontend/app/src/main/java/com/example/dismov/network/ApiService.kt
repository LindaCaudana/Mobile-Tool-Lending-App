package com.example.dismov.network

import com.example.dismov.models.LoginRequest
import com.example.dismov.models.LoginResponse
import com.example.dismov.models.RegisterRequest
import com.example.dismov.models.RegisterResponse
import com.example.dismov.models.Tool
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
    @GET("/api/v1/tools")
    suspend fun getAvailableTools(
        @Header("Authorization") token: String
    ): List<Tool>

}

