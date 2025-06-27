package com.example.khizana_user.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, UnAvailable, Loosing, Lost
    }
}
