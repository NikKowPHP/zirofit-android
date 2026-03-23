package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.BlogRepository
import com.ziro.fit.model.AdminBlogListResponse
import com.ziro.fit.model.AdminBlogPost
import com.ziro.fit.model.CreateBlogPostRequest
import com.ziro.fit.model.UpdateBlogPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminBlogState {
    object Idle : AdminBlogState()
    object Loading : AdminBlogState()
    data class PostsLoaded(val response: AdminBlogListResponse) : AdminBlogState()
    data class PostDetailLoaded(val post: AdminBlogPost) : AdminBlogState()
    data class Error(val message: String) : AdminBlogState()
}

@HiltViewModel
class AdminBlogViewModel @Inject constructor(
    private val blogRepository: BlogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminBlogState>(AdminBlogState.Idle)
    val uiState: StateFlow<AdminBlogState> = _uiState.asStateFlow()

    var uiLoading by mutableStateOf(false)
        private set
    var uiError by mutableStateOf<String?>(null)
        private set

    fun loadPosts() {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            _uiState.value = AdminBlogState.Loading

            val result = blogRepository.getAdminBlogPosts()

            result.onSuccess { response ->
                uiLoading = false
                _uiState.value = AdminBlogState.PostsLoaded(response)
            }.onFailure { error ->
                uiLoading = false
                uiError = error.message ?: "Failed to load posts"
                _uiState.value = AdminBlogState.Error(error.message ?: "Failed to load posts")
            }
        }
    }

    fun loadPost(id: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            _uiState.value = AdminBlogState.Loading

            val result = blogRepository.getAdminBlogPost(id)

            result.onSuccess { post ->
                uiLoading = false
                _uiState.value = AdminBlogState.PostDetailLoaded(post)
            }.onFailure { error ->
                uiLoading = false
                uiError = error.message ?: "Failed to load post"
                _uiState.value = AdminBlogState.Error(error.message ?: "Failed to load post")
            }
        }
    }

    fun createPost(
        title: String,
        slug: String,
        content: String,
        excerpt: String? = null,
        coverImage: String? = null,
        published: Boolean = false,
        onSuccess: (AdminBlogPost) -> Unit
    ) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null

            val request = CreateBlogPostRequest(
                title = title,
                slug = slug,
                content = content,
                excerpt = excerpt,
                coverImage = coverImage,
                published = published
            )

            val result = blogRepository.createBlogPost(request)

            result.onSuccess { post ->
                uiLoading = false
                onSuccess(post)
            }.onFailure { error ->
                uiLoading = false
                uiError = error.message ?: "Failed to create post"
            }
        }
    }

    fun updatePost(
        id: String,
        title: String? = null,
        slug: String? = null,
        content: String? = null,
        excerpt: String? = null,
        coverImage: String? = null,
        published: Boolean? = null,
        onSuccess: (AdminBlogPost) -> Unit
    ) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null

            val request = UpdateBlogPostRequest(
                title = title,
                slug = slug,
                content = content,
                excerpt = excerpt,
                coverImage = coverImage,
                published = published
            )

            val result = blogRepository.updateBlogPost(id, request)

            result.onSuccess { post ->
                uiLoading = false
                onSuccess(post)
            }.onFailure { error ->
                uiLoading = false
                uiError = error.message ?: "Failed to update post"
            }
        }
    }

    fun deletePost(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null

            val result = blogRepository.deleteBlogPost(id)

            result.onSuccess {
                uiLoading = false
                onSuccess()
            }.onFailure { error ->
                uiLoading = false
                uiError = error.message ?: "Failed to delete post"
            }
        }
    }

    fun clearError() {
        uiError = null
    }
}
