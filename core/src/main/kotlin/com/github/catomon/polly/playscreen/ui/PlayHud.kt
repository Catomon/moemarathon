package com.github.catomon.polly.playscreen.ui

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.AudioManager
import com.github.catomon.polly.assets
import com.github.catomon.polly.game
import com.github.catomon.polly.mainmenu.MenuStage
import com.github.catomon.polly.playscreen.Note
import com.github.catomon.polly.playscreen.NoteListener
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.SpriteActor
import com.github.catomon.polly.utils.addCover
import com.github.catomon.polly.utils.removeCover
import com.github.catomon.polly.widgets.addChangeListener
import com.github.catomon.polly.widgets.newLabel
import com.github.catomon.polly.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisTable

class PlayHud(private val playScreen: PlayScreen) :
    Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })), NoteListener {

    private val scoreLabel = ScoreLabel(playScreen.stats)
    private val comboLabel = ComboLabel(playScreen.stats)

    private val hitGreat = assets.mainAtlas.findRegion("hit_great")
    private val hitOk = assets.mainAtlas.findRegion("hit_ok")
    private val hitMiss = assets.mainAtlas.findRegion("hit_miss")
    private val hitTooEarly = assets.mainAtlas.findRegion("too_early")
    private val hitTooFar = assets.mainAtlas.findRegion("too_far")
    private val hitQuestion = assets.mainAtlas.findRegion("question")

    private val menuTable = VisTable().apply {
        setFillParent(true)
        center()

        add(newLabel("Game paused")).colspan(3).padBottom(32f)
        row()
        add(newTextButton("<End").addChangeListener {
            game.menuScreen.changeStage(MenuStage())
            game.screen = game.menuScreen
        })
        add().width(100f)
        add(newTextButton("Resume>").addChangeListener {
            playScreen.paused = false
            hideMenu()
        })
    }

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

    fun showMenu() {
        addCover()
        addActor(menuTable)
    }

    fun hideMenu() {
        removeCover()
        menuTable.remove()
    }

    override fun onNoteEvent(id: Int, note: Note) {
        val notePos = with(playScreen) { note.calcPosition() }

        comboLabel.onNoteEvent(id, note)

        when (id) {
            1, 2, 3, NoteListener.HIT_TRACE, NoteListener.NOTE_TRACE_START -> {
                AudioManager.hitSound.play()
            }
        }

        val noteToStagePos = notePos
        val noteIsGreat = with(playScreen) { note.isGreat() }

        addActor(
            SpriteActor(
                Sprite(
                    when (id) {
                        0 -> hitMiss
                        1 -> {
                            if (noteIsGreat) hitGreat
                            else hitOk
                        }

                        7 -> hitGreat
                        4 -> hitTooEarly
                        5 -> hitTooFar
                        else -> hitQuestion
                    }
                )
            ).apply {
                setSize(playScreen.noteRadius * 2, playScreen.noteRadius * 2)
                setPosition(noteToStagePos.x, noteToStagePos.y)
                addAction(
                    Actions.sequence(
                        Actions.parallel(Actions.moveBy(0f, 16f, 1f), Actions.fadeOut(1f)),
                        Actions.removeActor()
                    )
                )
            }
        )

//        addActor(
//            VisLabel(
//                when (id) {
//                    0 -> "Miss!"
//                    1 -> {
//                        //if (noteIsGreat) "Great!" else "Ok!"
//
////                        (if (note.tracingPrev) SCORE_GAIN_TRACE
////                        else if (noteIsGreat) SCORE_GAIN_GREAT
////                        else SCORE_GAIN_OK).toString()
//
//                        //if (note.tracingPrev) "九百"
//                        //                        else
//                        if (noteIsGreat) "三百"
//                        else "\t\t二百"
//                    }
//
//                    7 -> "九百"
//
//                    2 -> "Early"
//                    3 -> "Late"
//                    4 -> "Too early!"
//                    5 -> "Too far!"
//                    else -> ""
//                }
//            ).apply {
//                color = when (id) {
//                    0 -> Color.RED
//                    1 -> {
//                        //if (noteIsGreat) Color.GREEN else Color.YELLOW
//                        if (note.tracingPrev) Color.YELLOW
//                        else if (noteIsGreat) Color.YELLOW
//                        else Color.GREEN
//                    }
//                    7 -> {
//                        Color.YELLOW
//                    }
//                    2 -> Color.ORANGE
//                    3 -> Color.BLUE
//                    4 -> Color.ORANGE
//                    5 -> Color.BLUE
//                    else -> Color.RED
//                }
//                setFontScale(0.75f)
//                pack()
//                setPosition(noteToStagePos.x - width / 2, noteToStagePos.y - height / 2)
//                addAction(Actions.parallel(Actions.moveBy(0f, 16f, 1f), Actions.fadeOut(1f)))
//            }
//        )
    }
}
