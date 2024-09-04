package com.github.catomon.polly

import kotlin.random.Random

data class Note(
    val timing: Float,
    var initialPosition: Float = 1f,
    val tracingNext: Boolean = false,
    val tracingPrev: Boolean = false,
    var visual: Int = Random.nextInt(0, 3),
    var color: Int = 0,
)

