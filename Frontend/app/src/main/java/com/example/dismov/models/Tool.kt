package com.example.dismov.models

data class Tool(
    val _id: String,
    val name: String,
    val description: String,
    val availableQuantity: Int,
    val image: String?
) {
    val imageUrl: String?
        get() = image?.let { "http://10.0.2.2:3000/$it" } // ajusta URL base si es necesario
}
