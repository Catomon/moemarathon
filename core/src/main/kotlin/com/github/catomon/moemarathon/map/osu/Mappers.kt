package com.github.catomon.moemarathon.map.osu

import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.utils.calculateDegrees

//osu play field size is 512x384
fun hitObjectToNotePosition(hitZonesAmount: Int, x: Int, y: Int): Float {
    val degrees = calculateDegrees(256f, 193f, x.toFloat(), y.toFloat()) / 360
    val initialPosition =
        100f / hitZonesAmount * (hitZonesAmount * degrees).toInt() / 100f
    return initialPosition
}

fun HitObject.toNote(hitZonesAmount: Int, tracingNext: Boolean = false, tracingPrev: Boolean = false): Note {
    return Note(
        time / 1000f,
        hitObjectToNotePosition(hitZonesAmount, x, y),
        tracingNext,
        tracingPrev,
        isNextColor = isNewCombo,
        colorIndex = comboIndex,
        hitSound = hitSound,
        hitSample = hitSample
    )
}
