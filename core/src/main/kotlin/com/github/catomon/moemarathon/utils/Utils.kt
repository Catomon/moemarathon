package com.github.catomon.moemarathon.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.atan2
import com.github.catomon.moemarathon.Config

val defaultCursor by lazy { createCursor("cursor.png") }
val emptyCursor by lazy { createCursor("empty.png") }
var currentCursor: Cursor? = null
    private set

fun setMouseCursor(cursor: Cursor? = defaultCursor) {
    if (Config.IS_MOBILE) return
    if (cursor == null) return
    Gdx.graphics.setCursor(cursor)
    currentCursor = cursor
}

fun createCursor(cursorFileName: String): Cursor? {
    if (Config.IS_MOBILE) return null
    return run {
        val pixmap = Pixmap(Gdx.files.internal(cursorFileName))
        val xHotspot = 16
        val yHotspot = 16
        Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot).also { pixmap.dispose() }
    }
}

fun calculateDegrees(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val degrees = atan2(y2 - y1, x2 - x1) * 180 / MathUtils.PI
    return if (degrees < 0) degrees + 360 else degrees
}
