package com.github.catomon.polly.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap

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
