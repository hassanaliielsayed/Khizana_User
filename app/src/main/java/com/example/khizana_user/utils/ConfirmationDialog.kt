package com.example.khizana_user.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.khizana_user.R

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    confirmButtonColor: Color = Color.Red,
    dismissButtonColor: Color = Color.Blue
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                ) {
                    Text(
                        confirmText,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                ) {
                    Text(
                        dismissText,
                        color = Color.Black
                    )
                }
            }
        )
    }
}