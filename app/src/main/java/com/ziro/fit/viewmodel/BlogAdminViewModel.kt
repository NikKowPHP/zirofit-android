package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.BlogRepository
import com.ziro.fit.model.AdminBlogPost
import com.ziro.fit.model.CreateBlogPostRequest
import com.ziro.fit.model.UpdateBlogPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminBlogUiState {
    object Loading : AdminBlogUiState()
    object Success : AdminBlogUiState()
    object Deleting : AdminBlogUiState()
    object Error : AdminBlogUiState()
}

@HiltViewModel
class BlogAdminViewModel @Inject constructor(
    private val blogRepository: BlogRepository
) : ViewModel() {

    var uiState by mutableStateOf<AdminBlogUiState>(AdminBlogUiState.Loading)
        private set

    var posts by mutableStateOf<List<AdminBlogPost>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var selectedPost by mutableStateOf<AdminBlogPost?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            uiState = AdminBlogUiState.Loading
            isLoading = true

            blogRepository.getAdminBlogPosts()
                .onSuccess { response ->
                    posts = response.posts
                    uiState = AdminBlogUiState.Success
                }
                .onFailure { e ->
                    error = e.message
                    uiState = AdminBlogUiState.Error
                }
                .also {
                    isLoading = false
                }
        }
    }

    fun refresh() {
        loadPosts()
    }

    fun deletePost(id: String) {
        viewModelScope.launch {
            uiState = AdminBlogUiState.Deleting

            blogRepository.deleteBlogPost(id)
                .onSuccess {
                    posts = posts.filter { it.id != id }
                    uiState = AdminBlogUiState.Success
                }
                .onFailure { e ->
                    error = e.message
                    uiState = AdminBlogUiState.Error
                }
        }
    }

    fun loadPostForEdit(id: String) {
        viewModelScope.launch {
            isLoading = true
            blogRepository.getAdminBlogPost(id)
                .onSuccess { post ->
                    selectedPost = post
                }
                .onFailure { e ->
                    error = e.message
                }
                .also {
                    isLoading = false
                }
        }
    }

    fun createPost(request: CreateBlogPostRequest) {
        viewModelScope.launch {
            isSaving = true
            saveSuccess = false

            blogRepository.createBlogPost(request)
                .onSuccess {
                    saveSuccess = true
                    loadPosts()
                }
                .onFailure { e ->
                    error = e.message
                }
                .also {
                    isSaving = false
                }
        }
    }

    fun updatePost(id: String, request: UpdateBlogPostRequest) {
        viewModelScope.launch {
            isSaving = true
            saveSuccess = false

            blogRepository.updateBlogPost(id, request)
                .onSuccess { updatedPost ->
                    saveSuccess = true
                    posts = posts.map { if (it.id == id) updatedPost else it }
                    selectedPost = updatedPost
                }
                .onFailure { e ->
                    error = e.message
                }
                .also {
                    isSaving = false
                }
        }
    }

    fun clearError() {
        error = null
    }

    fun clearSaveSuccess() {
        saveSuccess = false
    }

    fun clearSelectedPost() {
        selectedPost = null
    }
}
