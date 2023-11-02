package com.example.foodshot

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshot.ui.theme.FoodShotTheme
import com.example.foodshot.ui.theme.Titan

const val ICON_COLOR = 0xfffef1ce
const val CIRCLE_BUTTON_COLOR = 0x14f8f4e8
const val APP_NAME_COLOR = 0xfff8f4e8

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            FoodShotTheme {
                val viewModel = viewModel<MainViewModel>()
                val bitmaps by viewModel.bitmaps.collectAsState()
                NavHost(
                    navController = navController,
                    startDestination = "MainScreen"
                ) {
                    composable("MainScreen") {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MainScreen(
                                { navController.navigate("HistoryScreen") },
                                { navController.navigate("CameraScreen") },
                                { navController.navigate("GalleryScreen") }
                            )
                        }
                    }

                    composable("HistoryScreen") {
                        HistoryScreen(
                            backToMainScreen = { navController.navigate("MainScreen") }
                        )
                    }

                    composable("CameraScreen") {
                        //requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        if (!hasRequiredPermission()) {
                            ActivityCompat.requestPermissions(
                                this@MainActivity, CAMERAX_PERMISSION, 0
                            )
                        }
                        val controller = remember {
                            LifecycleCameraController(applicationContext).apply {
                                setEnabledUseCases(
                                    CameraController.IMAGE_CAPTURE
                                )
                            }
                        }
                        CameraScreen(
                            controller = controller,
                            takePhoto = { takePhoto(controller, viewModel::onTakePhoto) },
                            backToMainScreen = { navController.navigate("MainScreen") }
                        )
                    }

                    composable("GalleryScreen") {
                        GalleryScreen(
                            bitmaps = bitmaps,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

            }
        }
    }

    private fun hasRequiredPermission(): Boolean {
        return CAMERAX_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                    applicationContext,
                    it
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhotoTaken: (Bitmap) -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object: ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onPhotoTaken(image.toBitmap())
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            }
        )
    }
}

@Composable
fun MainScreen(
    onClickHistory: () -> Unit,
    onClickCamera: () -> Unit,
    onClickGallery: () -> Unit
) {
    Image(
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxSize(),
        bitmap = ImageBitmap.imageResource(R.drawable.background_pic),
        contentDescription = "background_pic"
    )
    Column {
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
            onClickHistory = onClickHistory,
            onClickCamera = onClickCamera,
            onClickGallery = onClickGallery,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 30.dp)
        )
    }
}

@Composable
fun Menu(
    modifier: Modifier,
    onClickHistory: () -> Unit,
    onClickCamera: () -> Unit,
    onClickGallery: () -> Unit
) {
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
                onClick = { onClickHistory() }
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
                onClick = { onClickCamera() }
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
                onClick = { onClickGallery() /*TODO: import gallery from phone*/ }
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
            //MainScreen(/* Is not possible to pass parameters */)
        }
    }
}