package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.github.catomon.moemarathon.AudioManager
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisTextButton
import java.awt.Desktop

class SettingsStage() :
    BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen
    private val userSave = GamePref.userSave
    private var musicVolume = 0f
    private var soundVolume = 0f
    private var fullscreen = false

    init {
        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            center().top()
        }

        createTable().apply {
            add(VisTextButton(if (Gdx.graphics.isFullscreen) "(F11) Windowed" else "(F11) Fullscreen").also { button ->
                button.addChangeListener {
                    if (Gdx.graphics.isFullscreen) {
                        Gdx.graphics.setWindowedMode(Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT)
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
            if (Const.IS_DESKTOP && !Const.IS_RELEASE || userSave.normalRank != 0 || userSave.hardRank != 0 || userSave.insaneRank != 0) {
                add(VisTextButton("Open other maps folder").addChangeListener {
                    try {
                        Desktop.getDesktop().open(Gdx.files.local("maps/other maps/").file().also { it.mkdirs() })

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
