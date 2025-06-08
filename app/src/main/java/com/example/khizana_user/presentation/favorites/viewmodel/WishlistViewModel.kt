package com.example.khizana_user.presentation.favorites.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.usecase.favouriteusecases.AddToFavoritesUseCase
import com.example.khizana_user.domain.usecase.favouriteusecases.DeleteFavoritesUseCase
import com.example.khizana_user.domain.usecase.favouriteusecases.GetFavoritesUseCase
import com.example.khizana_user.domain.usecase.favouriteusecases.RemoveFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val deleteFavoritesUseCase: DeleteFavoritesUseCase
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<FavoriteList?>(null)
    val favoritesState: StateFlow<FavoriteList?> = _favoritesState

    fun loadFavorites(customerId: Long) {
        viewModelScope.launch {
            Log.d("WishlistViewModel", "Loading favorites for customerId: $customerId")
            getFavoritesUseCase(customerId)
                .onSuccess {
                    Log.d("WishlistViewModel", "Favorites loaded successfully: ${it.items.size} items")
                    _favoritesState.value = it
                }
                .onFailure {
                    Log.e("WishlistViewModel", "Failed to load favorites: ${it.message}")
                    _favoritesState.value = null
                }
        }
    }

    fun addToFavorites(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            Log.d("WishlistViewModel", "Adding to favorites - customerId: $customerId, variantId: $variantId")
            val result = addToFavoritesUseCase(customerId, variantId)
            if (result.isSuccess) {
                Log.d("WishlistViewModel", "Successfully added to favorites")
            } else {
                Log.e("WishlistViewModel", "Failed to add to favorites: ${result.exceptionOrNull()?.message}")
            }
            loadFavorites(customerId)
        }
    }

    fun removeFromFavorites(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            Log.d("WishlistViewModel", "Removing from favorites - customerId: $customerId, variantId: $variantId")
            val result = removeFromFavoritesUseCase(customerId, variantId)
            if (result.isSuccess) {
                Log.d("WishlistViewModel", "Successfully removed from favorites")
            } else {
                Log.e("WishlistViewModel", "Failed to remove from favorites: ${result.exceptionOrNull()?.message}")
            }
            loadFavorites(customerId)
        }
    }

    fun clearFavorites(customerId: Long) {
        viewModelScope.launch {
            Log.d("WishlistViewModel", "Clearing all favorites for customerId: $customerId")
            val result = deleteFavoritesUseCase(customerId)
            if (result.isSuccess) {
                Log.d("WishlistViewModel", "Successfully cleared favorites")
            } else {
                Log.e("WishlistViewModel", "Failed to clear favorites: ${result.exceptionOrNull()?.message}")
            }
            loadFavorites(customerId)
        }
    }
}
