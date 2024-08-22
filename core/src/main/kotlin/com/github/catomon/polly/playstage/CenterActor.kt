package com.github.catomon.polly.playstage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.polly.PlayScreen

class CenterActor(private val playScreen: PlayScreen) : Actor() {

    private val clickZoneSprite = Sprite(Texture("textures/click_zone.png"))
    private val centerSprite = Sprite(Texture("textures/center.png"))

    override fun draw(batch: Batch?, parentAlpha: Float) {
        playScreen.apply {
            val cameraX = camera.position.x
            val cameraY = camera.position.y
            clickZoneSprite.setSize(circleRadius * 2 + noteRadius * 2, circleRadius * 2 + noteRadius * 2)
            clickZoneSprite.setPosition(cameraX - clickZoneSprite.width / 2, cameraY - clickZoneSprite.height / 2)
            clickZoneSprite.setAlpha(0.5f)
            clickZoneSprite.draw(batch)

            //todo only on resize
            centerSprite.setPosition(cameraX - centerSprite.width / 2, cameraY - centerSprite.height / 2)
            centerSprite.draw(batch)
        }
    }
}
