package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisSlider

class SettingsStage() :
    BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen
    private val userSave = GamePref.userSave
    private var musicVolume = 0f
    private var soundVolume = 0f
    private var fullscreen = false

    init {
        addGameNameLabel()

        createTable().apply {
            add(newTextButton(if (Gdx.graphics.isFullscreen) "(F11) Windowed" else "(F11) Fullscreen").also { button ->
                button.addChangeListener {
                    if (Gdx.graphics.isFullscreen) {
                        Gdx.graphics.setWindowedMode(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT)
                        button.setText("(F11) Fullscreen")
                        fullscreen = false
                    } else {
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
                        button.setText("(F11) Windowed")
                        fullscreen = true
                    }
                }
            })
            row()
            add("Music volume")
            row()
            add(VisSlider(0f, 100f, 5f, false).also {
                it.setValue(AudioManager.musicVolume * 100f)
                musicVolume = it.percent
                it.addChangeListener {
                    AudioManager.musicVolume = it.percent
                    musicVolume = it.percent
                }
            }).padBottom(20f).width(250f)
            row()
            add("Sound volume")
            row()
            add(VisSlider(0f, 100f, 5f, false).also {
                it.setValue(AudioManager.soundVolume * 100f)
                soundVolume = it.percent
                it.addChangeListener {
                    AudioManager.soundVolume = it.percent
                    soundVolume = it.percent
                }
            }).width(250f)
            row()
            if (Config.IS_DESKTOP && !Config.IS_RELEASE || userSave.normalRank != 0 || userSave.hardRank != 0 || userSave.insaneRank != 0) {
                add(newTextButton("Open other maps folder").addChangeListener {
                    try {
                        platformSpecific?.desktopOpenMapsFolder()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                row()
            }
        }

        createTable(newBackButton().addChangeListener {
            GamePref.musicVolume = musicVolume
            GamePref.soundVolume = soundVolume
            GamePref.fullscreen = fullscreen
            //GamePref.userSave = userSave
            GamePref.save()
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }
}
