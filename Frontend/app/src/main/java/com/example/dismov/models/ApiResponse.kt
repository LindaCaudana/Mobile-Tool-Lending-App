// models/ApiResponse.kt
package com.example.dismov.models

data class ApiResponse<T>(
    val status: String,
    val data: Map<String, T> // por ejemplo: "tool": Tool
)
