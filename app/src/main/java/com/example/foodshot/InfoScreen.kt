package com.example.foodshot

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun InfoScreen(
    bitmap: MutableState<Bitmap?>,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        bitmap.value?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = "food_photo"
            )
        }
    }
}