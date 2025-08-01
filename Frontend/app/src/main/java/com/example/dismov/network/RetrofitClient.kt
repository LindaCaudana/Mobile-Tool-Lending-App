// network/RetrofitClient.kt
package com.example.dismov.network

import com.example.dismov.network.RetrofitClient.retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.100.7:3000/" // Cambia si usas IP diferente

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

    // Servicio de autenticación y usuarios
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Servicio de herramientas (para AvailableToolsScreen)
    val toolService: ToolService by lazy {
        retrofit.create(ToolService::class.java)
    }

    val loanService: LoanService by lazy {
        retrofit.create(LoanService::class.java)
    }

