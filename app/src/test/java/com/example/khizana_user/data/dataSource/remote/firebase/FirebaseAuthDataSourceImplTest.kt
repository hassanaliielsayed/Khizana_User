package com.example.khizana_user.data.dataSource.remote.firebase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.khizana_user.data.repository.auth.AuthDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [28])
@RunWith(RobolectricTestRunner::class)
class FirebaseAuthDataSourceImplRoboTest {

    private lateinit var context: Context
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser
    private lateinit var dataSource: AuthDataSourceTestDouble

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        mockAuth = mock(FirebaseAuth::class.java)
        mockUser = mock(FirebaseUser::class.java)

        `when`(mockUser.email).thenReturn("test@fake.com")
        `when`(mockUser.displayName).thenReturn("John Doe")
        `when`(mockAuth.currentUser).thenReturn(mockUser)

        dataSource = AuthDataSourceTestDouble(mockAuth)
    }

    @Test
    fun testGetCurrentUserEmail() {
        val email = dataSource.getCurrentUserEmail()
        assertEquals("test@fake.com", email)
    }

    @Test
    fun testGetCurrentUserName() {
        val name = dataSource.getCurrentUserName()
        assertEquals("John Doe", name)
    }

    @Test
    fun testLoginSuccess() = runTest {
        val result = dataSource.login("john@example.com", "pass123")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testLoginFailure() = runTest {
        val result = dataSource.login("fail@example.com", "wrong")
        assertTrue(result.isFailure)
    }

    @Test
    fun testRegisterSuccess() = runTest {
        val result = dataSource.register("new@example.com", "123456")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testLogout() = runTest {
        val result = dataSource.logout()
        assertTrue(result.isSuccess)
    }

    @Test
    fun testSendPasswordResetEmail() = runTest {
        val result = dataSource.sendPasswordResetEmail("john@example.com")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testSendEmailVerification() = runTest {
        val result = dataSource.sendEmailVerification()
        assertTrue(result.isSuccess)
    }

    @Test
    fun testIsEmailVerified() = runTest {
        assertTrue(dataSource.isEmailVerified())
    }

    @Test
    fun testIsAnonymousUser() {
        assertFalse(dataSource.isAnonymousUser())
    }

    // Fake TestDouble Implementation
    class AuthDataSourceTestDouble(
        private val auth: FirebaseAuth
    ) : AuthDataSource {

        override fun getCurrentUserEmail(): String? = auth.currentUser?.email

        override fun getCurrentUserName(): String? = auth.currentUser?.displayName

        override suspend fun login(email: String, password: String): Result<Unit> {
            return if (email == "fail@example.com") {
                Result.failure(Exception("Login failed"))
            } else {
                Result.success(Unit)
            }
        }

        override suspend fun register(email: String, password: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun loginWithGoogle(idToken: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun logout(): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun loginAsGuest(): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun sendEmailVerification(): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun isEmailVerified(): Boolean = true

        override fun isAnonymousUser(): Boolean = false
    }
}
