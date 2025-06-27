package com.example.khizana_user.data.repository


import com.example.khizana_user.data.dto.CurrencyData
import com.example.khizana_user.data.dto.CurrencyResponseDto
import com.example.khizana_user.domain.model.CurrencyRate
import com.example.khizana_user.domain.repository.SettingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SettingRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: SettingRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        repository = SettingRepositoryImpl(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCurrencyRate returns mapped CurrencyRate`() = runTest {
        val base = "USD"
        val target = "EGP"

        val dto = CurrencyData(code = "EGP", value = 30.5)
        val mockResponse = CurrencyResponseDto(data = mapOf("EGP" to dto))

        coEvery { remoteDataSource.getCurrencyRate(base, target) } returns mockResponse

        val result = repository.getCurrencyRate(base, target)

        assertEquals(CurrencyRate(code = "EGP", rate = 30.5), result)
    }


    @Test
    fun `getCurrencyRate throws exception when currency is missing`() = runTest {
        val base = "USD"
        val target = "SAR"

        coEvery { remoteDataSource.getCurrencyRate(base, target) } returns CurrencyResponseDto(
            data = mapOf("EGP" to CurrencyData("EGP", 30.5))
        )

        val exception = kotlin.runCatching {
            repository.getCurrencyRate(base, target)
        }.exceptionOrNull()

        assertEquals("Currency not found", exception?.message)
    }

}