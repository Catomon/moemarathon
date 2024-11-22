package com.github.catomon.polly.playscreen.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.polly.utils.setPositionByCenter
import com.github.catomon.polly.utils.worldCenterX
import com.github.catomon.polly.utils.worldCenterY

class BackgroundActor(pSprite: Sprite? = null) : Actor() {

    var sprite: Sprite? = pSprite
        set(value) {
            field = value
            field?.setAlpha(0.25f)
        }

    init {
        sprite?.setAlpha(0.25f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val sprite = sprite
        if (sprite == null || sprite.texture == null) return

        if (sprite.height != stage.height) {
            val ratio = stage.height / sprite.height
            sprite.setSize(sprite.width * ratio, sprite.height * ratio)
            if (sprite.width <= 0f || sprite.height <= 0f) {
                sprite.setSize(1f, 1f)
            }
        }

        sprite.setPositionByCenter(stage.worldCenterX, stage.worldCenterY)
        sprite.draw(batch)
    }
}
