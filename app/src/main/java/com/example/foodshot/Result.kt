package com.example.foodshot

import android.graphics.RectF

data class Result(val classIndex: Int, val score: Float, val rectF: RectF)

data class DetectionResult(val boundingBox: RectF, val text: String)