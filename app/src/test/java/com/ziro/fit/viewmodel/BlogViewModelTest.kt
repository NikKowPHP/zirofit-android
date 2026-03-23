package com.ziro.fit.viewmodel

import android.util.Log
import com.ziro.fit.data.repository.BlogRepository
import com.ziro.fit.model.BlogListResponse
import com.ziro.fit.model.BlogPost
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
class BlogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: BlogRepository = mockk(relaxed = true)

    private fun createBlogPost(
        id: String = "post1",
        title: String = "Test Blog Post"
    ) = BlogPost(
        id = id,
        slug = "test-blog-post",
        title = title,
        excerpt = "This is a test excerpt",
        coverImage = null,
        publishedAt = "2026-03-22T10:00:00Z",
        author = null
    )

    private fun createBlogListResponse(
        posts: List<BlogPost>,
        total: Int,
        page: Int = 1
    ) = BlogListResponse(
        posts = posts,
        total = total,
        page = page,
        pageSize = 10
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `init loads posts automatically`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(createBlogPost()),
            total = 1
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        coVerify { repository.getBlogPosts(any(), any()) }
        assertEquals(1, viewModel.posts.size)
    }

    @Test
    fun `loadPosts success with posts updates uiState to Success and populates posts`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(
                createBlogPost(id = "post1", title = "First Post"),
                createBlogPost(id = "post2", title = "Second Post")
            ),
            total = 5
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is BlogUiState.Success)
        assertEquals(2, viewModel.posts.size)
        assertTrue(viewModel.hasMore)
    }

    @Test
    fun `loadPosts success with empty posts updates uiState to Empty`() = runTest {
        val response = createBlogListResponse(
            posts = emptyList(),
            total = 0
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is BlogUiState.Empty)
        assertTrue(viewModel.posts.isEmpty())
        assertFalse(viewModel.hasMore)
    }

    @Test
    fun `loadPosts failure updates uiState to Error and sets errorMessage`() = runTest {
        coEvery { repository.getBlogPosts(any(), any()) } returns
            Result.failure(RuntimeException("Network Error"))

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is BlogUiState.Error)
        assertEquals("Network Error", (viewModel.uiState as BlogUiState.Error).message)
        assertEquals("Network Error", viewModel.errorMessage)
    }

    @Test
    fun `loadMore when already loading more does nothing`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(createBlogPost()),
            total = 10
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        viewModel.loadMore()
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)
        viewModel.loadMore()
        advanceUntilIdle()

        coVerify(exactly = 2) { repository.getBlogPosts(any(), any()) }
    }

    @Test
    fun `loadMore when no more pages does nothing`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(createBlogPost()),
            total = 1
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.hasMore)

        viewModel.loadMore()
        advanceUntilIdle()

        coVerify(exactly = 2) { repository.getBlogPosts(any(), any()) }
    }

    @Test
    fun `loadMore success appends posts and increments page`() = runTest {
        val firstPage = createBlogListResponse(
            posts = listOf(createBlogPost(id = "post1", title = "First Post")),
            total = 10,
            page = 1
        )
        val secondPage = createBlogListResponse(
            posts = listOf(createBlogPost(id = "post2", title = "Second Post")),
            total = 10,
            page = 2
        )
        coEvery { repository.getBlogPosts(page = 1, any()) } returns Result.success(firstPage)
        coEvery { repository.getBlogPosts(page = 2, any()) } returns Result.success(secondPage)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.posts.size)
        assertEquals(1, viewModel.currentPage)

        viewModel.loadMore()
        advanceUntilIdle()

        assertEquals(2, viewModel.posts.size)
        assertEquals(2, viewModel.currentPage)
        assertTrue(viewModel.hasMore)
    }

    @Test
    fun `loadMore failure sets errorMessage and resets isLoadingMore`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(createBlogPost()),
            total = 10
        )
        coEvery { repository.getBlogPosts(page = 1, any()) } returns Result.success(response)
        coEvery { repository.getBlogPosts(page = 2, any()) } returns
            Result.failure(RuntimeException("Load More Error"))

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        assertEquals("Load More Error", viewModel.errorMessage)
        assertFalse(viewModel.isLoadingMore)
    }

    @Test
    fun `refresh resets page to 1 and reloads`() = runTest {
        val firstLoad = createBlogListResponse(
            posts = listOf(createBlogPost(id = "post1")),
            total = 10,
            page = 1
        )
        val secondLoad = createBlogListResponse(
            posts = listOf(createBlogPost(id = "post2")),
            total = 10,
            page = 1
        )
        coEvery { repository.getBlogPosts(page = 1, any()) } returnsMany
            listOf(Result.success(firstLoad), Result.success(secondLoad))

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()
        assertEquals(2, viewModel.currentPage)

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(1, viewModel.currentPage)
    }

    @Test
    fun `clearError clears errorMessage and triggers reload when in Error state`() = runTest {
        coEvery { repository.getBlogPosts(any(), any()) } returns
            Result.failure(RuntimeException("Network Error"))

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is BlogUiState.Error)
        assertNotNull(viewModel.errorMessage)

        coEvery { repository.getBlogPosts(any(), any()) } returns
            Result.success(createBlogListResponse(emptyList(), 0))
        viewModel.clearError()
        advanceUntilIdle()

        assertNull(viewModel.errorMessage)
        coVerify(exactly = 2) { repository.getBlogPosts(any(), any()) }
    }

    @Test
    fun `hasMore is false when posts size is greater than or equal to total`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(createBlogPost()),
            total = 1
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.hasMore)
    }

    @Test
    fun `hasMore is true when posts size is less than total`() = runTest {
        val response = createBlogListResponse(
            posts = listOf(createBlogPost()),
            total = 10
        )
        coEvery { repository.getBlogPosts(any(), any()) } returns Result.success(response)

        val viewModel = BlogViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.hasMore)
    }
}
