package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GetAllBrandsUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: HomeRepository
    private lateinit var useCase: GetAllBrandsUseCase

    private val mockBrands = listOf(
        Brand(id = 1L, title = "Nike", imageUrl = "https://example.com/nike.png"),
        Brand(id = 2L, title = "Adidas", imageUrl = "https://example.com/adidas.png")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetAllBrandsUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns brand list from repository`() = runTest {
        coEvery { repository.getAllBrands() } returns mockBrands

        val result = useCase()

        assertEquals(2, result.size)
        assertEquals("Nike", result[0].title)
        assertEquals("Adidas", result[1].title)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        coEvery { repository.getAllBrands() } returns emptyList()

        val result = useCase()

        assertTrue(result.isEmpty())
    }
}
