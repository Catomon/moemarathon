package com.github.catomon.polly.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.polly.utils.setPositionByCenter
import com.github.catomon.polly.utils.worldCenterX
import com.github.catomon.polly.utils.worldCenterY

class BackgroundActor(private val sprite: Sprite) : Actor() {

    init {
        sprite.setAlpha(0f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (sprite.height != stage.height) {
            val ratio = stage.height / sprite.height
            sprite.setSize(sprite.width * ratio, sprite.height * ratio)
        }

        sprite.setPositionByCenter(stage.worldCenterX, stage.worldCenterY)
        sprite.draw(batch)
    }
}
