package com.ziro.fit.data.remote

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.model.CalendarResponse
import com.ziro.fit.model.GetExercisesResponse
import com.ziro.fit.model.GetClientsResponse
import com.ziro.fit.model.CalendarSummaryResponse
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
import com.ziro.fit.model.UploadPhotoResponse
import com.ziro.fit.model.UpdateSessionRequest
import com.ziro.fit.model.ClientSession
import com.ziro.fit.model.CreateSessionRequest
import com.ziro.fit.model.CreateSessionResponse
import com.ziro.fit.model.CreateExerciseRequest
import com.ziro.fit.model.CreateExerciseResponse
import com.ziro.fit.model.*
import com.ziro.fit.data.model.CheckInContext
import com.ziro.fit.data.model.CheckInDetailWrapper
import com.ziro.fit.data.model.CheckInPendingItem
import com.ziro.fit.data.model.ReviewCheckInRequest
import com.ziro.fit.data.model.CheckInConfig
import com.ziro.fit.data.model.CheckInSubmissionRequest
import com.ziro.fit.data.model.CheckInHistoryItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.*

interface ZiroApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("api/auth/me")
    suspend fun getMe(): ApiResponse<User>

    @POST("api/profile/me/push-token")
    suspend fun registerPushToken(@Body request: RegisterPushTokenRequest): ApiResponse<Any>

    @GET("api/trainer/calendar")
    suspend fun getCalendarEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ApiResponse<CalendarResponse>

    @GET("api/trainer/calendar/clients-summary")
    suspend fun getCalendarClientsSummary(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ApiResponse<CalendarSummaryResponse>

    @POST("api/trainer/calendar")
    suspend fun createCalendarSession(@Body request: CreateSessionRequest): ApiResponse<CreateSessionResponse>

    @GET("api/workout-sessions/live")
    suspend fun getActiveSession(): ApiResponse<GetActiveSessionResponse>

    @POST("api/workout-sessions/start")
    suspend fun startWorkout(@Body request: StartWorkoutRequest): ApiResponse<StartWorkoutResponse>

    @POST("api/workout-templates")
    suspend fun createWorkoutTemplate(@Body request: CreateWorkoutTemplateRequest): ApiResponse<CreateWorkoutTemplateResponse>

    @POST("api/workout/log")
    suspend fun logSet(@Body request: LogSetRequest): ApiResponse<Any>

    @POST("api/workout-sessions/finish")
    suspend fun finishWorkout(@Body request: FinishWorkoutRequest): ApiResponse<FinishWorkoutResponse>

    @POST("api/workout-sessions/{id}/cancel")
    suspend fun cancelActiveWorkout(@retrofit2.http.Path("id") id: String): ApiResponse<Any>

    @retrofit2.http.PUT("api/workout-sessions/{id}")
    suspend fun updateWorkoutSession(
        @retrofit2.http.Path("id") id: String,
        @Body request: UpdateSessionRequest
    ): ApiResponse<Any>
    
    @GET("api/exercises")
    suspend fun getExercises(
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("page") page: Int = 1
    ): ApiResponse<GetExercisesResponse>

    @POST("api/exercises")
    suspend fun createExercise(@Body request: CreateExerciseRequest): ApiResponse<CreateExerciseResponse>

    @retrofit2.http.PUT("api/exercises/{id}")
    suspend fun updateExercise(
        @retrofit2.http.Path("id") id: String,
        @Body request: CreateExerciseRequest
    ): ApiResponse<Any>

    @retrofit2.http.DELETE("api/exercises/{id}")
    suspend fun deleteExercise(@retrofit2.http.Path("id") id: String): ApiResponse<Any>


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
    suspend fun getClientAssessments(@retrofit2.http.Path("id") id: String): ApiResponse<GetClientAssessmentsResponse>

    @POST("api/clients/{id}/assessments")
    suspend fun createAssessment(@retrofit2.http.Path("id") id: String, @Body request: CreateClientAssessmentRequest): ApiResponse<CreateClientAssessmentResponse>

    @retrofit2.http.DELETE("api/clients/{id}/assessments/{resultId}")
    suspend fun deleteAssessment(@retrofit2.http.Path("id") id: String, @retrofit2.http.Path("resultId") resultId: String): ApiResponse<Any>

    @GET("api/clients/{id}/photos")
    suspend fun getClientPhotos(@retrofit2.http.Path("id") id: String): ApiResponse<GetPhotosResponse>

    @retrofit2.http.Multipart
    @POST("api/clients/{id}/photos")
    suspend fun uploadPhoto(
        @retrofit2.http.Path("id") id: String,
        @retrofit2.http.Part photo: okhttp3.MultipartBody.Part,
        @retrofit2.http.Part("photoDate") photoDate: okhttp3.RequestBody,
        @retrofit2.http.Part("caption") caption: okhttp3.RequestBody?
    ): ApiResponse<UploadPhotoResponse>

    @retrofit2.http.DELETE("api/clients/{id}/photos/{photoId}")
    suspend fun deletePhoto(@retrofit2.http.Path("id") id: String, @retrofit2.http.Path("photoId") photoId: String): ApiResponse<Any>

    @GET("api/clients/{id}/sessions")
    suspend fun getClientSessions(@retrofit2.http.Path("id") id: String): ApiResponse<GetClientSessionsResponse>

    @retrofit2.http.PUT("api/clients/{id}/sessions/{sessionId}")
    suspend fun updateSession(
        @retrofit2.http.Path("id") id: String, 
        @retrofit2.http.Path("sessionId") sessionId: String,
        @Body request: UpdateSessionRequest
    ): ApiResponse<ClientSession>

    @retrofit2.http.DELETE("api/clients/{id}/sessions/{sessionId}")
    suspend fun deleteSession(@retrofit2.http.Path("id") id: String, @retrofit2.http.Path("sessionId") sessionId: String): ApiResponse<Any>

    @retrofit2.http.DELETE("api/clients/{id}")
    suspend fun deleteClient(@retrofit2.http.Path("id") id: String): ApiResponse<Any>

    // Profile Endpoints
    @GET("api/profile/me/core-info")
    suspend fun getCoreInfo(): ApiResponse<ProfileCoreInfoResponse>

    @GET("api/profile/me/branding")
    suspend fun getBranding(): ApiResponse<ProfileBrandingResponse>

    @GET("api/profile/me/services")
    suspend fun getServices(): ApiResponse<ProfileServicesResponse>

    @GET("api/profile/me/packages")
    suspend fun getPackages(): ApiResponse<ProfilePackagesResponse>

    @GET("api/profile/me/availability")
    suspend fun getAvailability(): ApiResponse<ProfileAvailabilityResponse>

    @GET("api/profile/me/transformation-photos")
    suspend fun getTransformationPhotos(): ApiResponse<ProfileTransformationPhotosResponse>

    @GET("api/profile/me/testimonials")
    suspend fun getTestimonials(): ApiResponse<ProfileTestimonialsResponse>

    @GET("api/profile/me/social-links")
    suspend fun getSocialLinks(): ApiResponse<ProfileSocialLinksResponse>

    @GET("api/profile/me/external-links")
    suspend fun getExternalLinks(): ApiResponse<ProfileExternalLinksResponse>

    @GET("api/profile/me/billing")
    suspend fun getBilling(): ApiResponse<ProfileBillingResponse>

    @GET("api/profile/me/benefits")
    suspend fun getBenefits(): ApiResponse<ProfileBenefitsResponse>

    @GET("api/notifications")
    suspend fun getNotifications(): ApiResponse<GetNotificationsResponse>

    // Generic Assessments Management
    @GET("api/profile/me/assessments")
    suspend fun getAssessments(): ApiResponse<GetAssessmentsResponse>

    @POST("api/profile/me/assessments")
    suspend fun createAssessment(@Body request: com.ziro.fit.model.CreateAssessmentRequest): ApiResponse<CreateAssessmentResponse>

    @retrofit2.http.PUT("api/profile/me/assessments/{id}")
    suspend fun updateAssessment(@retrofit2.http.Path("id") id: String, @Body request: com.ziro.fit.model.UpdateAssessmentRequest): ApiResponse<Assessment>

    @retrofit2.http.DELETE("api/profile/me/assessments/{id}")
    suspend fun deleteAssessment(@retrofit2.http.Path("id") id: String): ApiResponse<Any>

    // Bookings
    @GET("api/bookings")
    suspend fun getBookings(): ApiResponse<List<Booking>>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): ApiResponse<CreateBookingResponse>

    @retrofit2.http.PUT("api/bookings/{id}")
    suspend fun updateBooking(
        @retrofit2.http.Path("id") id: String,
        @Body request: UpdateBookingRequest
    ): ApiResponse<BookingResponse>

    @retrofit2.http.DELETE("api/bookings/{id}")
    suspend fun deleteBooking(@retrofit2.http.Path("id") id: String): ApiResponse<Any>

    // Check-Ins
    @GET("api/trainer/check-ins/pending")
    suspend fun getPendingCheckIns(): List<CheckInPendingItem>

    @GET("api/trainer/check-ins/{id}")
    suspend fun getCheckInDetails(@retrofit2.http.Path("id") id: String): ApiResponse<CheckInContext>

    @retrofit2.http.PATCH("api/trainer/check-ins/{id}/review")
    suspend fun reviewCheckIn(
        @retrofit2.http.Path("id") id: String,
        @Body request: ReviewCheckInRequest
    ): ApiResponse<Any>
    @GET("api/client/dashboard")
    suspend fun getClientDashboard(): ApiResponse<ClientDashboardResponse>

    // Trainer Discovery
    @GET("api/trainers")
    suspend fun getTrainers(
        @Query("search") search: String? = null
    ): ApiResponse<GetTrainersResponse>

    @GET("api/trainers/{id}/public")
    suspend fun getPublicTrainerProfile(@retrofit2.http.Path("id") id: String): ApiResponse<PublicTrainerProfileResponse>

    @GET("api/trainers/{username}/schedule")
    suspend fun getTrainerSchedule(@retrofit2.http.Path("username") username: String): ApiResponse<TrainerScheduleResponse>

    @POST("api/client/trainer/link")
    suspend fun linkTrainer(@Body request: LinkTrainerRequest): ApiResponse<LinkActionResponse>

    @retrofit2.http.DELETE("api/client/trainer/link")
    suspend fun unlinkTrainer(): ApiResponse<LinkActionResponse>

    @GET("api/client/trainer")
    suspend fun getLinkedTrainer(): ApiResponse<LinkedTrainerResponse>

    @GET("api/workout-sessions/history")
    suspend fun getWorkoutHistory(
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null,
        @Query("clientId") clientId: String? = null
    ): ApiResponse<WorkoutHistoryResponse>

    @GET("api/client/progress")
    suspend fun getClientProgress(): ApiResponse<ClientProgressResponse>

    // Client Check-ins
    @GET("api/client/check-in/config")
    suspend fun getCheckInConfig(): ApiResponse<CheckInConfig>

    @POST("api/client/check-in")
    suspend fun submitCheckIn(@Body request: CheckInSubmissionRequest): ApiResponse<Any>

    @GET("api/client/check-ins")
    suspend fun getClientCheckInHistory(): ApiResponse<List<CheckInHistoryItem>>

    @GET("api/client/check-ins/{id}")
    suspend fun getClientCheckInDetails(@retrofit2.http.Path("id") id: String): ApiResponse<CheckInDetailWrapper>

    @GET("api/client/programs")
    suspend fun getClientPrograms(): ApiResponse<GetClientProgramsResponse>

    // Chat
    @GET("api/chat")
    suspend fun getChatHistory(
        @Query("clientId") clientId: String,
        @Query("trainerId") trainerId: String
    ): ApiResponse<StartChatResponse>

    @POST("api/chat")
    suspend fun sendMessage(@Body request: SendMessageRequest): ApiResponse<Any>

    @POST("api/client/ai/generate")
    suspend fun generateAiWorkout(@Body request: AiGenerationRequest): ApiResponse<WorkoutGenerationResponse>

    @POST("api/checkout/session")
    suspend fun createCheckoutSession(@Body request: CreateCheckoutSessionRequest): ApiResponse<CreateCheckoutSessionResponse>
}