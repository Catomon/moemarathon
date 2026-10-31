package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.playscreen.getHitZoneKeyById
import com.github.catomon.moemarathon.utils.setPosByCenter
import com.github.catomon.moemarathon.utils.setPositionByCenter
import com.github.catomon.moemarathon.widgets.newLabel

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
        val zones = playScreen.getHitZonesForRender()
        val baseSize = noteRadius * 4f

        for ((id, pos) in zones) {
            val stateIndex = id - 1
            val hitZoneStateVal = hitZonesStates[stateIndex]
            if (hitZoneStateVal != null && hitZoneStateVal > 0f) {
                hitZonesStates[stateIndex] = hitZoneStateVal - Gdx.graphics.deltaTime * 4
                hitZoneTexture.setSize(
                    baseSize + hitZoneStateVal * (baseSize * 0.20f),
                    baseSize + hitZoneStateVal * (baseSize * 0.20f)
                )
                if (hitZonesStates[stateIndex]!! < 0f) hitZonesStates[stateIndex] = 0f
            } else {
                hitZoneTexture.setSize(baseSize, baseSize)
            }

            hitZoneTexture.setOriginCenter()
            hitZoneTexture.setPositionByCenter(pos.x, pos.y)
            hitZoneTexture.draw(batch)

            if (PlayScreen.GameplayConfig.playMethod == PlayScreen.PlayMethod.POINTER || !drawKeyIndicators) {
                keyName.setText("")
            } else {
                when (PlayScreen.GameplayConfig.hitZonesAmount) {
                    6 -> {
                        if (!Config.IS_MOBILE)
                            keyName.setText(
                                getHitZoneKeyById(id - 1)
                            )
                    }

                    8 -> {
                        //todo
                        keyName.setText(
                            getHitZoneKeyById(id - 1)
                        )
                    }

                    12 -> {
                        keyName.setText("")
                    }
                }
            }

            keyName.pack()
            keyName.setPosByCenter(pos.x, pos.y)
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
