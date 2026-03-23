package com.ziro.fit.ui.blog

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.BlogPostUiState
import com.ziro.fit.viewmodel.BlogPostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogPostScreen(
    slug: String,
    onBack: () -> Unit,
    viewModel: BlogPostViewModel = hiltViewModel()
) {
    LaunchedEffect(slug) {
        viewModel.loadPost(slug)
    }

    Scaffold(
        containerColor = StrongBackground,
        topBar = {
            TopAppBar(
                title = { Text("Blog", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        when (viewModel.uiState) {
            is BlogPostUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StrongBlue)
                }
            }

            is BlogPostUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (viewModel.uiState as BlogPostUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPost(slug) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is BlogPostUiState.Success -> {
                viewModel.post?.let { post ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (!post.coverImage.isNullOrBlank()) {
                            AsyncImage(
                                model = post.coverImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = post.title,
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = post.author?.name ?: "Unknown Author",
                                    color = StrongBlue,
                                    fontSize = 14.sp
                                )

                                if (!post.publishedAt.isNullOrBlank()) {
                                    Text(
                                        text = " • ${formatDate(post.publishedAt)}",
                                        color = StrongTextSecondary,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            HorizontalDivider(color = StrongDivider)
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            val contentText = parseHtmlToAnnotatedString(post.content ?: "No content available.")
                            Text(
                                text = contentText,
                                color = StrongTextSecondary,
                                lineHeight = 24.sp,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }

    if (viewModel.errorMessage != null && viewModel.uiState !is BlogPostUiState.Error) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Error") },
            text = { Text(viewModel.errorMessage ?: "Unknown error") },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) { Text("OK") }
            }
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        dateString.take(10)
    } catch (e: Exception) {
        dateString
    }
}

private fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    return try {
        val plainText = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        buildAnnotatedString {
            append(plainText)
        }
    } catch (e: Exception) {
        buildAnnotatedString {
            withStyle(SpanStyle(color = StrongTextSecondary)) {
                append(html)
            }
        }
    }
}
