package com.github.catomon.moemarathon.playscreen.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.playscreen.getHitZoneIdByButton
import com.github.catomon.moemarathon.playscreen.getHitZoneKeyById
import com.github.catomon.moemarathon.utils.setPosByCenter
import com.github.catomon.moemarathon.utils.setPositionByCenter
import com.github.catomon.moemarathon.widgets.newLabel
import kotlin.math.pow

class MobileButtonsLayout(private val playScreen: PlayScreen) : Actor() {
    private lateinit var camera: Camera
    private lateinit var viewport: Viewport

    private val buttonSprite = Sprite(assets.mainAtlas.findRegion("hit_button")).apply {
        this.setAlpha(0.5f)
    }

    inner class HitButton(val data: MobileButton) {
        var size: Float = 1f

        val key get() = data.key
        val x get() = MobileButtons.x(data, camera.viewportWidth, unitsPerPixel, GamePref.mobileButtonsScale, GamePref.mobileButtonsPosition)
        val y get() = MobileButtons.y(data, unitsPerPixel, GamePref.mobileButtonsScale)
    }

    private val unitsPerPixel get() = (viewport as ScreenViewport).unitsPerPixel

    private val keyName = newLabel("")

    private val activePointers = mutableMapOf<Int, Int>()

    private val buttons = MobileButtons.buttons.map { HitButton(it) }

    fun isPointInCircle(pointX: Float, pointY: Float, centerX: Float, centerY: Float, radius: Float): Boolean {
        return (pointX - centerX).pow(2) + (pointY - centerY).pow(2) <= radius.pow(2)
    }

    private fun buttonCenter(button: HitButton): Pair<Float, Float> {
        return button.x to button.y
    }

    override fun act(delta: Float) {
        super.act(delta)

        for (button in buttons) {
            if (button.size > 1f) {
                button.size -= Gdx.graphics.deltaTime

                if (button.size < 1f)
                    button.size = 1f
            }
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        val scale = GamePref.mobileButtonsScale

        buttons.forEach { button ->
            val size = MobileButtons.size(unitsPerPixel, scale) * button.size
            buttonSprite.setSize(size, size)

            val (buttonX, buttonY) = buttonCenter(button)

            buttonSprite.setPositionByCenter(buttonX, buttonY)
            buttonSprite.draw(batch)

            if (!Config.IS_MOBILE) {
                keyName.setText(
                    getHitZoneKeyById(getHitZoneIdByButton(button.key) - 1)
                )
            }

            keyName.pack()
            keyName.setPosByCenter(buttonX, buttonY)
            keyName.draw(batch, buttonSprite.color.a)
        }
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)

        if (stage is PlayHud) {
            camera = stage.camera
            viewport = stage.viewport

            stage.addListener(object : InputListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)

                    val key = activePointers.remove(pointer) ?: return

                    if (!playScreen.paused && playScreen.isHoldingNote && playScreen.holdNoteButton == key) {
                        playScreen.processButtonDown(key)
                    }
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    for (hitButton in buttons) {
                        val (btnX, btnWorldY) = buttonCenter(hitButton)
                        if (isPointInCircle(
                                x,
                                y,
                                btnX,
                                btnWorldY,
                                MobileButtons.TOUCH_RADIUS * GamePref.mobileButtonsScale * unitsPerPixel
                            )
                        ) {
                            hitButton.size = 1.25f
                            activePointers[pointer] = hitButton.key
                            playScreen.processButtonDown(hitButton.key)
                            return true
                        }
                    }
                    return false
                }
            })
        } else {
            throw IllegalStateException("Stage is not PlayHud")
        }
    }
}
