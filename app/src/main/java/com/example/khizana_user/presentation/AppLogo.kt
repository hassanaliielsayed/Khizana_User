package com.example.khizana_user.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khizana_user.R
import com.example.khizana_user.utils.customFontFamily

@Composable
fun AppLogo() {

    Row(
    verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo2),
            contentDescription = stringResource(R.string.project_name),
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.project_name),
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colorResource(R.color.black)
        )
    }
}
