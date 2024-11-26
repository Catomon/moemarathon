package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.catomon.moemarathon.AudioManager
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.difficulties.PlaySets.UnlockedOnlyPlaySets
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import kotlin.concurrent.thread

class MenuStage(private val menuScreen: MenuScreen = game.menuScreen) : BgStage() {
    init {
        if (AudioManager.mapMusic?.isPlaying == true) {
            createTable().apply {
                add(VisTextButton("Pause").apply {
                    label.setFontScale(0.5f)
                    addChangeListener {
                        if (AudioManager.mapMusic != null)
                            if (AudioManager.mapMusic?.isPlaying == true) {
                                AudioManager.pauseMapMusic()
                                setText("Resume")
                            }
                            else {
                                AudioManager.playMapMusic()
                                setText("Pause")
                            }
                    }
                })
                top().right()
            }
        }

        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            center().top()
        }

        createTable().apply {
            center()
            add(VisTextButton("Start").addChangeListener {
                menuScreen.changeStage(DifficultySelectStage())
            }).center()
            row()
            add(VisTextButton("Maps").addChangeListener {
                menuScreen.changeStage(MapSelectStage(UnlockedOnlyPlaySets))
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

        createTable().apply {
            add(VisTextButton("monscout.itch.io").apply {
                label.setFontScale(0.5f)
                addChangeListener {
                    Gdx.app.net.openURI("https://monscout.itch.io/")
                }
            })
            center().bottom()
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
