package com.ziro.fit.ui.admin.blog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.CreateBlogPostRequest
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.BlogAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogCreateScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: BlogAdminViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var excerpt by remember { mutableStateOf("") }
    var coverImage by remember { mutableStateOf("") }
    var published by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var slugError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            viewModel.clearSaveSuccess()
            onSuccess()
        }
    }

    fun generateSlugFromTitle() {
        if (slug.isBlank()) {
            slug = title.lowercase()
                .replace(" ", "-")
                .replace(Regex("[^a-z0-9-]"), "")
        }
    }

    fun validate(): Boolean {
        titleError = title.isBlank()
        slugError = slug.isBlank()
        contentError = content.isBlank()
        return !titleError && !slugError && !contentError
    }

    fun savePost() {
        if (!validate()) return

        val request = CreateBlogPostRequest(
            title = title.trim(),
            slug = slug.trim(),
            content = content.trim(),
            excerpt = excerpt.trim().ifBlank { null },
            coverImage = coverImage.trim().ifBlank { null },
            published = published
        )
        viewModel.createPost(request)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrongBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = StrongBackground
    ) { padding ->
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
                supportingText = if (slugError) {{ Text("Slug is required") }} else {
                    {
                        Text(
                            "Auto-generated from title. Click to edit.",
                            color = StrongTextSecondary
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { generateSlugFromTitle() }) {
                Text("Generate from title", color = StrongBlue, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                        text = if (published) "This post will be visible to users" else "This post will be saved as draft",
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
                onClick = { savePost() },
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
                        text = "Create Post",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
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
