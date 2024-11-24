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

        if (stage.width < stage.height) {
            if (sprite.height != stage.height) {
                val ratio = stage.height / sprite.texture.height
                sprite.setSize(sprite.texture.width * ratio, sprite.texture.height * ratio)
            }
        } else {
            if (sprite.width != stage.width) {
                val ratio = stage.width / sprite.texture.width
                sprite.setSize(sprite.texture.width * ratio, sprite.texture.height * ratio)
            }
        }

        sprite.setPositionByCenter(stage.worldCenterX, stage.worldCenterY)
        sprite.draw(batch)
    }
}
