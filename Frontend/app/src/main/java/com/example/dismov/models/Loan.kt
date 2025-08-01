package com.example.dismov.models

data class LoanListResponse( // ← CAMBIADO
    val status: String,
    val data: LoanData
)

data class LoanData(
    val loans: List<Loan>
)

data class Loan(
    val _id: String,
    val user: LoanUser?,        // Puede venir como null si el backend lo omite
    val tool: LoanTool?,        // Puede venir como null o incompleto
    val startDate: String,
    val endDate: String?,
    val notes: String?,
    val status: String
)

data class LoanUser(
    val _id: String,
    val name: String,
    val email: String
)

data class LoanTool(
    val _id: String,
    val name: String,
    val description: String?,
    val image: String?
)
