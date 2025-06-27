@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.khizana_user.presentation.setting.viewmodel

import com.example.khizana_user.domain.usecase.sharedperference.GetCurrencyUseCase
import com.example.khizana_user.domain.usecase.home.GetExchangeRateUseCase
import com.example.khizana_user.domain.usecase.auth.LogoutUseCase
import com.example.khizana_user.domain.usecase.sharedperference.ClearCustomerUseCase
import com.example.khizana_user.domain.usecase.sharedperference.SaveCurrencyUseCase
import com.example.khizana_user.utils.CurrencyHelper
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getExchangeRateUseCase: GetExchangeRateUseCase
    private lateinit var clearCustomerUseCase: ClearCustomerUseCase
    private lateinit var saveCurrencyUseCase: SaveCurrencyUseCase
    private lateinit var getCurrencyUseCase: GetCurrencyUseCase
    private lateinit var logoutUseCase: LogoutUseCase

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getExchangeRateUseCase = mockk()
        clearCustomerUseCase = mockk(relaxed = true)
        saveCurrencyUseCase = mockk(relaxed = true)
        getCurrencyUseCase = mockk()
        logoutUseCase = mockk()

        coEvery { getCurrencyUseCase() } returns flowOf("USD")

        viewModel = SettingViewModel(
            getExchangeRateUseCase,
            clearCustomerUseCase,
            saveCurrencyUseCase,
            getCurrencyUseCase,
            logoutUseCase,
            saveAddressUseCase = TODO(),
            getAddressUseCase = TODO()
        )

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state uses saved currency`() = runTest {
        assertEquals("USD", viewModel.state.value)
        assertEquals("USD", CurrencyHelper.currencyUnit)
    }

    @Test
    fun `updateCurrency changes the current state`() = runTest {
        viewModel.updateCurrency("SAR")
        assertEquals("SAR", viewModel.state.value)
    }

    @Test
    fun `saveCurrency invokes use case`() = runTest {
        viewModel.saveCurrency("EUR")
        advanceUntilIdle()

        coVerify { saveCurrencyUseCase("EUR") }
    }

    @Test
    fun `logout invokes clearCustomerUseCase on success`() = runTest {
        coEvery { logoutUseCase() } returns Result.success(Unit)

        viewModel.logout()
        advanceUntilIdle()

        coVerify { clearCustomerUseCase() }
    }

    @Test
    fun `logout does not call clearCustomerUseCase on failure`() = runTest {
        coEvery { logoutUseCase() } returns Result.failure(Exception("Logout error"))

        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 0) { clearCustomerUseCase() }
    }


}