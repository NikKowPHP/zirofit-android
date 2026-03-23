package com.ziro.fit.ui.admin.blog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ziro.fit.model.UpdateBlogPostRequest
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.BlogAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogEditScreen(
    postId: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: BlogAdminViewModel = hiltViewModel()
) {
    val selectedPost = viewModel.selectedPost
    val isLoading = viewModel.isLoading

    var title by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var excerpt by remember { mutableStateOf("") }
    var coverImage by remember { mutableStateOf("") }
    var published by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var slugError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        viewModel.loadPostForEdit(postId)
    }

    LaunchedEffect(selectedPost) {
        if (selectedPost != null && !initialized) {
            title = selectedPost.title
            slug = selectedPost.slug
            content = selectedPost.content ?: ""
            excerpt = selectedPost.excerpt ?: ""
            coverImage = selectedPost.coverImage ?: ""
            published = selectedPost.published
            initialized = true
        }
    }

    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            viewModel.clearSaveSuccess()
            onSuccess()
        }
    }

    fun validate(): Boolean {
        titleError = title.isBlank()
        slugError = slug.isBlank()
        contentError = content.isBlank()
        return !titleError && !slugError && !contentError
    }

    fun updatePost() {
        if (!validate()) return

        val request = UpdateBlogPostRequest(
            title = title.trim().takeIf { it != selectedPost?.title },
            slug = slug.trim().takeIf { it != selectedPost?.slug },
            content = content.trim().takeIf { it != (selectedPost?.content ?: "") },
            excerpt = excerpt.trim().ifBlank { null }.takeIf { it != selectedPost?.excerpt },
            coverImage = coverImage.trim().ifBlank { null }.takeIf { it != selectedPost?.coverImage },
            published = published.takeIf { it != selectedPost?.published }
        )

        val hasChanges = request.title != null || request.slug != null || request.content != null ||
                request.excerpt != null || request.coverImage != null || request.published != null

        if (hasChanges) {
            viewModel.updatePost(postId, request)
        } else {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Post", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrongBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StrongRed)
                    }
                }
            )
        },
        containerColor = StrongBackground
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StrongBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongDivider,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = StrongBlue,
                        errorBorderColor = StrongRed,
                        errorLabelColor = StrongRed
                    ),
                    isError = titleError,
                    supportingText = if (titleError) {{ Text("Title is required") }} else null,
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = slug,
                    onValueChange = {
                        slug = it.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9-]"), "")
                        slugError = false
                    },
                    label = { Text("Slug *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongDivider,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = StrongBlue,
                        errorBorderColor = StrongRed,
                        errorLabelColor = StrongRed
                    ),
                    isError = slugError,
                    supportingText = if (slugError) {{ Text("Slug is required") }} else null,
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        contentError = false
                    },
                    label = { Text("Content *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongDivider,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = StrongBlue,
                        errorBorderColor = StrongRed,
                        errorLabelColor = StrongRed
                    ),
                    isError = contentError,
                    supportingText = if (contentError) {{ Text("Content is required") }} else null,
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = excerpt,
                    onValueChange = { excerpt = it },
                    label = { Text("Excerpt (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongDivider,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = StrongBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = coverImage,
                    onValueChange = { coverImage = it },
                    label = { Text("Cover Image URL (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongDivider,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = StrongBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Published",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (published) "This post is visible to users" else "This post is saved as draft",
                            color = StrongTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = published,
                        onCheckedChange = { published = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = StrongGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = StrongSecondaryBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { updatePost() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StrongBlue),
                    enabled = !viewModel.isSaving
                ) {
                    if (viewModel.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete \"${selectedPost?.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePost(postId)
                        showDeleteDialog = false
                        onSuccess()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = StrongRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (viewModel.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Error") },
            text = { Text(viewModel.error ?: "Unknown error") },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) { Text("OK") }
            }
        )
    }
}
