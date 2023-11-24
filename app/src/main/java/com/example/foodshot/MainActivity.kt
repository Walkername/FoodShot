package com.example.foodshot

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshot.ui.theme.FoodShotTheme
import com.example.foodshot.ui.theme.Titan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

const val ICON_COLOR = 0xfffef1ce
const val CIRCLE_BUTTON_COLOR = 0x14f8f4e8
const val APP_NAME_COLOR = 0xfff8f4e8

class MainActivity : ComponentActivity() {

    private val dataProcess = DataProcess(context = this)
    private var calories = Calories()

    private lateinit var ortEnvironment: OrtEnvironment
    private lateinit var session: OrtSession
    private lateinit var classes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        load()
        setContent {
            val navController = rememberNavController()
            FoodShotTheme {
                // GALLERY VARIABLES
                var imageUri by remember {
                    mutableStateOf<Uri?>(null)
                }
                var galBitmap: Bitmap? = null
                val galleryLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                        imageUri = uri
                    }

                // CAMERA VARIABLES
                val viewModel = viewModel<MainViewModel>()
                val camBitmap by viewModel.bitmaps.collectAsState()

                var bitmapChoice = false

                val resultLabels = remember {
                    mutableStateOf<MutableList<String>>(mutableListOf())
                }
                NavHost(
                    navController = navController,
                    startDestination = "MainScreen"
                ) {
                    composable("MainScreen") {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MainScreen(
                                onClickHistory = { navController.navigate("HistoryScreen") },
                                onClickCamera = { navController.navigate("CameraScreen") },
                                onClickGallery = {
                                    bitmapChoice = true
                                    galleryLauncher.launch("image/*")
                                    navController.navigate("InfoScreen")
                                }
                            )
                        }
                    }

                    composable("HistoryScreen") {
                        HistoryScreen(
                            backToMainScreen = { navController.navigate("MainScreen") }
                        )
                    }

                    composable("CameraScreen") {
                        bitmapChoice = false
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
                            takePhoto = {
                                takePhoto(controller, viewModel::onTakePhoto) {
                                    navController.navigate("InfoScreen")
                                }
                            },
                            backToMainScreen = { navController.navigate("MainScreen") }
                        )
                    }

                    composable("InfoScreen") {
                        if (bitmapChoice) {
                            val context = LocalContext.current
                            imageUri?.let {
                                galBitmap = BitmapFactory.decodeStream(
                                    context.contentResolver.openInputStream(it)
                                )
                            }
                            galBitmap?.let {
                                setViewAndDetect(it, resultLabels)
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Button(onClick = { navController.navigate("MainScreen") }) {
                                    Text(text = "Back")
                                }
                                galBitmap?.let { btm ->
                                    Image(
                                        bitmap = btm.asImageBitmap(),
                                        contentDescription = "detected_image",
                                        modifier = Modifier
                                            .size(500.dp)
                                    )
                                }
                                for (label in resultLabels.value) {
                                    Text(text = label)
                                }
                            }
                        } else {
                            setViewAndDetect(camBitmap[0], resultLabels)
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Button(onClick = { navController.navigate("MainScreen") }) {
                                    Text(text = "Back")
                                }
                                Image(
                                    bitmap = camBitmap[0].asImageBitmap(),
                                    contentDescription = "detected_image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                                for (label in resultLabels.value) {
                                    Text(text = label)
                                }
                            }
                        }

                        /*
                        InfoScreen(
                            bitmap = bitmapToDisplay,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                        */
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
        onPhotoTaken: (Bitmap) -> Unit,
        navToInfoScreen: () -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onPhotoTaken(image.toBitmap())
                    navToInfoScreen()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            }
        )
    }

    private fun runObjectDetection(bitmap: Bitmap, foodLabels: MutableState<MutableList<String>>) {
        val scaledBitmap = dataProcess.bitmapToScaledBitmap(bitmap)

        val floatBuffer = dataProcess.bitmapToFloatBuffer(scaledBitmap)
        val inputName = session.inputNames.iterator().next()
        val shape = longArrayOf(
            DataProcess.BATCH_SIZE.toLong(),
            DataProcess.PIXEL_SIZE.toLong(),
            DataProcess.INPUT_SIZE.toLong(),
            DataProcess.INPUT_SIZE.toLong()
        )

        // create Input for YOLO using processing image
        val inputTensor = OnnxTensor.createTensor(ortEnvironment, floatBuffer, shape)
        val resultTensor = session.run(Collections.singletonMap(inputName, inputTensor))
        val outputs = resultTensor.get(0).value as Array<*>
        val results = dataProcess.outputsToNPMSPredictions(outputs) // model predictions

        val resultLabels = mutableListOf<String>()

        // convert predictions into DetectionResult format
        val resultToDisplay = results.map {
            val className = classes[it.classIndex]

            val text = className
            val cal = "$className ${calories.calories[className]}"
            resultLabels.add(cal)
        }
        foodLabels.value = resultLabels
    }

    private fun setViewAndDetect(bitmap: Bitmap, labels: MutableState<MutableList<String>>) {
        // Run ODT and display result
        // Note that we run this in the background thread to avoid blocking the app UI because
        // ONNX object detection is a synchronised process.
        lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap, labels) }
    }

    private fun load() { // load labels and neural model
        dataProcess.loadModel()
        dataProcess.loadLabel()

        ortEnvironment = OrtEnvironment.getEnvironment()
        session = ortEnvironment.createSession(
            this.filesDir.absolutePath.toString() + "/" + DataProcess.FILE_NAME,
            OrtSession.SessionOptions()
        )

        this.classes = dataProcess.classes
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
                onClick = { onClickGallery() }
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
            MainScreen(
                onClickHistory = {},
                onClickGallery = {},
                onClickCamera = {}
            )
        }
    }
}