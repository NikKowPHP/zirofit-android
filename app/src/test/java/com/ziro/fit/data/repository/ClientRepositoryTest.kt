package com.ziro.fit.data.repository

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ClientRepositoryTest {

    private val api: ZiroApi = mockk()
    private val repository = ClientRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @Test
    fun `getClients returns list of clients on success`() = runBlocking {
        val clients = listOf(
            Client(id = "1", name = "John", email = "john@test.com", phone = "123", status = "active"),
            Client(id = "2", name = "Jane", email = "jane@test.com", phone = "456", status = "active")
        )
        val response = ApiResponse(data = GetClientsResponse(clients = clients), success = true)
        coEvery { api.getClients() } returns response

        val result = repository.getClients()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("John", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun `getClients returns failure when API throws exception`() = runBlocking {
        coEvery { api.getClients() } throws RuntimeException("Network Error")

        val result = repository.getClients()

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `searchClients filters by name case insensitive`() = runBlocking {
        val clients = listOf(
            Client(id = "1", name = "John Doe", email = "john@test.com", phone = null, status = "active"),
            Client(id = "2", name = "Jane Smith", email = "jane@test.com", phone = null, status = "active"),
            Client(id = "3", name = "Bob Johnson", email = "bob@test.com", phone = null, status = "active")
        )
        val response = ApiResponse(data = GetClientsResponse(clients = clients), success = true)
        coEvery { api.getClients() } returns response

        val result = repository.searchClients("john")

        assertTrue(result.isSuccess)
        val filtered = result.getOrNull()!!
        assertEquals(2, filtered.size)
        assertTrue(filtered.any { it.name == "John Doe" })
        assertTrue(filtered.any { it.name == "Bob Johnson" })
    }

    @Test
    fun `searchClients filters by email`() = runBlocking {
        val clients = listOf(
            Client(id = "1", name = "John", email = "john@work.com", phone = null, status = "active"),
            Client(id = "2", name = "Jane", email = "jane@test.com", phone = null, status = "active"),
            Client(id = "3", name = "Bob", email = "bob@personal.com", phone = null, status = "active")
        )
        val response = ApiResponse(data = GetClientsResponse(clients = clients), success = true)
        coEvery { api.getClients() } returns response

        val result = repository.searchClients("test.com")

        assertTrue(result.isSuccess)
        val filtered = result.getOrNull()!!
        assertEquals(1, filtered.size)
        assertEquals("Jane", filtered[0].name)
    }

    @Test
    fun `searchClients filters by phone`() = runBlocking {
        val clients = listOf(
            Client(id = "1", name = "John", email = "john@test.com", phone = "123-456-7890", status = "active"),
            Client(id = "2", name = "Jane", email = "jane@test.com", phone = "555-1234", status = "active"),
            Client(id = "3", name = "Bob", email = "bob@test.com", phone = "999-8888", status = "active")
        )
        val response = ApiResponse(data = GetClientsResponse(clients = clients), success = true)
        coEvery { api.getClients() } returns response

        val result = repository.searchClients("456")

        assertTrue(result.isSuccess)
        val filtered = result.getOrNull()!!
        assertEquals(1, filtered.size)
        assertEquals("John", filtered[0].name)
    }

    @Test
    fun `searchClients returns empty list when no matches`() = runBlocking {
        val clients = listOf(
            Client(id = "1", name = "John", email = "john@test.com", phone = null, status = "active"),
            Client(id = "2", name = "Jane", email = "jane@test.com", phone = null, status = "active")
        )
        val response = ApiResponse(data = GetClientsResponse(clients = clients), success = true)
        coEvery { api.getClients() } returns response

        val result = repository.searchClients("xyz123")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `searchClients returns failure when API throws exception`() = runBlocking {
        coEvery { api.getClients() } throws RuntimeException("Network Error")

        val result = repository.searchClients("test")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createClient returns new client on success`() = runBlocking {
        val newClient = Client(id = "1", name = "New Client", email = "new@test.com", phone = "123", status = "active")
        val response = ApiResponse(data = CreateClientResponse(client = newClient), success = true)
        coEvery { api.createClient(any()) } returns response

        val result = repository.createClient("New Client", "new@test.com", "123", "active")

        assertTrue(result.isSuccess)
        assertEquals("New Client", result.getOrNull()?.name)
        assertEquals("new@test.com", result.getOrNull()?.email)
    }

    @Test
    fun `createClient returns failure when API throws exception`() = runBlocking {
        coEvery { api.createClient(any()) } throws RuntimeException("Create Failed")

        val result = repository.createClient("New", "new@test.com", null, "active")

        assertTrue(result.isFailure)
        assertEquals("Create Failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateClient returns Unit on success`() = runBlocking {
        coEvery { api.updateClient(any(), any()) } returns ApiResponse(data = Unit, success = true)

        val result = repository.updateClient("1", "Updated", "updated@test.com", null, null)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { api.updateClient("1", any()) }
    }

    @Test
    fun `updateClient returns failure when API throws exception`() = runBlocking {
        coEvery { api.updateClient(any(), any()) } throws RuntimeException("Update Failed")

        val result = repository.updateClient("1", null, null, null, null)

        assertTrue(result.isFailure)
        assertEquals("Update Failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getClientProfile returns complete profile data`() = runBlocking {
        val client = Client(id = "1", name = "John", email = "john@test.com", phone = "123", status = "active")
        val measurements = listOf(Measurement(id = "m1", measurementDate = "2024-01-01", weightKg = 75.0, bodyFatPercentage = 15.0, notes = null, customMetrics = null))
        val assessments = listOf(AssessmentResult(id = "a1", assessmentId = "ass1", assessmentName = "Test", value = 100.0, date = "2024-01-01", notes = null, unit = "%"))
        val photos = listOf(TransformationPhoto(id = "p1", photoUrl = "http://test.com/photo.jpg", photoDate = "2024-01-01", caption = "Before"))
        val sessions = listOf(ClientSession(id = "s1", startTime = "2024-01-01T10:00:00Z", endTime = null, status = "scheduled", notes = null, name = null, plannedDate = null, workoutTemplateId = null))

        coEvery { api.getClientDetails("1") } returns ApiResponse(data = GetClientDetailsResponse(client = client), success = true)
        coEvery { api.getClientMeasurements("1") } returns ApiResponse(data = GetMeasurementsResponse(measurements = measurements), success = true)
        coEvery { api.getClientAssessments("1") } returns ApiResponse(data = GetClientAssessmentsResponse(results = assessments), success = true)
        coEvery { api.getClientPhotos("1") } returns ApiResponse(data = GetPhotosResponse(photos = photos), success = true)
        coEvery { api.getClientSessions("1") } returns ApiResponse(data = GetClientSessionsResponse(sessions = sessions), success = true)

        val result = repository.getClientProfile("1")

        assertTrue(result.isSuccess)
        val profile = result.getOrNull()!!
        assertEquals("John", profile.client.name)
        assertEquals(1, profile.measurements.size)
        assertEquals("m1", profile.measurements[0].id)
        assertEquals(1, profile.assessments.size)
        assertEquals("a1", profile.assessments[0].id)
        assertEquals(1, profile.photos.size)
        assertEquals("p1", profile.photos[0].id)
        assertEquals(1, profile.sessions.size)
        assertEquals("s1", profile.sessions[0].id)
    }

    @Test
    fun `getClientProfile returns failure when API throws exception`() = runBlocking {
        coEvery { api.getClientDetails("1") } throws RuntimeException("Profile Load Failed")

        val result = repository.getClientProfile("1")

        assertTrue(result.isFailure)
        assertEquals("Profile Load Failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getClientMeasurements returns list on success`() = runBlocking {
        val measurements = listOf(
            Measurement(id = "m1", measurementDate = "2024-01-01", weightKg = 75.0, bodyFatPercentage = 15.0, notes = null, customMetrics = null),
            Measurement(id = "m2", measurementDate = "2024-02-01", weightKg = 74.0, bodyFatPercentage = 14.0, notes = null, customMetrics = null)
        )
        val response = ApiResponse(data = GetMeasurementsResponse(measurements = measurements), success = true)
        coEvery { api.getClientMeasurements("1") } returns response

        val result = repository.getClientMeasurements("1")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `deleteClient returns Unit on success`() = runBlocking {
        coEvery { api.deleteClient("1") } returns ApiResponse(data = Unit, success = true)

        val result = repository.deleteClient("1")

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { api.deleteClient("1") }
    }

    @Test
    fun `deleteSession returns Unit on success`() = runBlocking {
        coEvery { api.deleteSession("1", "s1") } returns ApiResponse(data = Unit, success = true)

        val result = repository.deleteSession("1", "s1")

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { api.deleteSession("1", "s1") }
    }

    @Test
    fun `updateSession returns session on success`() = runBlocking {
        val session = ClientSession(id = "s1", startTime = "2024-01-01T10:00:00Z", endTime = null, status = "Completed", notes = "Done", name = null, plannedDate = null, workoutTemplateId = null)
        val response = ApiResponse(data = session, success = true)
        coEvery { api.updateSession(any(), any(), any()) } returns response

        val result = repository.updateSession("1", "s1", "Done", "Completed")

        assertTrue(result.isSuccess)
        assertEquals("s1", result.getOrNull()?.id)
        assertEquals("Completed", result.getOrNull()?.status)
    }

    @Test
    fun `generateAiProgram returns ProgramResponse on success`() = runBlocking {
        val programResponse = ProgramResponse(programId = "prog1", name = "AI Program", description = "Generated", weeks = emptyList())
        val response = ApiResponse(data = programResponse, success = true)
        coEvery { api.generateAiProgram(any()) } returns response

        val result = repository.generateAiProgram("1", "4 weeks", "Strength")

        assertTrue(result.isSuccess)
        assertEquals("prog1", result.getOrNull()?.programId)
        assertEquals("AI Program", result.getOrNull()?.name)
    }
}
