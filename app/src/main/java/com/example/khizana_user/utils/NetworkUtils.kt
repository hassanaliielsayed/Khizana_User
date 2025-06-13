package com.example.khizana_user.utils

import android.content.Context
import androidx.lifecycle.LiveData

object NetworkUtils {
    fun observeNetworkConnectivity(context: Context): LiveData<Boolean> {
        return ConnectionLiveData(context)
    }
}