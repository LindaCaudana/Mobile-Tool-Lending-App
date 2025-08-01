// models/ToolUpdateRequest.kt
package com.example.dismov.models

data class ToolUpdateRequest(
    val name: String,
    val description: String,
    val availableQuantity: Int,
    val image: String? = null
)
