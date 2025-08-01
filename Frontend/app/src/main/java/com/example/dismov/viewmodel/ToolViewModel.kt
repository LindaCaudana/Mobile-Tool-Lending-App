package com.example.dismov.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dismov.models.Tool
import com.example.dismov.network.ApiClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.dismov.models.LoanRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ToolViewModel : ViewModel() {

    private val toolService = ApiClient.toolService

    var tools by mutableStateOf<List<Tool>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun fetchTools(token: String) {
        viewModelScope.launch {
            try {
                val response = toolService.getAllTools("Bearer $token")
                tools = response.data.tools
                println("✔ Herramientas recibidas: ${tools.size}") // <-- Añade esto
                tools.forEach {
                    println(" - ${it.name} (Stock: ${it.availableQuantity})")
                }
            } catch (e: Exception) {
                errorMessage = "Error al obtener herramientas: ${e.message}"
            }
        }
    }

    fun addTool(name: String, description: String, quantity: Int, imageUrl: String?, token: String) {
        viewModelScope.launch {
            try {
                val response = toolService.createTool(
                    token = "Bearer $token",
                    name = name,
                    description = description,
                    availableQuantity = quantity,
                    image = null // Cambiar si usas imagen real
                )
                fetchTools(token)
            } catch (e: Exception) {
                errorMessage = "Error al crear herramienta: ${e.message}"
            }
        }
    }

    fun deleteTool(toolId: String, token: String) {
        viewModelScope.launch {
            try {
                toolService.deleteTool("Bearer $token", toolId)
                tools = tools.filterNot { it._id == toolId }
            } catch (e: Exception) {
                errorMessage = "Error al eliminar herramienta: ${e.message}"
            }
        }
    }

    fun updateTool(
        toolId: String,
        name: String,
        description: String,
        quantity: Int,
        imageFile: File?,
        token: String
    ) {
        viewModelScope.launch {
            try {
                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                toolService.updateTool(
                    token = "Bearer $token",
                    toolId = toolId,
                    name = name,
                    description = description,
                    availableQuantity = quantity,
                    image = imagePart
                )

                fetchTools(token)
            } catch (e: Exception) {
                errorMessage = "Error al actualizar herramienta: ${e.message}"
            }
        }
    }

    fun requestLoan(toolId: String, token: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val request = LoanRequest(
                    toolId = toolId,
                    endDate = "2025-08-15",
                    notes = "Solicitado desde la app"
                )

                val response = toolService.createLoan(request, "Bearer $token")
                if (response.isSuccessful && response.body()?.status == "success") {
                    println("✔ Préstamo exitoso")
                    onSuccess()
                } else {
                    val error = response.errorBody()?.string() ?: "Error desconocido"
                    println("⚠️ Error al solicitar préstamo: $error")
                    onError(error)
                }
            } catch (e: Exception) {
                println("❌ Excepción al solicitar préstamo: ${e.message}")
                onError(e.message ?: "Error desconocido")
            }
        }
    }



}
