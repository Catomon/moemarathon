package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.utils.Align
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
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisWindow

class MenuStage(private val menuScreen: MenuScreen = game.menuScreen) : BgStage() {

    private val userSave = GamePref.userSave

    private val pauseBgMusicButton = newTextButton("Pause").apply {
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
    }

    private val pauseButtonVisibilityAction
        get() = RunnableAction().apply {
            setRunnable {
                pauseBgMusicButton.isVisible = AudioManager.mapMusic?.isPlaying == true
            }
        }

    init {
        background.color.a = 0.75f

        createTable().apply {
            add(pauseBgMusicButton)
            top().right()
        }

        //cuz the music is resumed with a delay when we are coming to the menu stage through the game pause
        addAction(
            Actions.sequence(
                pauseButtonVisibilityAction,
                Actions.delay(0.25f),
                pauseButtonVisibilityAction,
                Actions.delay(0.75f),
                pauseButtonVisibilityAction
            )
        )

        createTable().apply {
            add(newTextButton("Skins").apply {
                label.setFontScale(0.5f)
            }.addChangeListener {
                menuScreen.changeStage(SkinsStage())
            })
            row()
            if (!Const.IS_RELEASE || userSave.normalRank != 0 || userSave.hardRank != 0 || userSave.insaneRank != 0) {
                addAction(OneAction {
                    if (userSave.unlockedAllMaps == 0) {
                        addActor(VisWindow("'Other Maps' unlocked!").also { window ->
                            window.centerWindow()
                            window.add(
                                "You can add other maps to\nthe maps folder\n" +
                                    "and they will appear here."
                            )
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
                add(newTextButton("Other Songs").apply {
                    label.setFontScale(0.5f)
                }.addChangeListener {
                    menuScreen.changeStage(MapSelectStage())
                }).center()
                row()
            } else {
                add(newTextButton("Other Songs").also {
                    it.label.setFontScale(0.5f); it.add(VisImage("locked")).size(48f)
                }).center()
                row()
            }
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
//            if (!Const.IS_RELEASE || userSave.normalRank != 0 || userSave.hardRank != 0 || userSave.insaneRank != 0) {
//                addAction(OneAction {
//                    if (userSave.unlockedAllMaps == 0) {
//                        addActor(VisWindow("'Other Maps' unlocked!").also { window ->
//                            window.centerWindow()
//                            window.add(
//                                "You can add other maps to\nthe maps folder\n" +
//                                    "and they will appear here."
//                            )
//                            window.row()
//                            window.add(newTextButton("OK!").addChangeListener {
//                                window.remove()
//                            })
//                            window.pack()
//                        })
//                        userSave.unlockedAllMaps = 1
//                        GamePref.userSave = userSave
//                        GamePref.save()
//                    }
//                })
//                add(newBigButton("Other Songs").addChangeListener {
//                    menuScreen.changeStage(MapSelectStage())
//                }).center()
//                row()
//            } else {
//                add(newBigButton("Other Songs").also { it.add(VisImage("locked")).size(48f) }).center()
//                row()
//            }
//            add(newBigButton("Skins").addChangeListener {
//                menuScreen.changeStage(SkinsStage())
//            })
//            row()
            add(newBigButton("Achievements").addChangeListener {
                menuScreen.changeStage(AchievementsStage())
            })
            row()
            add(newBigButton("Leaderboard").addChangeListener {
                menuScreen.changeStage(LeaderboardStage())
            })
            row()
            add(
                VisTable().also {
                    val textField = VisTextField(userSave.name, "small")
                    it.add(textField).width(300f)
                    it.add(
                        newTextButton("Save").apply {
                            label.setFontScale(0.5f)
                            addChangeListener {
                                if (textField.isEmpty) return@addChangeListener
                                GamePref.userSave = GamePref.userSave.copy(name = textField.text.take(24).replace("*", "_").replace(" ", "_"))
                                GamePref.save()
                            }
                        }
                    )
                }
            ).fillX().align(Align.center)
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
        Thread {
            val map = GameMap(MapsManager.collectMapFiles().random())
            Gdx.app.postRunnable {
                root.addActorAt(
                    0,
                    background.also { if (it.sprite == null) it.sprite = Sprite(map.newBackgroundTexture()) })
            }
        }.start()
    }
}
