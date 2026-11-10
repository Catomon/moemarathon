package com.github.catomon.moemarathon.playscreen.ui

import com.badlogic.gdx.Input

data class MobileButton(
    val key: Int,
    val left: Boolean,
    val row: Int,
    val xOff: Float = 0f,
)

object MobileButtons {
    const val SIZE = 200f
    const val TOUCH_RADIUS = 90f
    private const val MARGIN = 20f
    private const val GAP = 8f

    val buttons = listOf(
        MobileButton(key = Input.Keys.F, left = true, row = 2),
        MobileButton(key = Input.Keys.D, left = true, row = 1),
        MobileButton(key = Input.Keys.S, left = true, row = 0, xOff = 75f),

        MobileButton(key = Input.Keys.J, left = false, row = 2),
        MobileButton(key = Input.Keys.K, left = false, row = 1),
        MobileButton(key = Input.Keys.L, left = false, row = 0, xOff = -75f),
    )

    fun size(unitsPerPixel: Float, scale: Float): Float = SIZE * scale * unitsPerPixel

    fun x(button: MobileButton, viewportWidth: Float, unitsPerPixel: Float, scale: Float, position: Float): Float {
        val size = size(unitsPerPixel, scale)
        val margin = MARGIN * scale * unitsPerPixel
        val shift = (position - 1f) * viewportWidth / 6f
        val center =
            if (button.left) margin + size / 2f + shift
            else viewportWidth - margin - size / 2f - shift
        return center + button.xOff * scale * unitsPerPixel
    }

    fun y(button: MobileButton, unitsPerPixel: Float, scale: Float): Float {
        val size = size(unitsPerPixel, scale)
        val margin = MARGIN * scale * unitsPerPixel
        val gap = GAP * scale * unitsPerPixel
        return margin + size / 2f + button.row * (size + gap)
    }
}
