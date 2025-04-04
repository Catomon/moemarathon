package com.github.catomon.moemarathon.ui

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.catomon.moemarathon.utils.RegionAnimation
import com.github.catomon.moemarathon.utils.SpriteActor

class AnimationActor(val regionAnimation: RegionAnimation) : SpriteActor(Sprite(regionAnimation.currentFrame)) {

    init {
        regionAnimation.playMode = Animation.PlayMode.NORMAL
    }

    override fun act(delta: Float) {
        super.act(delta)

        regionAnimation.update(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        sprite.setRegion(regionAnimation.currentFrame)

        super.draw(batch, parentAlpha)

        if (regionAnimation.progress >= 1f) remove()
    }
}
