package com.example.dismov.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dismov.models.LoginRequest
import com.example.dismov.network.ApiClient
import com.example.dismov.utils.TokenManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import retrofit2.HttpException

class LoginViewModel : ViewModel() {

    private val authService = ApiClient.apiService

    private val _loginResult = mutableStateOf<String?>(null)
    val loginResult: State<String?> = _loginResult

    var loggedUserRole: String? = null
        private set

    var loggedUserToken: String? = null
        private set

    var loggedUserId: String? = null
        private set

    fun login(identifier: String, password: String) {
        val request = LoginRequest(identifier, password)
        viewModelScope.launch {
            try {
                val response = authService.login(request)
                val token = response.token
                TokenManager.saveToken(token)
                loggedUserToken = token
                loggedUserRole = response.data.user.role
                loggedUserId = response.data.user._id
                _loginResult.value = "Login exitoso"
            } catch (e: HttpException) {
                _loginResult.value = "Error HTTP: ${e.code()} ${e.message()}"
            } catch (e: Exception) {
                _loginResult.value = "Excepción: ${e.localizedMessage}"
            }
        }
    }
}
