package com.example.khizana_user.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
                    colors =  ButtonDefaults.buttonColors(containerColor = confirmButtonColor)
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(dismissText)
                }
            }
        )
    }
}