package com.example.dismov.models

data class LoanRequest(
    val toolId: String,
    val endDate: String, // Formato: "2025-08-15"
    val notes: String? = null
)
