package com.example.khizana_user.utils

import com.google.firebase.auth.FirebaseAuth

fun isGuestUser(): Boolean {
    return FirebaseAuth.getInstance().currentUser?.isAnonymous == true
}