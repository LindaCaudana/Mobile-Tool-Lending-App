package com.example.dismov.network

import com.example.dismov.models.LoanRequest
import com.example.dismov.models.LoanResponse
import com.example.dismov.models.Tool
import com.example.dismov.models.ToolResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ToolService {

    @GET("api/v1/tools")
    suspend fun getAllTools(
        @Header("Authorization") token: String
    ): ToolResponse

    @Multipart
    @POST("api/v1/tools")
    suspend fun createTool(
        @Header("Authorization") token: String,
        @Part("name") name: String,
        @Part("description") description: String,
        @Part("availableQuantity") availableQuantity: Int,
        @Part image: MultipartBody.Part? = null
    ): ResponseBody

    @DELETE("api/v1/tools/{id}")
    suspend fun deleteTool(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )

    @Multipart
    @PATCH("api/v1/tools/{id}")
    suspend fun updateTool(
        @Header("Authorization") token: String,
        @Path("id") toolId: String,
        @Part("name") name: String,
        @Part("description") description: String,
        @Part("availableQuantity") availableQuantity: Int,
        @Part image: MultipartBody.Part? = null
    ): Tool

    @POST("api/v1/tools/{id}/request-loan")
    suspend fun requestLoan(
        @Path ("id") toolId: String,
        @Header("Authorization") token: String
    ): Response<LoanResponse>

    @POST("api/v1/loans")
    suspend fun createLoan(
        @Body loanRequest: LoanRequest,
        @Header("Authorization") token: String
    ): Response<LoanResponse>


}
