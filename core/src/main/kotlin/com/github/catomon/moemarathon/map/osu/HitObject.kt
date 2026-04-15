package com.github.catomon.moemarathon.map.osu

data class HitObject(
    val x: Int,
    val y: Int,
    val time: Int,
    val type: Int,
    val hitSound: Int = 0,
    val objectParams: List<String> = emptyList(),
    val hitSample: String = "",
    val comboIndex: Int = 0,
    val isNewCombo: Boolean = false,
)
