package com.ziro.fit.data.remote

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.model.LoginRequest
import com.ziro.fit.model.LoginResponse
import com.ziro.fit.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ZiroApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("api/auth/me")
    suspend fun getMe(): ApiResponse<User>
}
