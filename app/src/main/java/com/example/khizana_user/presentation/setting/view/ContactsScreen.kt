package com.example.khizana_user.presentation.setting.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AppLogo()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.light_blue)
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.light_background))
                .padding(24.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = stringResource(R.string.contact_icon),
                    tint = colorResource(id = R.color.primary_color),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.contact_us),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary_color)
                )
            }

            ContactCard(
                email = stringResource(R.string.ayaahmed111_gmail_com),
                phone = stringResource(R.string.aya_phone)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ContactCard(
                email = stringResource(R.string.esraamohammed_gmail_com),
                phone = stringResource(R.string.esraa_phone)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ContactCard(
                email = stringResource(R.string.yousefghoneim2_gmail_com),
                phone = stringResource(R.string.yousef_phone)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ContactCard(
                email = stringResource(R.string.hassanaliielsayed_gmail_com),
                phone = stringResource(R.string.hassan_phone)
            )
        }
    }
}

@Composable
private fun ContactCard(email: String, phone: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ContactInfoRow(
                label = stringResource(R.string.email),
                value = email,
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactInfoRow(
                label = stringResource(R.string.phone),
                value = phone,
                icon = Icons.Default.Phone
            )
        }
    }
}

@Composable
private fun ContactInfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorResource(id = R.color.primary_color),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}