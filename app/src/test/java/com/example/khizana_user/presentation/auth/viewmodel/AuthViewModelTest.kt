package com.example.khizana_user.presentation.auth.viewmodel

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.usecase.auth.*
import com.example.khizana_user.domain.usecase.sharedperference.GetCustomerUseCase
import com.example.khizana_user.domain.usecase.sharedperference.SaveCustomerUseCase
import com.example.khizana_user.utils.AuthState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var registerShopifyCustomerUseCase: RegisterShopifyCustomerUseCase
    private lateinit var getShopifyCustomerByEmailUseCase: GetShopifyCustomerByEmailUseCase
    private lateinit var saveCustomerUseCase: SaveCustomerUseCase
    private lateinit var loginWithGoogleUseCase: LoginWithGoogleUseCase
    private lateinit var loginAsGuestUseCase: LoginAsGuestUseCase
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private lateinit var sendEmailVerificationUseCase: SendEmailVerificationUseCase
    private lateinit var checkEmailVerifiedUseCase: CheckEmailVerifiedUseCase
    private lateinit var getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase
    private lateinit var getCurrentUserNameUseCase: GetCurrentUserNameUseCase
    private lateinit var getCustomerUseCase: GetCustomerUseCase

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        loginUseCase = mockk()
        registerUseCase = mockk()
        registerShopifyCustomerUseCase = mockk()
        getShopifyCustomerByEmailUseCase = mockk()
        saveCustomerUseCase = mockk(relaxed = true)
        loginWithGoogleUseCase = mockk()
        loginAsGuestUseCase = mockk()
        resetPasswordUseCase = mockk()
        sendEmailVerificationUseCase = mockk()
        checkEmailVerifiedUseCase = mockk()
        getCurrentUserEmailUseCase = mockk()
        getCurrentUserNameUseCase = mockk()
        getCustomerUseCase = mockk()

        every { getCustomerUseCase() } returns flowOf(null)
        coEvery { getCurrentUserEmailUseCase() } returns "init@example.com"
        coEvery { checkEmailVerifiedUseCase() } returns false
        coEvery { getShopifyCustomerByEmailUseCase("init@example.com") } returns Result.success(null)

        viewModel = AuthViewModel(
            loginUseCase,
            registerUseCase,
            registerShopifyCustomerUseCase,
            getShopifyCustomerByEmailUseCase,
            saveCustomerUseCase,
            loginWithGoogleUseCase,
            loginAsGuestUseCase,
            resetPasswordUseCase,
            sendEmailVerificationUseCase,
            checkEmailVerifiedUseCase,
            getCurrentUserEmailUseCase,
            getCurrentUserNameUseCase,
            getCustomerUseCase
        )
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login sets Success when login succeeds`() = runTest {
        coEvery { loginUseCase("test@example.com", "pass") } returns Result.success(Unit)
        coEvery { getShopifyCustomerByEmailUseCase("test@example.com") } returns Result.success(
            Customer(1, "Test", "test@example.com", true, "USD")
        )

        viewModel.login("test@example.com", "pass")
        advanceUntilIdle()

        assertEquals(AuthState.Success, viewModel.authState.value)
    }

    @Test
    fun `login sets Error when login fails`() = runTest {
        coEvery { loginUseCase("bad@example.com", "pass") } returns Result.failure(Exception("Bad login"))

        viewModel.login("bad@example.com", "pass")
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Error)
    }

    @Test
    fun `register sets VerificationEmailSent when registration succeeds`() = runTest {
        coEvery { registerUseCase(any(), any()) } returns Result.success(Unit)
        coEvery { sendEmailVerificationUseCase() } returns Result.success(Unit)

        viewModel.register("email@test.com", "1234", "Test User")
        advanceUntilIdle()

        assertEquals(AuthState.VerificationEmailSent, viewModel.authState.value)
    }

    @Test
    fun `loginWithGoogle registers new customer if Shopify customer is null`() = runTest {
        coEvery { loginWithGoogleUseCase("token") } returns Result.success(Unit)
        coEvery { getCurrentUserEmailUseCase() } returns "test@example.com"
        coEvery { getCurrentUserNameUseCase() } returns "Test User"
        coEvery { getShopifyCustomerByEmailUseCase("test@example.com") } returns Result.success(null)
        coEvery { registerShopifyCustomerUseCase("Test User", "test@example.com") } returns Result.success(
            Customer(1L, "Test User", "test@example.com", true, "EGP")
        )

        viewModel.loginWithGoogle("token")
        advanceUntilIdle()

        assertEquals(AuthState.Success, viewModel.authState.value)
    }

    @Test
    fun `loginAsGuest saves guest user`() = runTest {
        coEvery { loginAsGuestUseCase() } returns Result.success(Unit)

        viewModel.loginAsGuest()
        advanceUntilIdle()

        assertEquals(AuthState.Success, viewModel.authState.value)
        coVerify {
            saveCustomerUseCase(
                Customer(-1L, "Guest", "guest@anonymous.com", false, "EGP")
            )
        }
    }

    @Test
    fun `resetPassword updates resetPasswordState with success`() = runTest {
        coEvery { resetPasswordUseCase("test@example.com") } returns Result.success(Unit)

        viewModel.resetPassword("test@example.com")
        advanceUntilIdle()

        assertTrue(viewModel.resetPasswordState.value?.isSuccess == true)
    }

    @Test
    fun `checkEmailVerificationStatus sets _isEmailVerified to true`() = runTest {
        coEvery { checkEmailVerifiedUseCase() } returns true
        coEvery { getCurrentUserEmailUseCase() } returns "test@example.com"

        var verifiedCalled = false

        viewModel.checkEmailVerificationStatus {
            verifiedCalled = true
            assertEquals("test@example.com", it)
        }

        advanceUntilIdle()
        assertTrue(viewModel.isEmailVerified.value)
        assertTrue(verifiedCalled)
    }
}

