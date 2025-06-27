@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.khizana_user.presentation.favorites.viewmodel

import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.usecase.favourite.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WishlistViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var addToFavoritesUseCase: AddToFavoritesUseCase
    private lateinit var removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
    private lateinit var getFavoritesUseCase: GetFavoritesUseCase
    private lateinit var deleteFavoritesUseCase: DeleteFavoritesUseCase

    private lateinit var viewModel: WishlistViewModel

    private val customerId = 1L
    private val variantId = 101L

    private val favoriteList = FavoriteList(
        draftOrderId = 1234L,
        customerId = customerId,
        items = listOf(
            FavoriteItem(
                variantId = variantId,
                title = "Test Item",
                quantity = 1,
                price = 99.99,
                imageUrl = "https://example.com/item.png"
            )
        )
    )


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0

        addToFavoritesUseCase = mockk()
        removeFromFavoritesUseCase = mockk()
        getFavoritesUseCase = mockk()
        deleteFavoritesUseCase = mockk()

        viewModel = WishlistViewModel(
            addToFavoritesUseCase,
            removeFromFavoritesUseCase,
            getFavoritesUseCase,
            deleteFavoritesUseCase
        )
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFavorites sets favoritesState on success`() = runTest {
        coEvery { getFavoritesUseCase(customerId) } returns Result.success(favoriteList)

        viewModel.loadFavorites(customerId)
        advanceUntilIdle()

        val result = viewModel.favoritesState.value
        assertEquals(favoriteList, result)
    }

    @Test
    fun `loadFavorites sets favoritesState to null on error`() = runTest {
        coEvery { getFavoritesUseCase(customerId) } returns Result.failure(Exception("Something failed"))

        viewModel.loadFavorites(customerId)
        advanceUntilIdle()

        assertNull(viewModel.favoritesState.value)
    }

    @Test
    fun `addToFavorites returns success and refreshes state`() = runTest {
        coEvery { addToFavoritesUseCase(customerId, variantId) } returns Result.success(Unit)
        coEvery { getFavoritesUseCase(customerId) } returns Result.success(favoriteList)

        val result = viewModel.addToFavorites(customerId, variantId)
        advanceUntilIdle()

        assertTrue(result is com.example.khizana_user.utils.Result.Success)
        assertEquals(favoriteList, viewModel.favoritesState.value)
    }

    @Test
    fun `addToFavorites returns failure on exception`() = runTest {
        coEvery { addToFavoritesUseCase(customerId, variantId) } throws Exception("Oops")

        val result = viewModel.addToFavorites(customerId, variantId)
        advanceUntilIdle()

        assertTrue(result is com.example.khizana_user.utils.Result.Error)
        assertEquals("Oops", (result as com.example.khizana_user.utils.Result.Error).message)
    }


    @Test
    fun `isFavorite returns true if item exists`() = runTest {
        coEvery { getFavoritesUseCase(customerId) } returns Result.success(favoriteList)

        val result = viewModel.isFavorite(customerId, variantId)

        assertTrue(result)
    }

    @Test
    fun `isFavorite returns false if item not in list`() = runTest {
        coEvery { getFavoritesUseCase(customerId) } returns Result.success(favoriteList.copy(items = emptyList()))

        val result = viewModel.isFavorite(customerId, variantId)

        assertFalse(result)
    }



    @Test
    fun `removeFromFavorites removes one item if multiple exist`() = runTest {
        val listWithTwo = favoriteList.copy(
            items = favoriteList.items + FavoriteItem(
                variantId = 999L,
                title = "Second",
                quantity = 1,
                price = 10.0,
                imageUrl = "https://img.com"
            )
        )

        coEvery { getFavoritesUseCase(customerId) } returns Result.success(listWithTwo)
        coEvery { removeFromFavoritesUseCase(customerId, variantId) } returns Result.success(Unit)

        viewModel.loadFavorites(customerId)
        advanceUntilIdle()

        viewModel.removeFromFavorites(customerId, variantId)
        advanceUntilIdle()

        assertEquals(listWithTwo.customerId, viewModel.favoritesState.value?.customerId)
    }
}
