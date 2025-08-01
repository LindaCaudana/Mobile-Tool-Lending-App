package com.example.dismov.models

data class ToolResponse(
    val status: String,
    val results: Int,
    val data: ToolData
)

data class ToolData(
    val tools: List<Tool>
)
