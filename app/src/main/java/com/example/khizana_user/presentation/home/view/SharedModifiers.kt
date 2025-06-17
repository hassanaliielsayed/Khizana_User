package com.example.khizana_user.presentation.home.view

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import com.example.khizana_user.R


@SuppressLint("ModifierFactoryExtensionFunction")
object SharedModifiers {
    fun roundedImageModifier(size: Dp) = Modifier
        .size(size)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)

    fun circleImageModifier(size: Dp) = Modifier
        .size(size)
        .clip(CircleShape)
        .background(Color.White)
        .border(1.dp, Color.Gray, CircleShape)
        .padding(4.dp)

    @Composable
    fun cardModifier(height: Dp) = Modifier
        .height(height)
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, colorResource(id = R.color.black))


    fun couponImageModifier() = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(16.dp))
        .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(16.dp))
}