package com.example.foodshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodshot.ui.theme.FoodShotTheme

class HistoryActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HistoryScreen()
        }
    }
}

@Composable
fun HistoryScreen() {
    FoodShotTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Image(
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize(),
                bitmap = ImageBitmap.imageResource(R.drawable.background_pic),
                contentDescription = "background_pic"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryActivityPreview() {
    HistoryScreen()
}