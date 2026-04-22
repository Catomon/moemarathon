package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.playscreen.getHitZoneKeyById
import com.github.catomon.moemarathon.utils.setPosByCenter
import com.github.catomon.moemarathon.utils.setPositionByCenter
import com.github.catomon.moemarathon.widgets.newLabel
import kotlin.math.cos
import kotlin.math.sin

class HitZonesDrawer(private val playScreen: PlayScreen) : Actor() {

    private val playStage get() = playScreen.playStage
    private val noteRadius get() = playScreen.noteRadius
    private val hitZoneTexture = Sprite(assets.mainAtlas.findRegion("hit_zone"))
    private val keyName = newLabel("")

    private val hitZonesStates = mutableMapOf<Int, Float>()

    private val drawKeyIndicators = !GamePref.hideKeyIndicators

    init {
        keyName.setFontScale(0.75f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val circleRadius = playScreen.hitZoneCircleRadius
        val angleBetweenParts = 360f / PlayScreen.GameplayConfig.hitZonesAmount

        for (i in 0 until PlayScreen.GameplayConfig.hitZonesAmount) {
            var angle = i * angleBetweenParts

            if (PlayScreen.GameplayConfig.hitZonesAmount <= 6)
                if (angle != 0f && angle != 180f) {
                    when {
                        angle < 90f -> angle -= 21f
                        angle > 270f -> angle += 21f
                        angle > 180f -> angle -= 21f
                        angle > 90f -> angle += 21f
                    }
                }

            val x = circleRadius * cos(MathUtils.degRad * angle)
            val y = circleRadius * sin(MathUtils.degRad * angle)

            val spriteWidth = noteRadius * 4
            val spriteHeight = noteRadius * 4
            val cameraX = playScreen.camera.position.x
            val cameraY = playScreen.camera.position.y

            val hitZoneStateVal = hitZonesStates[i]
            if (hitZoneStateVal != null && hitZoneStateVal > 0f) {
                hitZonesStates[i] = hitZoneStateVal - Gdx.graphics.deltaTime * 4
                hitZoneTexture.setSize(
                    spriteWidth + hitZoneStateVal * (spriteWidth * 0.20f),
                    spriteHeight + hitZoneStateVal * (spriteHeight * 0.20f)
                )
                if (hitZonesStates[i]!! < 0f) hitZonesStates[i] = 0f
            } else {
                hitZoneTexture.setSize(spriteWidth, spriteHeight)
            }

            hitZoneTexture.setOriginCenter()
            hitZoneTexture.setPositionByCenter(cameraX + x, cameraY + y)
            hitZoneTexture.draw(batch)

            if (PlayScreen.GameplayConfig.playMethod == PlayScreen.PlayMethod.POINTER || !drawKeyIndicators) {
                keyName.setText("")
            } else {
                when (PlayScreen.GameplayConfig.hitZonesAmount) {
                    6 -> {
                        if (!Config.IS_MOBILE)
                            keyName.setText(
                                getHitZoneKeyById(i)
                            )
                    }

                    8 -> {
                        //todo
                        keyName.setText(
                            getHitZoneKeyById(i)
                        )
                    }

                    12 -> {
                        keyName.setText("")
                    }
                }
            }

            keyName.pack()
            keyName.setPosByCenter(cameraX + x, cameraY + y)
            keyName.draw(batch, 1f)
        }
    }

    /* animates a hit zone */
    fun animateHitZone(hitZoneId: Int) {
//        if (PlayScreen.GameplayConfig.hitZonesAmount <= 9) {
        hitZonesStates[hitZoneId - 1] = 1f
//        }

        playStage.playStarEffect()
    }
}
