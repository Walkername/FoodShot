package com.example.foodshot

import android.graphics.Bitmap
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshot.ui.theme.APP_NAME_COLOR
import com.example.foodshot.ui.theme.CIRCLE_BUTTON_COLOR
import com.example.foodshot.ui.theme.ICON_COLOR
import com.example.foodshot.ui.theme.TRANSLUCENT_WHITE
import com.example.foodshot.ui.theme.Titan

@Composable
fun InfoScreen(
    bitmapImage: Bitmap?,
    foodLabels: MutableState<MutableList<String>>,
    backToPrevScreen: () -> Unit
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
                            backToPrevScreen()
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
                    text = "Food Info",
                    color = Color(APP_NAME_COLOR),
                    fontSize = 36.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }
            InfoBox(
                bitmapImage = bitmapImage,
                foodLabels = foodLabels
            )
        }
    }
}

@Composable
fun InfoBox(
    bitmapImage: Bitmap?,
    foodLabels: MutableState<MutableList<String>>
) {
    Column(
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        bitmapImage?.let {
            Image(
                bitmap = bitmapImage.asImageBitmap(),
                contentDescription = "food_picture"
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 35.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            if (foodLabels.value.isEmpty()) {
                Text(
                    text = "The Neural Network did not detect food in the photo ",
                    color = Color(APP_NAME_COLOR),
                    lineHeight = 36.sp,
                    fontSize = 36.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.Center
                )
            } else {
                for (foodLabel in foodLabels.value) {
                    val foodProps = foodLabel.split(":")
                    val foodName = foodProps[0].trim()
                    var kcalPerHundredGrams : Int?
                    if (foodProps.size != 1) {
                        kcalPerHundredGrams = foodProps[1].trim().toIntOrNull()

                        if (kcalPerHundredGrams != null) {
                            InfoCell(
                                foodName = foodName,
                                kcalPerHundredGrams = kcalPerHundredGrams
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoCell(
    foodName: String,
    kcalPerHundredGrams: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Text(
                text = "$foodName: $kcalPerHundredGrams kcal (100g)",
                color = Color(APP_NAME_COLOR),
                fontSize = 25.sp,
                fontFamily = Titan,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val weight = remember {
                mutableStateOf("")
            }
            val unitChoice = remember {
                mutableStateOf(false)
            }
            OutlinedTextField(
                value = weight.value,
                onValueChange = { newWeight -> weight.value = newWeight },
                textStyle = TextStyle(
                    color = Color(APP_NAME_COLOR),
                    fontSize = 20.sp,
                    fontFamily = Titan,
                    textAlign = TextAlign.Center
                ),
                trailingIcon = {
                    Text(
                        text = if (!unitChoice.value) "g" else "kg",
                        color = Color(APP_NAME_COLOR),
                        fontSize = 25.sp,
                        fontFamily = Titan,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .toggleable(
                                value = unitChoice.value,
                                onValueChange = {
                                    unitChoice.value = it
                                }
                            )
                    )
                },
                placeholder = {
                    Text(
                        text = "Type weight",
                        color = Color(TRANSLUCENT_WHITE),
                        fontSize = 20.sp,
                        fontFamily = Titan,
                        textAlign = TextAlign.Center
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0x00000000),
                    focusedBorderColor = Color(APP_NAME_COLOR),
                    unfocusedBorderColor = Color(APP_NAME_COLOR)
                ),
                modifier = Modifier
                    .weight(1.6f)
            )
            val weightValue = if (weight.value == "") 0.0f else weight.value.toFloat()
            val kcalValue = if (!unitChoice.value) {
                weightValue / 100 * kcalPerHundredGrams
            } else {
                weightValue * 1000 / 100 * kcalPerHundredGrams
            }
            Text(
                text = "${String.format("%.2f", kcalValue)} kcal",
                color = Color(APP_NAME_COLOR),
                fontSize = 20.sp,
                fontFamily = Titan,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoScreenPreview() {
    //InfoScreen()
}