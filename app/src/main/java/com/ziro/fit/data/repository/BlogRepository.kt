package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.AdminBlogListResponse
import com.ziro.fit.model.AdminBlogPost
import com.ziro.fit.model.BlogListResponse
import com.ziro.fit.model.BlogPost
import com.ziro.fit.model.CreateBlogPostRequest
import com.ziro.fit.model.UpdateBlogPostRequest
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject

class BlogRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getBlogPosts(page: Int = 1, pageSize: Int = 10): Result<BlogListResponse> {
        return try {
            val response = api.getBlogPosts(page, pageSize)
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load blog posts"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getBlogPost(slug: String): Result<BlogPost> {
        return try {
            val response = api.getBlogPost(slug)
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load blog post"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getAdminBlogPosts(): Result<AdminBlogListResponse> {
        return try {
            val response = api.getAdminBlogPosts()
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load admin blog posts"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun createBlogPost(request: CreateBlogPostRequest): Result<AdminBlogPost> {
        return try {
            val response = api.createBlogPost(request)
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to create blog post"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getAdminBlogPost(id: String): Result<AdminBlogPost> {
        return try {
            val response = api.getAdminBlogPost(id)
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load admin blog post"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun updateBlogPost(id: String, request: UpdateBlogPostRequest): Result<AdminBlogPost> {
        return try {
            val response = api.updateBlogPost(id, request)
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to update blog post"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deleteBlogPost(id: String): Result<Unit> {
        return try {
            val response = api.deleteBlogPost(id)
            if (response.success != false) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to delete blog post"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
