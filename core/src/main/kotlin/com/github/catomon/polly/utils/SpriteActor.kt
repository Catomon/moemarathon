package com.github.catomon.moemarathon.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

open class SpriteActor(
    val sprite: Sprite = Sprite()
) : Actor() {

    override fun draw(batch: Batch?, parentAlpha: Float) {
        sprite.setAlpha(color.a)
        sprite.draw(batch)
    }

    override fun setColor(color: Color?) {
        super.setColor(color)
    }

    override fun sizeChanged() {
        sprite.setSize(width, height)
        sprite.setPosition(x - width / 2, y - height / 2)
        sprite.setOriginCenter()
    }

    override fun positionChanged() {
        sprite.setPosition(x - width / 2, y - height / 2)
    }

    override fun rotationChanged() {
        sprite.rotation = rotation
    }
}
