package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.catomon.moemarathon.AudioManager
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.difficulties.PlaySets.UnlockedOnlyPlaySets
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.scene2d.actions.OneAction
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import kotlin.concurrent.thread

class MenuStage(private val menuScreen: MenuScreen = game.menuScreen) : BgStage() {

    private val userSave = GamePref.userSave

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
                            } else {
                                AudioManager.playMapMusic()
                                setText("Pause")
                            }
                    }
                })
                top().right()
            }
        }

        createTable().apply {
            add(VisTextButton("Settings")).center()
            row()
            add(VisTextButton("Exit").addChangeListener {
                Gdx.app.exit()
            })
            bottom().right()
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
            })
            row()
            add(VisTextButton("Non-stop").addChangeListener {
                menuScreen.changeStage(DifficultySelectStage())
            })
            row()
            add(VisTextButton("Maps").addChangeListener {
                menuScreen.changeStage(MapSelectStage(UnlockedOnlyPlaySets))
            })
            row()
            if (!Const.IS_RELEASE || userSave.easyRank != 0 || userSave.normalRank != 0 || userSave.hardRank != 0) {
                addAction(OneAction {
                    if (userSave.unlockedAllMaps == 0) {
                        addActor(VisWindow("All Maps unlocked!").also { window ->
                            window.centerWindow()
                            window.add("Custom maps can be added\nin the maps folder.")
                            window.row()
                            window.add(newTextButton("OK!").addChangeListener {
                                window.remove()
                            })
                            window.pack()
                        })
                        userSave.unlockedAllMaps = 1
                        GamePref.userSave = userSave
                        GamePref.save()
                    }
                })
                add(VisTextButton("All Maps").addChangeListener {
                    menuScreen.changeStage(MapSelectStage())
                }).center()
                row()
            }
            add(VisTextButton("Skins").addChangeListener {
                menuScreen.changeStage(SkinsStage())
            })
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

        userSave.notify.removeIf {
            if (it.startsWith("skin:")) {
                addActor(VisWindow("${it.removePrefix("skin:")} skin unlocked!").also { window ->
                    window.centerWindow()
                    window.add("See it in the Skins menu.")
                    window.row()
                    window.add(newTextButton("OK!").addChangeListener {
                        window.remove()
                    })
                    window.pack()
                })

                return@removeIf true
            }

            return@removeIf false
        }

        GamePref.userSave = userSave
        GamePref.save()
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
