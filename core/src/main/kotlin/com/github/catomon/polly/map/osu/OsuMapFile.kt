package com.github.catomon.polly.map.osu

import com.badlogic.gdx.utils.ArrayMap

data class OsuBeatmap(
    var version: String,
    val sections: ArrayMap<String, ArrayMap<String, String>> = ArrayMap(),
) {

    val audioFileName: String get() = sections["General"]["AudioFilename"] ?: throw IllegalStateException("audioFileName == null")

    val sliderMultiplier get() = sections["Difficulty"]["SliderMultiplier"].toFloat()

    val timingPoints get() = sections["TimingPoints"].map { it.value }

    val hitObjects get() = sections["HitObjects"].map { it.value }
}

data class HitObject(
    val x: Int,
    val y: Int,
    val time: Int,
    val type: Int,
    val hitSound: Int = 0,
    val objectParams: List<String> = emptyList(),
    val hitSample: String = "",
)
