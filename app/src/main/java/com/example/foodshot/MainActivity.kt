package com.example.foodshot

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.icu.text.SimpleDateFormat
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshot.ui.theme.APP_NAME_COLOR
import com.example.foodshot.ui.theme.CIRCLE_BUTTON_COLOR
import com.example.foodshot.ui.theme.FoodShotTheme
import com.example.foodshot.ui.theme.ICON_COLOR
import com.example.foodshot.ui.theme.Titan
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.Date
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private val dataProcess = DataProcess(context = this)
    private var calories = Calories()

    private lateinit var ortEnvironment: OrtEnvironment
    private lateinit var session: OrtSession
    private lateinit var classes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = ProtoDataStoreManager(this)
        load()
        setContent {
            val navController = rememberNavController()
            FoodShotTheme {
                // ACTIONS DATA
                val labelState = remember {
                    mutableStateOf(mutableListOf<Pair<String, MutableList<String>>>())
                }
                LaunchedEffect(key1 = true) {
                    dataStoreManager.getActions().collect {
                        labelState.value = it.resultLabels
                    }
                }
                // GALLERY VARIABLES
                var imageUri by remember {
                    mutableStateOf<Uri?>(null)
                }
                var galBitmap: Bitmap? = null
                val galleryLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                        imageUri = uri
                        navController.navigate("InfoScreen")
                    }

                // CAMERA VARIABLES
                val viewModel = viewModel<MainViewModel>()
                val camBitmap by viewModel.bitmaps.collectAsState()

                // Depends on navigation from
                var bitmapChoice = false

                // DETECTION RESULTS
                val resultLabels = remember {
                    mutableStateOf<MutableList<String>>(mutableListOf())
                }
                val loadingImage = BitmapFactory.decodeResource(resources, R.drawable.loading_icon)
                val imageBitmap = remember {
                    mutableStateOf<Bitmap>(loadingImage)
                }

                var detectionCompleted = false

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
                                    detectionCompleted = false
                                    galleryLauncher.launch("image/*")
                                    // navigation to InfoScreen due to galleryLauncher block
                                }
                            )
                        }
                    }

                    composable("HistoryScreen") {
                        HistoryScreen(
                            resultLabels = labelState.value,
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
                                    detectionCompleted = false
                                    navController.navigate("InfoScreen")
                                }
                            },
                            backToMainScreen = { navController.navigate("MainScreen") }
                        )
                    }

                    composable("InfoScreen") {
                        val context = LocalContext.current

                        imageUri?.let {
                            galBitmap = BitmapFactory.decodeStream(
                                context.contentResolver.openInputStream(it)
                            )
                        }
                        val imgForDetection = if (bitmapChoice) galBitmap else camBitmap[0]
                        val executor = Executors.newSingleThreadExecutor()
                        val runnableDetection = Runnable {
                            imgForDetection?.let {
                                runObjectDetection(it, resultLabels, imageBitmap)
                            }
                            detectionCompleted = true
                        }
                        if (!detectionCompleted) {
                            executor.submit(runnableDetection)
                        } else {
                            executor.shutdown()
                        }
                        val coroutine = rememberCoroutineScope()
                        InfoScreen(
                            bitmapImage = imageBitmap.value,
                            foodLabels = resultLabels,
                            backToPrevScreen = {
                                navController.navigate("MainScreen")
                                if (resultLabels.value.isNotEmpty()) {
                                    val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm").format(Date())
                                    val updatedLabels = addToHistoryList(labelState.value, resultLabels.value, dateTime)
                                    coroutine.launch {
                                        dataStoreManager.saveActions(
                                            ActionsData(
                                                resultLabels = updatedLabels
                                            )
                                        )
                                    }
                                }
                                resultLabels.value.clear()
                                imageBitmap.value = loadingImage
                            }
                        )
                    }
                }
            }
        }
    }

    private fun addToHistoryList (
        currentLabels: MutableList<Pair<String, MutableList<String>>>,
        newLabels: MutableList<String>,
        dateTime: String
    ) : MutableList<Pair<String, MutableList<String>>> {
        var newList = currentLabels.toMutableList()
        val timeAndLabels = Pair(dateTime, newLabels.toMutableList())
        newList.add(0, timeAndLabels)
        if (newList.size > 10) {
            newList = newList.subList(0, 10)
        }
        return newList
    }

    private fun selectImgToDetect(
        context: Context,
        bitmapChoice: Boolean,
        imageUri: Uri?,
        camBitmap: Bitmap
    ): Bitmap? {
        var galBitmap: Bitmap? = null
        imageUri?.let {
            galBitmap = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(it)
            )
        }
        return if (bitmapChoice) galBitmap else camBitmap
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

    private fun runObjectDetection(
        bitmap: Bitmap,
        foodLabels: MutableState<MutableList<String>>,
        imageBitmap: MutableState<Bitmap>
    ) {
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

            val cal = "$className : ${calories.calories[className]}"
            resultLabels.add(cal)

            // Return image to initial size
            val scaleX = bitmap.width / DataProcess.INPUT_SIZE.toFloat()
            val scaleY = scaleX * 9f / 16f
            val realY = bitmap.width * 9f / 16f
            val diffY = realY - bitmap.height

            val rectF = it.rectF
            val left = rectF.left * scaleX
            val right = rectF.right * scaleX
            val top = rectF.top * scaleY - (diffY / 2f)
            val bottom = rectF.bottom * scaleY - (diffY / 2f)

            val newRectF = RectF(left, top, right, bottom)

            DetectionResult(newRectF, className)
        }
        foodLabels.value = resultLabels
        drawDetectionResult(bitmap, resultToDisplay, imageBitmap)
    }

    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<DetectionResult>,
        imageBitmap: MutableState<Bitmap>
    ) {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = android.graphics.Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = android.graphics.Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = 40F
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        imageBitmap.value = outputBitmap
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