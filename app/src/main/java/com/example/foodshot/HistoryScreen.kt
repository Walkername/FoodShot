package com.example.foodshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshot.ui.theme.Titan

@Composable
fun HistoryScreen(
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

            HistoryBox()
        }
    }
}

@Composable
fun HistoryBox() {
    Column(
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Date",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan
            )
            Text(
                text = "Time",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan
            )
            Text(
                text = "Food",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan
            )
            Text(
                text = "Kcal",
                color = Color(APP_NAME_COLOR),
                fontSize = 18.sp,
                fontFamily = Titan
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 25.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            HistoryCell(
                date = "25/10/2023",
                time = "18:53",
                foodName = "Apple",
                kcal = "95"
            )
            HistoryCell(
                date = "25/10/2023",
                time = "18:10",
                foodName = "Black Mushrooms",
                kcal = "120"
            )
            HistoryCell(
                date = "05/10/2023",
                time = "10:11",
                foodName = "Orange",
                kcal = "64"
            )
            /*  Redraw all components is not effective!
            *   TODO: Find another way to add new action without redrawing all
            *   Maybe using of db in application
            * */
            for (i in 0..9) {
                HistoryCell(
                    date = "05/10/2023",
                    time = "10:11",
                    foodName = "Orange",
                    kcal = "64"
                )
            }
        }
    }
}

@Composable
fun HistoryCell(
    date: String,
    time: String,
    foodName: String,
    kcal: String
    /* TODO: Need to know format of data */
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .height(40.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(CIRCLE_BUTTON_COLOR))
        ) {
            Row (
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    modifier = Modifier
                        .padding(start = 15.dp)
                )
                Text(
                    text = time,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    modifier = Modifier
                        .padding(start = 13.dp)
                )
                Text(
                    text = foodName,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 25.dp)
                        .width(100.dp)
                )
                Text(
                    text = kcal,
                    color = Color(APP_NAME_COLOR),
                    fontSize = 15.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 25.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryActivityPreview() {
    //HistoryScreen()
}