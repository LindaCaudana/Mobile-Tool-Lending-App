package com.example.dismov.models

data class LoanResponse(
    val status: String,
    val message: String?,
    val data: LoanData?
)

data class SingleLoanData(
    val loan: Loan
)


data class LoanDetail(
    val _id: String,
    val tool: String,
    val user: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val notes: String?
)
