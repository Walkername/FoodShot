package com.example.foodshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodshot.ui.theme.FoodShotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Image(
                            modifier = Modifier.padding(start = 50.dp, end = 50.dp),
                            bitmap = ImageBitmap.imageResource(R.drawable.avocado_main_pic),
                            contentDescription = "Avocado"
                        )
                        Menu()
                    }
                }
            }
        }
    }
}

@Composable
fun Menu() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Button(
            modifier = Modifier.size(100.dp),
            colors = ButtonDefaults.buttonColors(contentColor = Color.White),
            onClick = { /*TODO: create HistoryActivity*/ }
        ) {
            Text(text = "History")
        }
        Button(
            modifier = Modifier.size(100.dp),
            colors = ButtonDefaults.buttonColors(contentColor = Color.White),
            onClick = { /*TODO: import camera API to take a photo*/ }
        ) {
            Text(text = "Camera")
        }
        Button(
            modifier = Modifier.size(100.dp),
            colors = ButtonDefaults.buttonColors(contentColor = Color.White),
            onClick = { /*TODO: import gallery from phone*/ }
        ) {
            Text(text = "Gallery")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    FoodShotTheme {
        Column(
            // TODO: set background color
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                modifier = Modifier.padding(start = 50.dp, end = 50.dp),
                bitmap = ImageBitmap.imageResource(R.drawable.avocado_main_pic),
                contentDescription = "Avocado"
            )
            Menu()
        }
    }
}