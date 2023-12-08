package com.example.foodshot

import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshot.ui.theme.APP_NAME_COLOR
import com.example.foodshot.ui.theme.CIRCLE_BUTTON_COLOR
import com.example.foodshot.ui.theme.ICON_COLOR
import com.example.foodshot.ui.theme.Titan

@Composable
fun HistoryScreen(
    resultLabels: MutableList<Pair<String, MutableList<String>>>,
    backToMainScreen: () -> Unit
) {
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
        Column(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                    end = 25.dp,
                    top = 45.dp,
                    bottom = 45.dp
                )
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(CIRCLE_BUTTON_COLOR))

                ) {
                    IconButton(
                        onClick = {
                            backToMainScreen()
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
                        .padding(top = 5.dp)
                )
            }
            HistoryBox(resultLabels)
        }
    }
}

@Composable
fun HistoryBox(
    resultLabels: MutableList<Pair<String, MutableList<String>>>
) {
    Column(
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Date",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1.5f)
            )
            Text(
                text = "Time",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "Food",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(2f)
            )
            Text(
                text = "Kcal",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 25.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            for (dateFood in resultLabels) {
                val dateTime = dateFood.first.split(" ")
                val date = dateTime[0]
                val time = dateTime[1]
                val foodLabels = dateFood.second
                val goods = foodLabels.map { good ->
                    good.split(":")[0].trim()
                }
                val kcals = foodLabels.map { kcal ->
                    kcal.split(":")[1].trim()
                }
                HistoryCell(
                    date = date,
                    time = time,
                    foodNames = goods.joinToString("\n"),
                    kcal = kcals
                )
            }
        }
    }
}

@Composable
fun HistoryCell(
    date: String,
    time: String,
    foodNames: String,
    kcal: List<String>
) {
    val cellState = remember {
        mutableStateOf(false)
    }
    val cellHeight: Dp
    val boxHeight: Dp
    val foodList = foodNames.split("\n")
    var timesToExtend = foodList.size
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val foodBoundsWidth = screenWidth / 5.5f * 2f
    val fontsizeToDp = with(LocalDensity.current) {
        15f.toDp()
    }
    for (food in foodList) {
        if (fontsizeToDp * food.length > foodBoundsWidth) {
            timesToExtend *= 2
        }
    }
    val kcalToDisplay : String
    val textOverflow : TextOverflow
    if (!cellState.value) {
        cellHeight = 40.dp
        boxHeight = 50.dp
        kcalToDisplay = kcal[0]
        textOverflow = TextOverflow.Ellipsis
    } else {
        cellHeight = (40 * timesToExtend).dp
        boxHeight = ((40 * timesToExtend + 10).dp)
        kcalToDisplay = kcal.joinToString("\n")
        textOverflow = TextOverflow.Clip
    }
    Box(
        modifier = Modifier
            .height(boxHeight)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .height(cellHeight)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(CIRCLE_BUTTON_COLOR))
                .toggleable(
                    value = cellState.value,
                    onValueChange = {
                        cellState.value = it
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp, end = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1.5f)
                )
                Text(
                    text = time,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = foodNames,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    overflow = textOverflow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(2f)
                )
                Text(
                    text = kcalToDisplay,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryActivityPreview() {
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
        Column(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                    end = 25.dp,
                    top = 45.dp,
                    bottom = 45.dp
                )
        ) {
            Box {
                Box(
                    modifier = Modifier
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
                        .padding(top = 5.dp)
                )
            }
            HistoryBox(mutableListOf(Pair("25-10-2023 15:30", mutableListOf("Pomegranate : 83"))))
        }
    }
}