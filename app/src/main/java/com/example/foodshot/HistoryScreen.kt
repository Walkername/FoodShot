package com.example.foodshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

@Composable
fun HistoryScreen() {
    val colorStops = arrayOf(
        0.0f to Color(0xffb04847),
        0.5f to Color(0xff5b175c)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colorStops = colorStops,
                    start = Offset(0f, Float.POSITIVE_INFINITY),
                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .padding(start = 25.dp, top = 45.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(CIRCLE_BUTTON_COLOR))

        ) {
            IconButton(
                onClick = {
                },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(ICON_COLOR)),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "BackToMainScreen"
                )
            }
        }
        Text(
            text = "History",
            color = Color(APP_NAME_COLOR),
            fontSize = 36.sp,
            fontFamily = Titan,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        )


    }
}

@Preview(showBackground = true)
@Composable
fun HistoryActivityPreview() {
    HistoryScreen()
}