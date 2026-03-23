package com.ziro.fit.viewmodel

import android.util.Log
import com.ziro.fit.data.repository.BlogRepository
import com.ziro.fit.model.AdminBlogListResponse
import com.ziro.fit.model.AdminBlogPost
import com.ziro.fit.model.CreateBlogPostRequest
import com.ziro.fit.model.UpdateBlogPostRequest
import com.ziro.fit.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminBlogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: BlogRepository = mockk(relaxed = true)

    private fun createAdminBlogPost(
        id: String = "post1",
        title: String = "Test Blog Post"
    ) = AdminBlogPost(
        id = id,
        title = title,
        slug = "test-blog-post",
        published = false,
        authorId = "author1",
        authorName = "Test Author",
        createdAt = "2026-03-22T10:00:00Z",
        updatedAt = "2026-03-22T10:00:00Z",
        publishedAt = null,
        excerpt = "Test excerpt",
        coverImage = null,
        content = "Test content"
    )

    private fun createAdminBlogListResponse(
        posts: List<AdminBlogPost>,
        total: Int = posts.size
    ) = AdminBlogListResponse(
        posts = posts,
        total = total
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `init calls loadPosts`() = runTest {
        val response = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(response)

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        coVerify { repository.getAdminBlogPosts() }
        assertEquals(1, viewModel.posts.size)
    }

    @Test
    fun `loadPosts success updates uiState to Success and populates posts`() = runTest {
        val response = createAdminBlogListResponse(
            posts = listOf(
                createAdminBlogPost(id = "post1", title = "First Post"),
                createAdminBlogPost(id = "post2", title = "Second Post")
            )
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(response)

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is AdminBlogUiState.Success)
        assertEquals(2, viewModel.posts.size)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `loadPosts failure updates uiState to Error`() = runTest {
        coEvery { repository.getAdminBlogPosts() } returns
            Result.failure(RuntimeException("Network Error"))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is AdminBlogUiState.Error)
        assertEquals("Network Error", viewModel.error)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `deletePost success removes post from list and updates uiState to Success`() = runTest {
        val initialResponse = createAdminBlogListResponse(
            posts = listOf(
                createAdminBlogPost(id = "post1"),
                createAdminBlogPost(id = "post2")
            )
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        coEvery { repository.deleteBlogPost("post1") } returns Result.success(Unit)

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        assertEquals(2, viewModel.posts.size)

        viewModel.deletePost("post1")
        advanceUntilIdle()

        assertEquals(1, viewModel.posts.size)
        assertEquals("post2", viewModel.posts.first().id)
        assertTrue(viewModel.uiState is AdminBlogUiState.Success)
    }

    @Test
    fun `deletePost failure updates uiState to Error and sets error`() = runTest {
        val initialResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        coEvery { repository.deleteBlogPost(any()) } returns
            Result.failure(RuntimeException("Delete Error"))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        viewModel.deletePost("post1")
        advanceUntilIdle()

        assertTrue(viewModel.uiState is AdminBlogUiState.Error)
        assertEquals("Delete Error", viewModel.error)
    }

    @Test
    fun `loadPostForEdit success sets selectedPost`() = runTest {
        val loadPostsResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        val postResponse = createAdminBlogPost(id = "post1", title = "Edited Post")
        coEvery { repository.getAdminBlogPosts() } returns Result.success(loadPostsResponse)
        coEvery { repository.getAdminBlogPost("post1") } returns Result.success(postResponse)

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        viewModel.loadPostForEdit("post1")
        advanceUntilIdle()

        assertNotNull(viewModel.selectedPost)
        assertEquals("Edited Post", viewModel.selectedPost?.title)
    }

    @Test
    fun `loadPostForEdit failure sets error`() = runTest {
        val loadPostsResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(loadPostsResponse)
        coEvery { repository.getAdminBlogPost(any()) } returns
            Result.failure(RuntimeException("Load Error"))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        viewModel.loadPostForEdit("post1")
        advanceUntilIdle()

        assertEquals("Load Error", viewModel.error)
    }

    @Test
    fun `createPost success sets saveSuccess to true and calls loadPosts`() = runTest {
        val initialResponse = createAdminBlogListResponse(posts = emptyList())
        val loadPostsAfterCreate = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        coEvery { repository.getAdminBlogPosts() } returnsMany
            listOf(Result.success(initialResponse), Result.success(loadPostsAfterCreate))
        coEvery { repository.createBlogPost(any()) } returns Result.success(createAdminBlogPost())

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        val request = CreateBlogPostRequest(
            title = "New Post",
            slug = "new-post",
            content = "New content"
        )
        viewModel.createPost(request)
        advanceUntilIdle()

        assertTrue(viewModel.saveSuccess)
        assertFalse(viewModel.isSaving)
        coVerify { repository.getAdminBlogPosts() }
    }

    @Test
    fun `createPost failure sets error`() = runTest {
        val initialResponse = createAdminBlogListResponse(posts = emptyList())
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        coEvery { repository.createBlogPost(any()) } returns
            Result.failure(RuntimeException("Create Error"))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        val request = CreateBlogPostRequest(
            title = "New Post",
            slug = "new-post",
            content = "New content"
        )
        viewModel.createPost(request)
        advanceUntilIdle()

        assertEquals("Create Error", viewModel.error)
        assertFalse(viewModel.isSaving)
    }

    @Test
    fun `updatePost success sets saveSuccess to true and updates post in list`() = runTest {
        val initialResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost(id = "post1", title = "Original Title"))
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        val updatedPost = createAdminBlogPost(id = "post1", title = "Updated Title")
        coEvery { repository.updateBlogPost("post1", any()) } returns Result.success(updatedPost)

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        val request = UpdateBlogPostRequest(title = "Updated Title")
        viewModel.updatePost("post1", request)
        advanceUntilIdle()

        assertTrue(viewModel.saveSuccess)
        assertFalse(viewModel.isSaving)
        assertEquals("Updated Title", viewModel.posts.first().title)
        assertEquals("Updated Title", viewModel.selectedPost?.title)
    }

    @Test
    fun `updatePost failure sets error`() = runTest {
        val initialResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost(id = "post1"))
        )
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        coEvery { repository.updateBlogPost(any(), any()) } returns
            Result.failure(RuntimeException("Update Error"))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        val request = UpdateBlogPostRequest(title = "Updated Title")
        viewModel.updatePost("post1", request)
        advanceUntilIdle()

        assertEquals("Update Error", viewModel.error)
        assertFalse(viewModel.isSaving)
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        coEvery { repository.getAdminBlogPosts() } returns
            Result.failure(RuntimeException("Network Error"))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        assertNotNull(viewModel.error)

        viewModel.clearError()

        assertNull(viewModel.error)
    }

    @Test
    fun `clearSaveSuccess sets saveSuccess to false`() = runTest {
        val initialResponse = createAdminBlogListResponse(posts = emptyList())
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        coEvery { repository.createBlogPost(any()) } returns Result.success(createAdminBlogPost())

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        val request = CreateBlogPostRequest(
            title = "New Post",
            slug = "new-post",
            content = "New content"
        )
        viewModel.createPost(request)
        advanceUntilIdle()

        assertTrue(viewModel.saveSuccess)

        viewModel.clearSaveSuccess()

        assertFalse(viewModel.saveSuccess)
    }

    @Test
    fun `clearSelectedPost sets selectedPost to null`() = runTest {
        val initialResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost(id = "post1", title = "Test Post"))
        )
        val postResponse = createAdminBlogPost(id = "post1", title = "Test Post")
        coEvery { repository.getAdminBlogPosts() } returns Result.success(initialResponse)
        coEvery { repository.getAdminBlogPost("post1") } returns Result.success(postResponse)

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        viewModel.loadPostForEdit("post1")
        advanceUntilIdle()

        assertNotNull(viewModel.selectedPost)

        viewModel.clearSelectedPost()

        assertNull(viewModel.selectedPost)
    }

    @Test
    fun `refresh calls loadPosts`() = runTest {
        val initialResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        val refreshResponse = createAdminBlogListResponse(
            posts = listOf(createAdminBlogPost())
        )
        coEvery { repository.getAdminBlogPosts() } returnsMany
            listOf(Result.success(initialResponse), Result.success(refreshResponse))

        val viewModel = BlogAdminViewModel(repository)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        coVerify(exactly = 2) { repository.getAdminBlogPosts() }
    }
}
