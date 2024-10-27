package com.example.mastersdegree.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf

data class MagneticField(
    val x: Float = 0.0F,
    val y: Float = 0.0F,
    val z: Float = 0.0F,
)