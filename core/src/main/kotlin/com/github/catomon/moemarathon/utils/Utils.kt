package com.github.catomon.moemarathon.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.atan2

fun setMouseCursor(cursorFileName: String = "cursor.png") {
    Gdx.graphics.setCursor(createCursor(cursorFileName))
}

fun createCursor(cursorFileName: String = "cursor.png"): Cursor {
    val pixmap = Pixmap(Gdx.files.internal(cursorFileName))
    val xHotspot = 16
    val yHotspot = 16
    val cursor: Cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot)
    pixmap.dispose()
    return cursor
}

fun calculateDegrees(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val degrees = atan2(y2 - y1, x2 - x1) * 180 / MathUtils.PI
    return if (degrees < 0) degrees + 360 else degrees
}
