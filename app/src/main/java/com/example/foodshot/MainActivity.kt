package com.example.foodshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshot.ui.theme.FoodShotTheme
import com.example.foodshot.ui.theme.Titan

const val ICON_COLOR = 0xfffef1ce
const val CIRCLE_BUTTON_COLOR = 0x14f8f4e8
const val APP_NAME_COLOR = 0xfff8f4e8

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        setContent {
            FoodShotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Image(
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxSize(),
        bitmap = ImageBitmap.imageResource(R.drawable.background_pic),
        contentDescription = "background_pic"
    )
    Column (
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .weight(3.4f)
        ) {
            Text(
                text = "FoodShot",
                color = Color(APP_NAME_COLOR),
                fontSize = 36.sp,
                fontFamily = Titan,
                textAlign = TextAlign.Center
            )
        }
        Menu(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 30.dp)
        )
    }
}

@Composable
fun Menu(modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(CIRCLE_BUTTON_COLOR))
        ) {
            IconButton(
                modifier = Modifier.size(300.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(ICON_COLOR)),
                onClick = { /*TODO: create navigation to HistoryActivity*/ }
            ) {
                Icon(
                    modifier = Modifier.size(53.dp),
                    painter = painterResource(R.drawable.history),
                    contentDescription = "history"
                )
            }
        }
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(CIRCLE_BUTTON_COLOR))
        ) {
            IconButton(
                modifier = Modifier.size(300.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(ICON_COLOR)),
                onClick = { /*TODO: import camera API to take a photo*/ }
            ) {
                Icon(
                    modifier = Modifier.size(54.dp),
                    painter = painterResource(R.drawable.camera),
                    contentDescription = "camera"
                )
            }
        }
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(CIRCLE_BUTTON_COLOR))
        ) {
            IconButton(
                modifier = Modifier.size(300.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(ICON_COLOR)),
                onClick = { /*TODO: import gallery from phone*/ }
            ) {
                Icon(
                    modifier = Modifier.size(58.dp),
                    painter = painterResource(R.drawable.gallery),
                    contentDescription = "gallery"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    FoodShotTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MainScreen()
        }
    }
}