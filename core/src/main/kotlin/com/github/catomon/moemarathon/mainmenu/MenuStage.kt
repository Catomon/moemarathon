package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.difficulties.PlaySets
import com.github.catomon.moemarathon.difficulties.PlaySets.UnlockedOnlyPlaySets
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.ui.actions.OneAction
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import kotlin.concurrent.thread

class MenuStage(private val menuScreen: MenuScreen = game.menuScreen) : BgStage() {

    private val userSave = GamePref.userSave

    init {
        background.color.a = 0.75f
        if (AudioManager.mapMusic?.isPlaying == true) {
            createTable().apply {
                add(newTextButton("Pause").apply {
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
            add(newTextButton("Settings").apply {
                label.setFontScale(0.5f)
                addChangeListener {
                    menuScreen.changeStage(SettingsStage())
                }
            }).center()
            row()
            add(newTextButton("Credits").apply {
                label.setFontScale(0.5f)
                addChangeListener {
                    menuScreen.changeStage(CreditsStage())
                }
            }).center()
            row()
            add(newTextButton("Exit").addChangeListener {
                Gdx.app.exit()
            }.apply { label.setFontScale(0.5f) })
            bottom().right()
        }

        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            row()
            add(VisImage("logo")).prefSize(128f).colspan(2)

            center().top()
        }

        createTable().apply {
            center()
            add(newBigButton("Start").addChangeListener {
                menuScreen.changeStage(DifficultySelectStage())
            })
//            add(newButton("start").addChangeListener {
//                menuScreen.changeStage(DifficultySelectStage())
//            })
            row()
            add(newBigButton("Song Select").addChangeListener {
                menuScreen.changeStage(MapSelectStage(UnlockedOnlyPlaySets))
            })
            row()
            if (!Const.IS_RELEASE || userSave.normalRank != 0 || userSave.hardRank != 0 || userSave.insaneRank != 0) {
                addAction(OneAction {
                    if (userSave.unlockedAllMaps == 0) {
                        addActor(VisWindow("'Other Maps' unlocked!").also { window ->
                            window.centerWindow()
                            window.add("You can add other maps to\nthe maps folder\n" +
                                "and they will appear here.")
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
                add(newBigButton("Other Songs").addChangeListener {
                    menuScreen.changeStage(MapSelectStage())
                }).center()
                row()
            } else {
                add(newBigButton("Other Songs").also { it.add(VisImage("locked")).size(48f) }).center()
                row()
            }
            add(newBigButton("Skins").addChangeListener {
                menuScreen.changeStage(SkinsStage())
            })
            row()
            add(newBigButton("Achievements").addChangeListener {
                menuScreen.changeStage(AchievementsStage())
            })
        }

//        createTable().apply {
//            add(VisTextButton("monscout.itch.io").apply {
//                label.setFontScale(0.5f)
//                addChangeListener {
//                    Gdx.app.net.openURI("https://monscout.itch.io/")
//                }
//            })
//            center().bottom()
//        }

        userSave.notify.removeIf {
            when {
                it.startsWith("skin:") -> {
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

                it == PlaySets.NonStop.name -> {
                    addActor(VisWindow("Non-Stop map unlocked!").also { window ->
                        window.centerWindow()
                        window.add("Navigate to 'Start' to see.")
                        window.row()
                        window.add(newTextButton("OK!").addChangeListener {
                            window.remove()
                        })
                        window.pack()
                    })

                    return@removeIf true
                }
                else -> return@removeIf false
            }
        }

        Achievements.list.forEach {
            if (it.type == Achievement.Type.MainMenu) {
                if (!userSave.achievements.contains(it.id))
                    if (it.condition(AchieveParam(this)))
                        userSave.achievements.add(it.id)
            }
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
