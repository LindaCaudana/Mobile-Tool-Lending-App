package com.example.dismov.models

data class LoanResponseWrapper(
    val status: String,
    val data: LoanDataWrapper
)

data class LoanDataWrapper(
    val loans: List<Loan>
)
