package com.example.dismov.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dismov.models.Loan
import com.example.dismov.network.ApiClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoanViewModel : ViewModel() {

    private val loanService = ApiClient.loanService

    var loans by mutableStateOf<List<Loan>>(emptyList())
        private set

    var adminLoans by mutableStateOf<List<Loan>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)

    // Para admin: obtener todos los préstamos
    fun fetchAllLoans(token: String) {
        viewModelScope.launch {
            try {
                val response = loanService.getAllLoansAdmin("Bearer $token")
                adminLoans = response.data.loans
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al obtener préstamos: ${e.message}"
            }
        }
    }

    // Para usuario: obtener solo sus préstamos
    fun fetchUserLoans(token: String) {
        viewModelScope.launch {
            try {
                val response = loanService.getUserLoans("Bearer $token")
                loans = response.data.loans
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al obtener tus préstamos: ${e.message}"
            }
        }
    }


    fun returnLoan(token: String, loanId: String) {
        viewModelScope.launch {
            try {
                loanService.returnLoan("Bearer $token", loanId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al devolver herramienta: ${e.message}"
            }
        }
    }
}
