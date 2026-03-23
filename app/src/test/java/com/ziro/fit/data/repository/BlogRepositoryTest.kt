package com.ziro.fit.data.repository

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BlogRepositoryTest {
    private val api: ZiroApi = mockk(relaxed = true)
    private val repository = BlogRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `getBlogPosts success returns blog posts`() = runBlocking {
        val post = BlogPost(
            id = "1",
            slug = "my-first-post",
            title = "My First Post",
            excerpt = "This is an excerpt",
            coverImage = "https://example.com/cover.jpg",
            publishedAt = "2024-01-01",
            author = BlogAuthor(id = "user1", name = "John Doe")
        )
        val responseData = BlogListResponse(
            posts = listOf(post),
            total = 1,
            page = 1,
            pageSize = 10
        )
        val response = ApiResponse(
            success = true,
            data = responseData
        )
        coEvery { api.getBlogPosts(1, 10) } returns response

        val result = repository.getBlogPosts()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.posts.size)
        assertEquals("My First Post", result.getOrNull()!!.posts[0].title)
    }

    @Test
    fun `getBlogPosts pagination page 1 returns first page`() = runBlocking {
        val posts = (1..10).map { i ->
            BlogPost(
                id = "$i",
                slug = "post-$i",
                title = "Post $i",
                excerpt = null,
                coverImage = null,
                publishedAt = null,
                author = null
            )
        }
        val responseData = BlogListResponse(posts = posts, total = 25, page = 1, pageSize = 10)
        val response = ApiResponse(success = true, data = responseData)
        coEvery { api.getBlogPosts(1, 10) } returns response

        val result = repository.getBlogPosts(page = 1, pageSize = 10)

        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrNull()!!.posts.size)
        assertEquals(25, result.getOrNull()!!.total)
        assertEquals(1, result.getOrNull()!!.page)
    }

    @Test
    fun `getBlogPosts pagination page 2 returns second page`() = runBlocking {
        val posts = (11..20).map { i ->
            BlogPost(
                id = "$i",
                slug = "post-$i",
                title = "Post $i",
                excerpt = null,
                coverImage = null,
                publishedAt = null,
                author = null
            )
        }
        val responseData = BlogListResponse(posts = posts, total = 25, page = 2, pageSize = 10)
        val response = ApiResponse(success = true, data = responseData)
        coEvery { api.getBlogPosts(2, 10) } returns response

        val result = repository.getBlogPosts(page = 2, pageSize = 10)

        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrNull()!!.posts.size)
        assertEquals("Post 11", result.getOrNull()!!.posts[0].title)
        assertEquals(2, result.getOrNull()!!.page)
    }

    @Test
    fun `getBlogPosts API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<BlogListResponse>(
            success = false,
            data = null,
            message = "Failed to load blog posts"
        )
        coEvery { api.getBlogPosts(1, 10) } returns response

        val result = repository.getBlogPosts()

        assertTrue(result.isFailure)
        assertEquals("Failed to load blog posts", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getBlogPosts API throws exception returns Result failure`() = runBlocking {
        coEvery { api.getBlogPosts(1, 10) } throws RuntimeException("Network Error")

        val result = repository.getBlogPosts()

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getBlogPosts API returns null data returns Result failure`() = runBlocking {
        val response = ApiResponse<BlogListResponse>(
            success = true,
            data = null
        )
        coEvery { api.getBlogPosts(1, 10) } returns response

        val result = repository.getBlogPosts()

        assertTrue(result.isFailure)
    }

    @Test
    fun `getBlogPost success returns blog post`() = runBlocking {
        val post = BlogPost(
            id = "1",
            slug = "my-first-post",
            title = "My First Post",
            excerpt = "This is an excerpt",
            coverImage = "https://example.com/cover.jpg",
            publishedAt = "2024-01-01",
            author = BlogAuthor(id = "user1", name = "John Doe"),
            content = "<p>Full content here</p>"
        )
        val response = ApiResponse(success = true, data = post)
        coEvery { api.getBlogPost("my-first-post") } returns response

        val result = repository.getBlogPost("my-first-post")

        assertTrue(result.isSuccess)
        assertEquals("My First Post", result.getOrNull()!!.title)
        assertEquals("<p>Full content here</p>", result.getOrNull()!!.content)
    }

    @Test
    fun `getBlogPost API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<BlogPost>(
            success = false,
            data = null,
            message = "Blog post not found"
        )
        coEvery { api.getBlogPost("nonexistent") } returns response

        val result = repository.getBlogPost("nonexistent")

        assertTrue(result.isFailure)
        assertEquals("Blog post not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getBlogPost API throws exception returns Result failure`() = runBlocking {
        coEvery { api.getBlogPost("my-first-post") } throws RuntimeException("Network Error")

        val result = repository.getBlogPost("my-first-post")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getBlogPost API returns null data returns Result failure`() = runBlocking {
        val response = ApiResponse<BlogPost>(
            success = true,
            data = null
        )
        coEvery { api.getBlogPost("my-first-post") } returns response

        val result = repository.getBlogPost("my-first-post")

        assertTrue(result.isFailure)
    }

    @Test
    fun `getAdminBlogPosts success returns admin blog posts`() = runBlocking {
        val adminPost = AdminBlogPost(
            id = "1",
            title = "Admin Post",
            slug = "admin-post",
            published = true,
            authorId = "user1",
            authorName = "Admin User",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-02",
            publishedAt = "2024-01-01",
            excerpt = "Admin excerpt",
            coverImage = "https://example.com/admin-cover.jpg",
            content = "Admin content"
        )
        val responseData = AdminBlogListResponse(posts = listOf(adminPost), total = 1)
        val response = ApiResponse(success = true, data = responseData)
        coEvery { api.getAdminBlogPosts() } returns response

        val result = repository.getAdminBlogPosts()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.posts.size)
        assertEquals("Admin Post", result.getOrNull()!!.posts[0].title)
        assertTrue(result.getOrNull()!!.posts[0].published)
    }

    @Test
    fun `getAdminBlogPosts API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogListResponse>(
            success = false,
            data = null,
            message = "Failed to load admin blog posts"
        )
        coEvery { api.getAdminBlogPosts() } returns response

        val result = repository.getAdminBlogPosts()

        assertTrue(result.isFailure)
        assertEquals("Failed to load admin blog posts", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAdminBlogPosts API throws exception returns Result failure`() = runBlocking {
        coEvery { api.getAdminBlogPosts() } throws RuntimeException("Network Error")

        val result = repository.getAdminBlogPosts()

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAdminBlogPosts API returns null data returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogListResponse>(
            success = true,
            data = null
        )
        coEvery { api.getAdminBlogPosts() } returns response

        val result = repository.getAdminBlogPosts()

        assertTrue(result.isFailure)
    }

    @Test
    fun `createBlogPost success with all fields returns created post`() = runBlocking {
        val createdPost = AdminBlogPost(
            id = "new-1",
            title = "New Post",
            slug = "new-post",
            published = true,
            authorId = "user1",
            authorName = "John Doe",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            publishedAt = "2024-01-01",
            excerpt = "New post excerpt",
            coverImage = "https://example.com/new-cover.jpg",
            content = "<p>New post content</p>"
        )
        val response = ApiResponse(success = true, data = createdPost)
        val request = CreateBlogPostRequest(
            title = "New Post",
            slug = "new-post",
            content = "<p>New post content</p>",
            excerpt = "New post excerpt",
            coverImage = "https://example.com/new-cover.jpg",
            published = true
        )
        coEvery { api.createBlogPost(request) } returns response

        val result = repository.createBlogPost(request)

        assertTrue(result.isSuccess)
        assertEquals("New Post", result.getOrNull()!!.title)
        assertEquals("new-post", result.getOrNull()!!.slug)
        assertTrue(result.getOrNull()!!.published)
    }

    @Test
    fun `createBlogPost with optional fields omitted returns created post`() = runBlocking {
        val createdPost = AdminBlogPost(
            id = "new-2",
            title = "Minimal Post",
            slug = "minimal-post",
            published = false,
            authorId = "user1",
            authorName = "John Doe",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            publishedAt = null,
            excerpt = null,
            coverImage = null,
            content = "<p>Content only</p>"
        )
        val response = ApiResponse(success = true, data = createdPost)
        val request = CreateBlogPostRequest(
            title = "Minimal Post",
            slug = "minimal-post",
            content = "<p>Content only</p>"
        )
        coEvery { api.createBlogPost(request) } returns response

        val result = repository.createBlogPost(request)

        assertTrue(result.isSuccess)
        assertEquals("Minimal Post", result.getOrNull()!!.title)
        assertFalse(result.getOrNull()!!.published)
    }

    @Test
    fun `createBlogPost API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogPost>(
            success = false,
            data = null,
            message = "Failed to create blog post"
        )
        val request = CreateBlogPostRequest(
            title = "Bad Post",
            slug = "bad-post",
            content = "Content"
        )
        coEvery { api.createBlogPost(request) } returns response

        val result = repository.createBlogPost(request)

        assertTrue(result.isFailure)
        assertEquals("Failed to create blog post", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createBlogPost API throws exception returns Result failure`() = runBlocking {
        val request = CreateBlogPostRequest(
            title = "New Post",
            slug = "new-post",
            content = "Content"
        )
        coEvery { api.createBlogPost(request) } throws RuntimeException("Network Error")

        val result = repository.createBlogPost(request)

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createBlogPost API returns null data returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogPost>(
            success = true,
            data = null
        )
        val request = CreateBlogPostRequest(
            title = "New Post",
            slug = "new-post",
            content = "Content"
        )
        coEvery { api.createBlogPost(request) } returns response

        val result = repository.createBlogPost(request)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getAdminBlogPost success with specific id returns admin post`() = runBlocking {
        val adminPost = AdminBlogPost(
            id = "admin-123",
            title = "Admin Only Post",
            slug = "admin-only-post",
            published = false,
            authorId = "admin1",
            authorName = "Admin User",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            publishedAt = null,
            excerpt = "Admin excerpt",
            coverImage = null,
            content = "Admin content"
        )
        val response = ApiResponse(success = true, data = adminPost)
        coEvery { api.getAdminBlogPost("admin-123") } returns response

        val result = repository.getAdminBlogPost("admin-123")

        assertTrue(result.isSuccess)
        assertEquals("admin-123", result.getOrNull()!!.id)
        assertEquals("Admin Only Post", result.getOrNull()!!.title)
        assertFalse(result.getOrNull()!!.published)
    }

    @Test
    fun `getAdminBlogPost API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogPost>(
            success = false,
            data = null,
            message = "Admin blog post not found"
        )
        coEvery { api.getAdminBlogPost("nonexistent") } returns response

        val result = repository.getAdminBlogPost("nonexistent")

        assertTrue(result.isFailure)
        assertEquals("Admin blog post not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAdminBlogPost API throws exception returns Result failure`() = runBlocking {
        coEvery { api.getAdminBlogPost("admin-123") } throws RuntimeException("Network Error")

        val result = repository.getAdminBlogPost("admin-123")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAdminBlogPost API returns null data returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogPost>(
            success = true,
            data = null
        )
        coEvery { api.getAdminBlogPost("admin-123") } returns response

        val result = repository.getAdminBlogPost("admin-123")

        assertTrue(result.isFailure)
    }

    @Test
    fun `updateBlogPost success with partial fields returns updated post`() = runBlocking {
        val updatedPost = AdminBlogPost(
            id = "1",
            title = "Updated Title",
            slug = "original-slug",
            published = true,
            authorId = "user1",
            authorName = "John Doe",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-15",
            publishedAt = "2024-01-15",
            excerpt = "Original excerpt",
            coverImage = "https://example.com/original-cover.jpg",
            content = "Original content"
        )
        val response = ApiResponse(success = true, data = updatedPost)
        val request = UpdateBlogPostRequest(title = "Updated Title", published = true)
        coEvery { api.updateBlogPost("1", request) } returns response

        val result = repository.updateBlogPost("1", request)

        assertTrue(result.isSuccess)
        assertEquals("Updated Title", result.getOrNull()!!.title)
        assertEquals("2024-01-15", result.getOrNull()!!.updatedAt)
    }

    @Test
    fun `updateBlogPost success with all fields updated`() = runBlocking {
        val updatedPost = AdminBlogPost(
            id = "1",
            title = "Fully Updated Post",
            slug = "fully-updated-slug",
            published = false,
            authorId = "user1",
            authorName = "John Doe",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-20",
            publishedAt = null,
            excerpt = "New excerpt",
            coverImage = "https://example.com/new-cover.jpg",
            content = "<p>New content</p>"
        )
        val response = ApiResponse(success = true, data = updatedPost)
        val request = UpdateBlogPostRequest(
            title = "Fully Updated Post",
            slug = "fully-updated-slug",
            content = "<p>New content</p>",
            excerpt = "New excerpt",
            coverImage = "https://example.com/new-cover.jpg",
            published = false
        )
        coEvery { api.updateBlogPost("1", request) } returns response

        val result = repository.updateBlogPost("1", request)

        assertTrue(result.isSuccess)
        assertEquals("Fully Updated Post", result.getOrNull()!!.title)
        assertFalse(result.getOrNull()!!.published)
    }

    @Test
    fun `updateBlogPost API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogPost>(
            success = false,
            data = null,
            message = "Failed to update blog post"
        )
        val request = UpdateBlogPostRequest(title = "Updated Title")
        coEvery { api.updateBlogPost("1", request) } returns response

        val result = repository.updateBlogPost("1", request)

        assertTrue(result.isFailure)
        assertEquals("Failed to update blog post", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateBlogPost API throws exception returns Result failure`() = runBlocking {
        val request = UpdateBlogPostRequest(title = "Updated Title")
        coEvery { api.updateBlogPost("1", request) } throws RuntimeException("Network Error")

        val result = repository.updateBlogPost("1", request)

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateBlogPost API returns null data returns Result failure`() = runBlocking {
        val response = ApiResponse<AdminBlogPost>(
            success = true,
            data = null
        )
        val request = UpdateBlogPostRequest(title = "Updated Title")
        coEvery { api.updateBlogPost("1", request) } returns response

        val result = repository.updateBlogPost("1", request)

        assertTrue(result.isFailure)
    }

    @Test
    fun `deleteBlogPost success returns Unit`() = runBlocking {
        val response = ApiResponse<Any>(
            success = true,
            data = Unit
        )
        coEvery { api.deleteBlogPost("1") } returns response

        val result = repository.deleteBlogPost("1")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `deleteBlogPost passes correct id to API`() = runBlocking {
        val response = ApiResponse<Any>(success = true, data = Unit)
        coEvery { api.deleteBlogPost("delete-me") } returns response

        repository.deleteBlogPost("delete-me")

        coVerify { api.deleteBlogPost("delete-me") }
    }

    @Test
    fun `deleteBlogPost API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<Any>(
            success = false,
            data = null,
            message = "Failed to delete blog post"
        )
        coEvery { api.deleteBlogPost("1") } returns response

        val result = repository.deleteBlogPost("1")

        assertTrue(result.isFailure)
        assertEquals("Failed to delete blog post", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteBlogPost API throws exception returns Result failure`() = runBlocking {
        coEvery { api.deleteBlogPost("1") } throws RuntimeException("Network Error")

        val result = repository.deleteBlogPost("1")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }
}
