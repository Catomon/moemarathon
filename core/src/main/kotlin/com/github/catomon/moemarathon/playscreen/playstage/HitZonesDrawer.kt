package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.*
import com.github.catomon.moemarathon.widgets.newLabel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class HitZonesDrawer(private val playScreen: PlayScreen) : Actor() {

    private val noteRadius get() = playScreen.noteRadius
    private val hitZoneTexture = Sprite(assets.mainAtlas.findRegion("hit_zone"))
    private val keyName = newLabel("?")

    private val hitZonesStates = mutableMapOf<Int, Float>()

    override fun draw(batch: Batch, parentAlpha: Float) {
        val circleRadius = playScreen.circleRadius
        val angleBetweenParts = 360f / PlayScreen.Config.hitZonesAmount

        for (i in 0 until PlayScreen.Config.hitZonesAmount) {
            val angle = i * angleBetweenParts
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

            when (PlayScreen.Config.hitZonesAmount) {
                6 -> {
                    keyName.setText(
                        when (i) {
                            0 -> "K"
                            1 -> "J"
                            2 -> "F"
                            3 -> "D"
                            4 -> "S"
                            5 -> "L"
                            else -> "?"
                        }
                    )
                }

                8 -> {
                    //todo
                    keyName.setText(
                        when (i) {
                            0 -> "K"
                            1 -> "J"
                            2 -> "F"
                            3 -> "D"
                            4 -> "S"
                            5 -> "L"
                            else -> "?"
                        }
                    )
                }

                12 -> {
                    keyName.setText("*")
                }
            }

            keyName.pack()
            keyName.setPosByCenter(cameraX + x, cameraY + y)
            keyName.draw(batch, 1f)
        }
    }

    fun onHitZoneActivated(hitZoneId: Int) {
        if (PlayScreen.Config.hitZonesAmount <= 9) {
            hitZonesStates[hitZoneId - 1] = 1f
        }

        val camera = playScreen.camera
        playScreen.playStage.addActor(SpriteActor(Sprite(assets.mainAtlas.findRegion("star_big"))).apply {
            val size = Random.nextFloat()
            setSize(size * (sprite.width / 3) + 64, size * (sprite.height / 3) + 64)
            setPosition(camera.cornerX() + Random.nextFloat() * camera.viewportWidth, camera.cornerY() - height / 2)
            addAction(
                Actions.sequence(
                    Actions.parallel(Actions.moveBy(0f, 256f, 1f), Actions.fadeOut(1f)),
                    Actions.removeActor()
                )
            )
        })
    }
}
