package com.ziro.fit.model

// Public Blog Models
data class BlogPost(
    val id: String,
    val slug: String,
    val title: String,
    val excerpt: String?,
    val coverImage: String?,
    val publishedAt: String?,
    val author: BlogAuthor?,
    val content: String? = null  // only in detail
)

data class BlogAuthor(
    val id: String?,
    val name: String?
)

data class BlogListResponse(
    val posts: List<BlogPost>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

// Admin Blog Models
data class AdminBlogPost(
    val id: String,
    val title: String,
    val slug: String,
    val published: Boolean,
    val authorId: String?,
    val authorName: String?,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String?,
    val excerpt: String?,
    val coverImage: String?,
    val content: String?
)

data class AdminBlogListResponse(
    val posts: List<AdminBlogPost>,
    val total: Int
)

// Request models
data class CreateBlogPostRequest(
    val title: String,
    val slug: String,
    val content: String,
    val excerpt: String? = null,
    val coverImage: String? = null,
    val published: Boolean = false
)

data class UpdateBlogPostRequest(
    val title: String? = null,
    val slug: String? = null,
    val content: String? = null,
    val excerpt: String? = null,
    val coverImage: String? = null,
    val published: Boolean? = null
)

// Admin Events Models
data class EventTrainerSummary(
    val id: String,
    val name: String?,
    val profilePhotoPath: String?
)

data class PendingEvent(
    val id: String,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String?,
    val price: Double?,
    val maxParticipants: Int?,
    val location: String?,
    val category: String?,
    val status: String,
    val trainer: EventTrainerSummary?
)

data class AdminEventsResponse(val events: List<PendingEvent>)

data class EventModerationDetailResponse(val event: PendingEvent)

data class EventModerationActionRequest(
    val action: String,
    val rejectionReason: String? = null
)

data class EventModerationUpdateResponse(val event: PendingEvent)
