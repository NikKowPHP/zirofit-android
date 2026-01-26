package com.ziro.fit.di

import android.util.Log
import com.ziro.fit.data.local.TokenManager
import com.ziro.fit.data.remote.ZiroApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    private const val BASE_URL = "http://localhost:3000/"
    private const val BASE_URL = "https://ziro.fit/"
    private const val TAG = "ZiroAPI"

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = tokenManager.getToken()
            
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            var response = chain.proceed(requestBuilder.build())

            if (response.code == 401) {
                // If we get a 401, it means the token is invalid or expired
                // We should clear the token and trigger a global logout
                kotlinx.coroutines.runBlocking {
                    tokenManager.triggerLogout()
                }
            }
            
            response
        }
    }

    @Provides
    @Singleton
    fun provideZiroApi(authInterceptor: Interceptor): ZiroApi {
        // Custom logger for better formatting
        val logger = HttpLoggingInterceptor.Logger { message ->
            Log.d(TAG, message)
        }
        
        val loggingInterceptor = HttpLoggingInterceptor(logger).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZiroApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): io.github.jan.supabase.SupabaseClient {
        // TODO: Move these to BuildConfig/Secrets
        return io.github.jan.supabase.createSupabaseClient(
            supabaseUrl = "http://127.0.0.1:54321",
            supabaseKey = "sb_publishable_ACJWlzQHlZjBrEguHvfOxg_3BJgxAaH"
        ) {
            install(io.github.jan.supabase.realtime.Realtime)
        }
    }
}
