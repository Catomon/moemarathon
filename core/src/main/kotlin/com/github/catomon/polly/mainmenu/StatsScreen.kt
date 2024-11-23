package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.GamePref
import com.github.catomon.polly.assets
import com.github.catomon.polly.difficulties.Ranks
import com.github.catomon.polly.game
import com.github.catomon.polly.map.MapsManager
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.playscreen.playstage.BackgroundActor
import com.github.catomon.polly.utils.createTable
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.github.catomon.polly.widgets.addChangeListener
import kotlin.concurrent.thread

class StatsStage(val playScreen: PlayScreen, val onReturn: () -> Unit = playScreen.onReturn) : Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })) {

    private val menuScreen: MenuScreen = game.screen as MenuScreen
    private val stats = playScreen.stats
    var background = BackgroundActor(Sprite(playScreen.gameMap.newBackgroundTexture())) //todo dispose

    init {
        val totalNotes = MapsManager.createNoteMap(playScreen.gameMap.osuBeatmap).size
        val pGreats = stats.greats.toFloat() / totalNotes.toFloat()
        val pOks = stats.oks.toFloat() / totalNotes.toFloat()
        val pMisses = stats.misses.toFloat() / totalNotes.toFloat()
        val rank = when {
            pGreats == 1f && pMisses == 0f -> "SS"
            pGreats >= 0.8f && pMisses == 0f -> "S"
            pGreats >= 0.7f && pMisses <= 0.05f -> "A"
            pGreats >= 0.6f && pMisses <= 0.10f -> "B"
            pGreats >= 0.5f && pMisses <= 0.15f -> "C"
            pGreats >= 0.4f && pMisses <= 0.20f -> "D"
            else -> "F"
        }
        thread(true) {
            val userSave = GamePref.userSave
            userSave.mapRanks[playScreen.gameMap.file.name()] = Ranks.getRankInt(rank)
            GamePref.userSave = userSave
            GamePref.save()
        }

        addActor(background)
        createTable().apply {
            add(VisLabel(playScreen.gameMap.file.nameWithoutExtension()).also { it.setFontScale(0.5f) }).colspan(2).width(768f)
            row()
            add(VisImage(SpriteDrawable(assets.mainAtlas.createSprite(rank)))).size(320f, 320f)
            add(VisTable().apply {
                add(VisLabel("Score: " + stats.score))
                row()
                add(VisLabel("Great: " + stats.greats).also { it.color = Color.ORANGE })
                row()
                add(VisLabel("Good: " + stats.oks).also { it.color = Color.GREEN })
                row()
                add(VisLabel("Miss: " + stats.misses).also { it.color = Color.RED })
                row()
                add(VisLabel("Combo: " + stats.maxCombo))
            }).width(480f)
        }

        createTable(VisTextButton("<Continue").addChangeListener {
            onReturn()
        }).apply {
            left().bottom()
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        super.touchDown(screenX, screenY, pointer, button)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //onReturn()

        return super.touchUp(screenX, screenY, pointer, button)
    }
}
