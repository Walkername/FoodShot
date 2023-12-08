package com.example.foodshot

import android.graphics.Bitmap
import kotlinx.serialization.Serializable

@Serializable
data class ActionsData (
    val resultLabels: MutableList<Pair<String, MutableList<String>>>
)