package com.github.catomon.moemarathon.map.osu

import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.calculateDegrees

fun hitObjectToNotePosition(x: Int, y: Int): Float {
    val degrees = calculateDegrees(256f, 193f, x.toFloat(), y.toFloat()) / 360
    val initialPosition = 100f / PlayScreen.Config.hitZonesAmount * (PlayScreen.Config.hitZonesAmount * degrees).toInt() / 100f
    return initialPosition
}

fun HitObject.toNote(tracingNext: Boolean = false, tracingPrev: Boolean = false): Note { //512x384
    return Note(time / 1000f, hitObjectToNotePosition(x, y), tracingNext, tracingPrev)
}
