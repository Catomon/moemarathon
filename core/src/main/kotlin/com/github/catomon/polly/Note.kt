package com.github.catomon.polly

data class Note(
    val timing: Float,
    var initialPosition: Float = 1f,
    val tracingNext: Boolean = false,
    val tracingPrev: Boolean = false,
    var visual: Int = 0,
    var color: Int = 0,
)

