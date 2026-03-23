package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.BlogRepository
import com.ziro.fit.model.BlogPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BlogUiState {
    object Loading : BlogUiState()
    object Success : BlogUiState()
    object Empty : BlogUiState()
    data class Error(val message: String) : BlogUiState()
}

@HiltViewModel
class BlogViewModel @Inject constructor(
    private val blogRepository: BlogRepository
) : ViewModel() {

    var uiState by mutableStateOf<BlogUiState>(BlogUiState.Loading)
        private set

    var posts by mutableStateOf<List<BlogPost>>(emptyList())
        private set

    var currentPage by mutableIntStateOf(1)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var hasMore by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            uiState = BlogUiState.Loading
            currentPage = 1

            blogRepository.getBlogPosts(page = 1)
                .onSuccess { response ->
                    posts = response.posts
                    hasMore = posts.size < response.total
                    uiState = if (posts.isEmpty()) BlogUiState.Empty else BlogUiState.Success
                }
                .onFailure { e ->
                    errorMessage = e.message
                    uiState = BlogUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true
            val nextPage = currentPage + 1

            blogRepository.getBlogPosts(page = nextPage)
                .onSuccess { response ->
                    posts = posts + response.posts
                    currentPage = nextPage
                    hasMore = posts.size < response.total
                }
                .onFailure { e ->
                    errorMessage = e.message
                }
                .also {
                    isLoadingMore = false
                }
        }
    }

    fun refresh() {
        loadPosts()
    }

    fun clearError() {
        errorMessage = null
        if (uiState is BlogUiState.Error) {
            loadPosts()
        }
    }
}
