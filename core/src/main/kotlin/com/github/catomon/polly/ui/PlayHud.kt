package com.github.catomon.polly.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.AudioManager
import com.github.catomon.polly.Note
import com.github.catomon.polly.PlayScreen
import com.github.catomon.polly.gameplay.NoteListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable

class PlayHud(private val playScreen: PlayScreen) :
    Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })), NoteListener {

    private val scoreLabel = ScoreLabel(playScreen.stats)
    private val comboLabel = ComboLabel(playScreen.stats)

    init {
        addActor(VisTable().apply {
            right().top()
            setFillParent(true)
            add(scoreLabel).right().top().padRight(16f)
        })
        addActor(VisTable().apply {
            left().bottom()
            setFillParent(true)
            add(comboLabel).left().bottom().padLeft(16f)
        })

        playScreen.noteListeners.add(comboLabel)
        playScreen.noteListeners.add(scoreLabel)
    }

    override fun onNoteEvent(id: Int, note: Note) {
        val notePos = with(playScreen) { note.calcPosition() }

        comboLabel.onNoteEvent(id, note)

        when (id) {
            1, 2, 3, NoteListener.NOTE_TRACE_START -> {
                AudioManager.hitSound.play()
            }
        }

        val noteToStagePos = notePos
        val noteIsGreat = with(playScreen) { note.isGreat() }
        addActor(
            VisLabel(
                when (id) {
                    0 -> "Miss!"
                    1 -> if (noteIsGreat) "Great!" else "Ok!"
                    2 -> "Early"
                    3 -> "Late"
                    4 -> "Too early!"
                    5 -> "Too far!"
                    else -> ""
                }
            ).apply {
                color = when (id) {
                    0 -> Color.RED
                    1 -> if (noteIsGreat) Color.GREEN else Color.YELLOW
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
