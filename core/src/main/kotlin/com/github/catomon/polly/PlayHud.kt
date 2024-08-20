package com.github.catomon.polly

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisLabel

class PlayHud : Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })) {

    init {
//        addActor(VisTable().apply {
//
//        })
    }

    fun onNoteEvent(id: Int, notePos: Vector2) {
        when (id) {
            0 -> "Miss!"
            1 -> {
                AudioManager.hitSound.play()
            }
            2 -> {
                AudioManager.hitSound.play()
            }
            3 -> {
                AudioManager.hitSound.play()
            }
            4 -> "Too early!"
            5 -> "Too far!"
            else -> "Unknown"
        }

        val noteToStagePos = notePos
        addActor(
            VisLabel(
                when (id) {
                    0 -> "Miss!"
                    1 -> "Great!"
                    2 -> "Early"
                    3 -> "Late"
                    4 -> "Too early!"
                    5 -> "Too far!"
                    else -> "Unknown"
                }
            ).apply {
                color = when (id) {
                    0 -> Color.RED
                    1 -> Color.GREEN
                    2 -> Color.ORANGE
                    3 -> Color.BLUE
                    4 -> Color.ORANGE
                    5 -> Color.BLUE
                    else -> Color.RED
                }
                setPosition(noteToStagePos.x, noteToStagePos.y)
                addAction(Actions.parallel(Actions.moveBy(0f, 16f, 1f), Actions.fadeOut(1f)))
            }
        )
    }
}
