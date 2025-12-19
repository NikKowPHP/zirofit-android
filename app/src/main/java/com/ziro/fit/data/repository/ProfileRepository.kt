package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getCoreInfo(): Result<ProfileCoreInfo> {
        return try {
            val response = api.getCoreInfo()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!.coreInfo)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch core info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBranding(): Result<ProfileBranding> {
        return try {
            val response = api.getBranding()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!.branding)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch branding"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServices(): Result<ProfileServicesResponse> {
        return try {
            val response = api.getServices()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch services"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPackages(): Result<ProfilePackagesResponse> {
        return try {
            val response = api.getPackages()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch packages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailability(): Result<ProfileAvailability> {
        return try {
            val response = api.getAvailability()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!.availability)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch availability"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransformationPhotos(): Result<ProfileTransformationPhotosResponse> {
        return try {
            val response = api.getTransformationPhotos()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch transformation photos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTestimonials(): Result<ProfileTestimonialsResponse> {
        return try {
            val response = api.getTestimonials()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch testimonials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSocialLinks(): Result<ProfileSocialLinksResponse> {
        return try {
            val response = api.getSocialLinks()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch social links"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExternalLinks(): Result<ProfileExternalLinksResponse> {
        return try {
            val response = api.getExternalLinks()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch external links"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBilling(): Result<ProfileBilling> {
        return try {
            val response = api.getBilling()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!.billing)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch billing info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBenefits(): Result<ProfileBenefitsResponse> {
        return try {
            val response = api.getBenefits()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch benefits"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotifications(): Result<GetNotificationsResponse> {
        return try {
            val response = api.getNotifications()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch notifications"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerPushToken(token: String): Result<Unit> {
        return try {
            val response = api.registerPushToken(RegisterPushTokenRequest(token))
            if (response.success != false) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to register push token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
