package com.example.khizana_user.domain.model

data class Customer(
    val id: Long,
    val name: String,
    val email: String,
    val isVerified: Boolean,
    val currency: String
)