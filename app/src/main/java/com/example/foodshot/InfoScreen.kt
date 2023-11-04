package com.example.foodshot

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun InfoScreen(
    selectImages: List<Uri>,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = rememberImagePainter(selectImages[0]),
            contentDescription = "Image",
            alignment = Alignment.Center
        )

    }
}