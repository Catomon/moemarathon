package com.github.catomon.moemarathon.playscreen.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.playscreen.getHitZoneIdByButton
import com.github.catomon.moemarathon.playscreen.getHitZoneKeyById
import com.github.catomon.moemarathon.utils.setPosByCenter
import com.github.catomon.moemarathon.utils.setPositionByCenter
import com.github.catomon.moemarathon.widgets.newLabel
import kotlin.math.cos
import kotlin.math.pow

class MobileButtonsLayout(private val playScreen: PlayScreen) : Actor() {
    private lateinit var camera: Camera
    private lateinit var viewport: Viewport

    private val buttonsFromBottom = 320f

    private val circleRadius get() = camera.viewportWidth / 2 - 120f

    private val buttonSprite = Sprite(assets.mainAtlas.findRegion("hit_button")).apply {
        this.setAlpha(0.5f)
    }
    private val buttonSize = 200f

    inner class HitButton(val key: Int, var angle: Float, var xOff: Float = 0f, var yOffRel: Float = 0f) {
        var size: Float = 1f

        val x get() = circleRadius * cos(MathUtils.degRad * angle) + xOff
        val y get() = buttonsFromBottom + yOffRel
    }

    private val keyName = newLabel("")

    private val buttons = listOf(
        HitButton(key = Input.Keys.F, angle = 183f, xOff = 0f, yOffRel = 215f),
        HitButton(key = Input.Keys.D, angle = 183f, xOff = 0f, yOffRel = 0f),
        HitButton(key = Input.Keys.S, angle = 183f, xOff = 75f, yOffRel = -200f),

        HitButton(key = Input.Keys.J, angle = -3f, xOff = 0f, yOffRel = 215f),
        HitButton(key = Input.Keys.K, angle = -3f, xOff = 0f, yOffRel = 0f),
        HitButton(key = Input.Keys.L, angle = -3f, xOff = -75f, yOffRel = -200f),
    )

    fun isPointInCircle(pointX: Float, pointY: Float, centerX: Float, centerY: Float, radius: Float): Boolean {
        return (pointX - centerX).pow(2) + (pointY - centerY).pow(2) <= radius.pow(2)
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

        buttons.forEach { button ->
            buttonSprite.setSize(buttonSize * button.size, buttonSize * button.size)

            val buttonX = camera.position.x + button.x
            val buttonY = button.y

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
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    for (hitButton in buttons) {
                        val btnWorldY = hitButton.y
                        if (isPointInCircle(
                                x,
                                y,
                                hitButton.x + camera.viewportWidth / 2,
                                btnWorldY,
                                90f
                            )
                        ) {
                            hitButton.size = 1.25f
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
