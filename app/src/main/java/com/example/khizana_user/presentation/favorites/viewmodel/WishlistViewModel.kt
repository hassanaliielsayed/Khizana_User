package com.example.khizana_user.presentation.favorites.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.usecase.favouriteusecases.AddToFavoritesUseCase
import com.example.khizana_user.domain.usecase.favouriteusecases.DeleteFavoritesUseCase
import com.example.khizana_user.domain.usecase.favouriteusecases.GetFavoritesUseCase
import com.example.khizana_user.domain.usecase.favouriteusecases.RemoveFromFavoritesUseCase
import com.example.khizana_user.utils.ConnectionLiveData
import com.example.khizana_user.utils.Result
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
    private val deleteFavoritesUseCase: DeleteFavoritesUseCase,
    private val connectionLiveData: ConnectionLiveData
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<FavoriteList?>(null)
    val favoritesState: StateFlow<FavoriteList?> = _favoritesState

    private val _toggleFavoriteState = MutableStateFlow<Result<Boolean>>(Result.Success(false))
    val toggleFavoriteState: StateFlow<Result<Boolean>> = _toggleFavoriteState

    private val _networkState = MutableStateFlow(true)
    val networkState: StateFlow<Boolean> = _networkState

    init {
        observeNetworkState()
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            connectionLiveData.asFlow().collect { isConnected ->
                _networkState.value = isConnected
            }
        }
    }

    /**
     * Initialize favorite status manually when screen loads.
     */
    fun setInitialFavoriteStatus(isFavorite: Boolean) {
        _toggleFavoriteState.value = Result.Success(isFavorite)
    }

    /**
     * Load the current list of favorites for a customer.
     */
    fun loadFavorites(customerId: Long) {
        viewModelScope.launch {
            getFavoritesUseCase(customerId)
                .onSuccess { _favoritesState.value = it }
                .onFailure {
                    Log.e("WishlistViewModel", "Failed to load favorites: ${it.message}")
                    _favoritesState.value = null
                }
        }
    }

    /**
     * Toggle between adding and removing favorite based on current state.
     */
    fun toggleFavorite(customerId: Long, variantId: Long, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            _toggleFavoriteState.value = Result.Loading

            val result = if (isCurrentlyFavorite) {
                removeFromFavoritesUseCase(customerId, variantId)
            } else {
                addToFavoritesUseCase(customerId, variantId)
            }

            loadFavorites(customerId)

            _toggleFavoriteState.value = if (result.isSuccess) {
                Result.Success(!isCurrentlyFavorite)
            } else {
                Result.Error(result.exceptionOrNull()?.message ?: "Favorite toggle failed")
            }
        }
    }

    /**
     * Remove all favorites for the current user.
     */
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
