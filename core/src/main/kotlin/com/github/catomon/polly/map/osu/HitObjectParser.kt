package com.github.catomon.moemarathon.map.osu

import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.utils.calculateDegrees

fun HitObject.toNote(tracingNext: Boolean = false, tracingPrev: Boolean = false): Note { //512x384
    return Note(time / 1000f, calculateDegrees(256f, 193f, x.toFloat(), y.toFloat()) / 360, tracingNext, tracingPrev)
}
