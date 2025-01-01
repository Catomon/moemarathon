package com.github.catomon.moemarathon.playscreen.ui

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.moemarathon.AudioManager
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.mainmenu.MenuStage
import com.github.catomon.moemarathon.mainmenu.newContinueButton
import com.github.catomon.moemarathon.mainmenu.newEndButton
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.ui.actions.OneAction
import com.github.catomon.moemarathon.utils.SpriteActor
import com.github.catomon.moemarathon.utils.addCover
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.removeCover
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton

class PlayHud(private val playScreen: PlayScreen) :
    Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })), NoteListener {

    private val scoreLabel = ScoreLabel(playScreen.stats)
    private val comboLabel = ComboLabel(playScreen.stats)

    private val skin = playScreen.skin
    private val hitGreat = assets.mainAtlas.findRegion(skin.hit + "hit_great")
    private val hitOk = assets.mainAtlas.findRegion(skin.hit + "hit_ok")
    private val hitMiss = assets.mainAtlas.findRegion(skin.hit + "hit_miss")
    private val hitTooEarly = assets.mainAtlas.findRegion(skin.hit + "too_early")
    private val hitTooFar = assets.mainAtlas.findRegion(skin.hit + "too_far")
    private val hitQuestion = assets.mainAtlas.findRegion("question")

    private val menuTable = VisTable().apply {
        setFillParent(true)
        center()

        add(newLabel(playScreen.gameMap.file.nameWithoutExtension()).apply { setFontScale(0.5f) }).colspan(3).top()
        row()
        add(newLabel("Game paused")).colspan(3).padBottom(32f)
        row()
        add(newEndButton().addChangeListener {
            playScreen.paused = false
            addAction(OneAction {
                game.menuScreen.changeStage(MenuStage())
                game.screen = game.menuScreen
            })
        })
        add().width(100f)
        add(newContinueButton().addChangeListener {
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

        createTable(newTextButton("Pause").apply {
            addChangeListener {
                playScreen.paused = true
                showMenu()
            }
            label.setFontScale(0.75f)
        }).left().top()
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
                AudioManager.playSound(AudioManager.hitSound)
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
    }
}
