package com.example.khizana_user.data.repository.auth


import com.example.khizana_user.data.repository.auth.AuthDataSource
import com.example.khizana_user.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryImpTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var authDataSource: AuthDataSource
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authDataSource = mockk()
        repository = AuthRepositoryImp(authDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login delegates to authDataSource`() = runTest {
        coEvery { authDataSource.login("test@example.com", "pass") } returns Result.success(Unit)

        val result = repository.login("test@example.com", "pass")

        assertTrue(result.isSuccess)
        coVerify { authDataSource.login("test@example.com", "pass") }
    }

    @Test
    fun `register delegates to authDataSource`() = runTest {
        coEvery { authDataSource.register("new@example.com", "1234") } returns Result.success(Unit)

        val result = repository.register("new@example.com", "1234")

        assertTrue(result.isSuccess)
        coVerify { authDataSource.register("new@example.com", "1234") }
    }

    @Test
    fun `loginWithGoogle delegates to authDataSource`() = runTest {
        coEvery { authDataSource.loginWithGoogle("token123") } returns Result.success(Unit)

        val result = repository.loginWithGoogle("token123")

        assertTrue(result.isSuccess)
        coVerify { authDataSource.loginWithGoogle("token123") }
    }

    @Test
    fun `logout delegates to authDataSource`() = runTest {
        coEvery { authDataSource.logout() } returns Result.success(Unit)

        val result = repository.logout()

        assertTrue(result.isSuccess)
        coVerify { authDataSource.logout() }
    }

    @Test
    fun `loginAsGuest delegates to authDataSource`() = runTest {
        coEvery { authDataSource.loginAsGuest() } returns Result.success(Unit)

        repository.loginAsGuest()

        coVerify { authDataSource.loginAsGuest() }
    }

    @Test
    fun `sendPasswordResetEmail delegates to authDataSource`() = runTest {
        coEvery { authDataSource.sendPasswordResetEmail("reset@example.com") } returns Result.success(Unit)

        val result = repository.sendPasswordResetEmail("reset@example.com")

        assertTrue(result.isSuccess)
        coVerify { authDataSource.sendPasswordResetEmail("reset@example.com") }
    }

    @Test
    fun `sendEmailVerification delegates to authDataSource`() = runTest {
        coEvery { authDataSource.sendEmailVerification() } returns Result.success(Unit)

        repository.sendEmailVerification()

        coVerify { authDataSource.sendEmailVerification() }
    }

    @Test
    fun `isEmailVerified delegates to authDataSource`() = runTest {
        coEvery { authDataSource.isEmailVerified() } returns true

        val result = repository.isEmailVerified()

        assertTrue(result)
        coVerify { authDataSource.isEmailVerified() }
    }

    @Test
    fun `getCurrentUserEmail delegates to authDataSource`() {
        every { authDataSource.getCurrentUserEmail() } returns "me@example.com"

        val result = repository.getCurrentUserEmail()

        assertEquals("me@example.com", result)
    }

    @Test
    fun `getCurrentUserName delegates to authDataSource`() {
        every { authDataSource.getCurrentUserName() } returns "John Doe"

        val result = repository.getCurrentUserName()

        assertEquals("John Doe", result)
    }
}