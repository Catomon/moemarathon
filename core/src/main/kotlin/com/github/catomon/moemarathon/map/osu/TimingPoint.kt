package com.github.catomon.moemarathon.map.osu

data class TimingPoint(
    val time: Float, //seconds
    val beatLength: Double,
    val uninherited: Boolean
)
