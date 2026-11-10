package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.playscreen.ui.MobileButtons
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.setPositionByCenter
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisTable
import java.util.Locale

class SettingsStage() :
    BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen
    private val userSave = GamePref.userSave
    private var musicVolume = 0f
    private var soundVolume = 0f
    private var fullscreen = false

    private val buttonsPreview = ButtonsPreview()

    private fun scaleText(value: Float) = String.format(Locale.US, "%.1fx", value)

    init {
        addGameNameLabel()

        val content = VisTable().apply {
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
            add(newTextButton(if (GamePref.hideKeyIndicators) "Show Keys" else "Hide Keys").also { button ->
                button.addChangeListener {
                    if (GamePref.hideKeyIndicators) {
                        GamePref.hideKeyIndicators = false
                        button.setText("Hide Keys")
                    } else {
                        button.setText("Show Keys")
                        GamePref.hideKeyIndicators = true
                    }
                }
            })
            row()

            val uiScaleLabel = newLabel("UI scale: ${scaleText(GamePref.uiScale)}")
            add(uiScaleLabel)
            row()
            add(VisSlider(1f, 3f, 0.5f, false).also { slider ->
                slider.setValue(GamePref.uiScale)
                slider.addChangeListener {
                    GamePref.uiScale = slider.value
                    uiScaleLabel.setText("UI scale: ${scaleText(slider.value)}")
                }
            }).width(250f).padBottom(10f)
            row()

            val playFieldLabel = newLabel("Play field: ${scaleText(GamePref.playFieldScale)}")
            add(playFieldLabel)
            row()
            add(VisSlider(0.5f, 2f, 0.1f, false).also { slider ->
                slider.setValue(GamePref.playFieldScale)
                slider.addChangeListener {
                    GamePref.playFieldScale = slider.value
                    playFieldLabel.setText("Play field: ${scaleText(slider.value)}")
                }
            }).width(250f).padBottom(10f)
            row()

            val buttonsLabel = newLabel("Button size: ${scaleText(GamePref.mobileButtonsScale)}")
            add(buttonsLabel)
            row()
            add(VisSlider(0.5f, 3f, 0.1f, false).also { slider ->
                slider.setValue(GamePref.mobileButtonsScale)
                slider.addChangeListener {
                    GamePref.mobileButtonsScale = slider.value
                    buttonsLabel.setText("Button size: ${scaleText(slider.value)}")
                }
                slider.addListener(object : InputListener() {
                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        buttonsPreview.isVisible = true
                        return true
                    }

                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        buttonsPreview.isVisible = false
                    }
                })
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

        content.padTop(90f).padBottom(80f)

        addActor(VisScrollPane(content).apply {
            setFillParent(true)
            setScrollingDisabled(true, false)
            setFadeScrollBars(false)
            scrollFocus = this
        })

        addActor(buttonsPreview)

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

    private inner class ButtonsPreview : Actor() {
        private val sprite = Sprite(assets.mainAtlas.findRegion("hit_button")).apply { setAlpha(0.5f) }

        init {
            isVisible = false
            touchable = Touchable.disabled
        }

        override fun draw(batch: Batch, parentAlpha: Float) {
            val stage = stage ?: return
            val unitsPerPixel = (stage.viewport as ScreenViewport).unitsPerPixel
            val scale = GamePref.mobileButtonsScale
            val size = MobileButtons.size(unitsPerPixel, scale)
            val viewportWidth = stage.viewport.worldWidth

            for (button in MobileButtons.buttons) {
                sprite.setSize(size, size)
                sprite.setPositionByCenter(
                    MobileButtons.x(button, viewportWidth, unitsPerPixel, scale),
                    MobileButtons.y(button, unitsPerPixel, scale)
                )
                sprite.draw(batch)
            }
        }
    }
}
