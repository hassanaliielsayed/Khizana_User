package com.example.khizana_user.data.repository.fav


import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.WishlistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WishlistRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remoteDataSource: WishlistRemoteDataSource
    private lateinit var repository: WishlistRepository

    private val customerId = 1L
    private val variantId = 100L

    private val favoriteList = FavoriteList(
        draftOrderId = 1234L,
        customerId = customerId,
        items = listOf(
            FavoriteItem(variantId, "Test Item", 1, 100.0, "url")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        repository = WishlistRepositoryImpl(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addToFavorites delegates to remoteDataSource and returns success`() = runTest {
        coEvery { remoteDataSource.addToFavorites(customerId, variantId) } returns Result.success(Unit)

        val result = repository.addToFavorites(customerId, variantId)

        assertTrue(result.isSuccess)
        coVerify { remoteDataSource.addToFavorites(customerId, variantId) }
    }

    @Test
    fun `removeFromFavorites delegates to remoteDataSource and returns success`() = runTest {
        coEvery { remoteDataSource.removeFromFavorites(customerId, variantId) } returns Result.success(Unit)

        val result = repository.removeFromFavorites(customerId, variantId)

        assertTrue(result.isSuccess)
        coVerify { remoteDataSource.removeFromFavorites(customerId, variantId) }
    }

    @Test
    fun `getFavorites returns result from remoteDataSource`() = runTest {
        coEvery { remoteDataSource.getFavorites(customerId) } returns Result.success(favoriteList)

        val result = repository.getFavorites(customerId)

        assertTrue(result.isSuccess)
        assertEquals(favoriteList, result.getOrNull())
        coVerify { remoteDataSource.getFavorites(customerId) }
    }

    @Test
    fun `deleteFavoritesDraft calls remoteDataSource and returns success`() = runTest {
        coEvery { remoteDataSource.deleteFavoritesDraft(customerId) } returns Result.success(Unit)

        val result = repository.deleteFavoritesDraft(customerId)

        assertTrue(result.isSuccess)
        coVerify { remoteDataSource.deleteFavoritesDraft(customerId) }
    }
}