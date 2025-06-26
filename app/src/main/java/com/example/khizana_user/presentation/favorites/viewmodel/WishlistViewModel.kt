package com.example.khizana_user.presentation.favorites.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.usecase.favourite.AddToFavoritesUseCase
import com.example.khizana_user.domain.usecase.favourite.DeleteFavoritesUseCase
import com.example.khizana_user.domain.usecase.favourite.GetFavoritesUseCase
import com.example.khizana_user.domain.usecase.favourite.RemoveFromFavoritesUseCase
import com.example.khizana_user.utils.ConnectionLiveData
import com.example.khizana_user.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val favoritesState = _favoritesState.asStateFlow()

    private val _toggleFavoriteState = MutableStateFlow<Result<Boolean>>(Result.Success(false))
    val toggleFavoriteState = _toggleFavoriteState.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    fun loadFavorites(customerId: Long) {
        viewModelScope.launch {
            _loading.value = true
            getFavoritesUseCase(customerId)
                .onSuccess {
                    _favoritesState.value = it
                }
                .onFailure {
                    Log.e("WishlistViewModel", "Failed to load favorites: ${it.message}")
                    _favoritesState.value = null
                }
            _loading.value = false
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

    suspend fun isFavorite(customerId: Long, variantId: Long): Boolean {
        return try {
            val result = getFavoritesUseCase(customerId)
            result.getOrNull()?.items?.any { it?.variantId == variantId } == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addToFavorites(customerId: Long, variantId: Long): Result<Unit> {
        return try {
            addToFavoritesUseCase(customerId, variantId)
            loadFavorites(customerId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    fun removeFromFavorites(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            try {
                val currentItems = _favoritesState.value?.items?.filterNotNull().orEmpty()
                val isLastItem = currentItems.size == 1 && currentItems[0].variantId == variantId
                if (isLastItem) {
                    clearFavorites(customerId)
                    return@launch
                }
                val result = removeFromFavoritesUseCase(customerId, variantId)
                if (result.isSuccess) {
                    loadFavorites(customerId)
                } else {
                    Log.e("WishlistViewModel", "Remove failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Exception during remove: ${e.message}")
            }
        }
    }

}
