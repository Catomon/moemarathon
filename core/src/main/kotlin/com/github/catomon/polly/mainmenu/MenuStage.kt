package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.catomon.polly.Const
import com.github.catomon.polly.difficulties.UnlockedOnlyPlaySets
import com.github.catomon.polly.game
import com.github.catomon.polly.map.GameMap
import com.github.catomon.polly.map.MapsManager
import com.github.catomon.polly.utils.createTable
import com.github.catomon.polly.widgets.addChangeListener
import com.kotcrab.vis.ui.widget.VisTextButton
import kotlin.concurrent.thread

class MenuStage(val menuScreen: MenuScreen = game.menuScreen) : BgStage() {
    init {
        createTable().apply {
            center()
            add(VisTextButton("Start").addChangeListener {
                menuScreen.changeStage(DifficultySelectStage())
            }).center()
            row()
            add(VisTextButton("Maps").addChangeListener {
                menuScreen.changeStage(MapSelectStage(UnlockedOnlyPlaySets()))
                //menuScreen.changeStage(MapSelectStage())
            }).center()
            row()
            if (!Const.IS_RELEASE) {
                add(VisTextButton("All Maps").addChangeListener {
                    menuScreen.changeStage(MapSelectStage())
                }).center()
                row()
            }
            add(VisTextButton("Settings")).center()
            row()
            add(VisTextButton("Exit").addChangeListener {
                Gdx.app.exit()
            }).center()
        }
    }

    fun setRandomBg() {
        thread(true) {
            val map = GameMap(MapsManager.collectMapFiles().random())
            Gdx.app.postRunnable {
                root.addActorAt(
                    0,
                    background.also { if (it.sprite == null) it.sprite = Sprite(map.newBackgroundTexture()) })
            }
        }
    }
}
