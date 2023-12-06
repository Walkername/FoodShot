package com.example.foodshot

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.foodshot.ui.theme.ICON_COLOR

@Composable
fun CameraScreen(
    controller: LifecycleCameraController,
    takePhoto: () -> Unit,
    backToMainScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CameraPreview(
            controller = controller,
            modifier = Modifier
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x7f000000))
                .height(80.dp)
        ) {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(ICON_COLOR)),
                onClick = {
                    backToMainScreen()
                },
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "BackToMainScreen",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0x7f000000)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xffffffff))
            ) {
                IconButton(
                    onClick = {
                        takePhoto()
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take a photo",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x7f000000))
                .height(80.dp)
        ) {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(ICON_COLOR)),
                onClick = {

                },
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "BackToMainScreen",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0x7f000000)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xffffffff))
            ) {
                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take a photo",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}