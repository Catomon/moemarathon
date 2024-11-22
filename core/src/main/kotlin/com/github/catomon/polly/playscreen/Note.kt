package com.github.catomon.polly.playscreen

import kotlin.random.Random

data class Note(
    var timing: Float,
    var initialPosition: Float = 1f,
    var tracingNext: Boolean = false,
    var tracingPrev: Boolean = false,
    var visual: Int = Random.nextInt(0, 3),
    var color: Int = 0,
)

