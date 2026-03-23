package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.BlogRepository
import com.ziro.fit.model.BlogPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BlogPostUiState {
    object Loading : BlogPostUiState()
    object Success : BlogPostUiState()
    data class Error(val message: String) : BlogPostUiState()
}

@HiltViewModel
class BlogPostViewModel @Inject constructor(
    private val blogRepository: BlogRepository
) : ViewModel() {

    var uiState by mutableStateOf<BlogPostUiState>(BlogPostUiState.Loading)
        private set

    var post by mutableStateOf<BlogPost?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadPost(slug: String) {
        viewModelScope.launch {
            uiState = BlogPostUiState.Loading

            blogRepository.getBlogPost(slug)
                .onSuccess { blogPost ->
                    post = blogPost
                    uiState = BlogPostUiState.Success
                }
                .onFailure { e ->
                    errorMessage = e.message
                    uiState = BlogPostUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
