package com.ziro.fit.data.remote

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.model.CalendarResponse
import com.ziro.fit.model.GetExercisesResponse
import com.ziro.fit.model.GetClientsResponse
import com.ziro.fit.model.GetClientDetailsResponse
import com.ziro.fit.model.GetMeasurementsResponse
import com.ziro.fit.model.GetAssessmentsResponse
import com.ziro.fit.model.GetPhotosResponse
import com.ziro.fit.model.GetClientSessionsResponse
import com.ziro.fit.model.LogSetRequest
import com.ziro.fit.model.LoginRequest
import com.ziro.fit.model.LoginResponse
import com.ziro.fit.model.ServerLiveSessionResponse
import com.ziro.fit.model.StartWorkoutRequest
import com.ziro.fit.model.StartWorkoutResponse
import com.ziro.fit.model.GetActiveSessionResponse
import com.ziro.fit.model.User
import com.ziro.fit.model.CreateClientRequest
import com.ziro.fit.model.CreateClientResponse
import com.ziro.fit.model.UpdateClientRequest
import com.ziro.fit.model.CreateMeasurementRequest
import com.ziro.fit.model.CreateMeasurementResponse
import com.ziro.fit.model.CreateAssessmentRequest
import com.ziro.fit.model.CreateAssessmentResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ZiroApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("api/auth/me")
    suspend fun getMe(): ApiResponse<User>

    @GET("api/trainer/calendar")
    suspend fun getCalendarEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ApiResponse<CalendarResponse>

    @GET("api/workout-sessions/live")
    suspend fun getActiveSession(): ApiResponse<GetActiveSessionResponse>

    @POST("api/workout-sessions/start")
    suspend fun startWorkout(@Body request: StartWorkoutRequest): ApiResponse<StartWorkoutResponse>

    @POST("api/workout/log")
    suspend fun logSet(@Body request: LogSetRequest): ApiResponse<Any>

    @POST("api/workout-sessions/finish")
    suspend fun finishSession(@Body body: Map<String, String>): ApiResponse<Any>
    
    @GET("api/exercises")
    suspend fun getExercises(
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 50
    ): ApiResponse<GetExercisesResponse>


    @POST("api/clients")
    suspend fun createClient(@Body request: CreateClientRequest): ApiResponse<CreateClientResponse>

    @GET("api/clients")
    suspend fun getClients(): ApiResponse<GetClientsResponse>

    @GET("api/clients/{id}")
    suspend fun getClientDetails(@retrofit2.http.Path("id") id: String): ApiResponse<GetClientDetailsResponse>

    @retrofit2.http.PUT("api/clients/{id}")
    suspend fun updateClient(@retrofit2.http.Path("id") id: String, @Body request: UpdateClientRequest): ApiResponse<Any>

    @GET("api/clients/{id}/measurements")
    suspend fun getClientMeasurements(@retrofit2.http.Path("id") id: String): ApiResponse<GetMeasurementsResponse>

    @POST("api/clients/{id}/measurements")
    suspend fun createMeasurement(@retrofit2.http.Path("id") id: String, @Body request: CreateMeasurementRequest): ApiResponse<CreateMeasurementResponse>

    @retrofit2.http.DELETE("api/clients/{id}/measurements/{measurementId}")
    suspend fun deleteMeasurement(@retrofit2.http.Path("id") id: String, @retrofit2.http.Path("measurementId") measurementId: String): ApiResponse<Any>

    @GET("api/clients/{id}/assessments")
    suspend fun getClientAssessments(@retrofit2.http.Path("id") id: String): ApiResponse<GetAssessmentsResponse>

    @POST("api/clients/{id}/assessments")
    suspend fun createAssessment(@retrofit2.http.Path("id") id: String, @Body request: CreateAssessmentRequest): ApiResponse<CreateAssessmentResponse>

    @retrofit2.http.DELETE("api/clients/{id}/assessments/{resultId}")
    suspend fun deleteAssessment(@retrofit2.http.Path("id") id: String, @retrofit2.http.Path("resultId") resultId: String): ApiResponse<Any>

    @GET("api/clients/{id}/photos")
    suspend fun getClientPhotos(@retrofit2.http.Path("id") id: String): ApiResponse<GetPhotosResponse>

    @GET("api/clients/{id}/sessions")
    suspend fun getClientSessions(@retrofit2.http.Path("id") id: String): ApiResponse<GetClientSessionsResponse>

    @retrofit2.http.DELETE("api/clients/{id}")
    suspend fun deleteClient(@retrofit2.http.Path("id") id: String): ApiResponse<Any>
}