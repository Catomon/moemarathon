package com.github.catomon.moemarathon.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.scene2d.actions.OneAction
import com.github.catomon.moemarathon.scene2d.actions.UpdateAction

class StageCover(alpha: Float = 1f) : Widget() {

    private val sprite = Sprite(assets.mainAtlas.findRegion("white"))

    private var afterFadeOutTime = 0f

    init {
        color.a = alpha
        sprite.color = Color.BLACK

        setFillParent(true)
    }

    fun fadeInAndThen(andThen: OneAction, duration: Float = 1f, blackTime: Float = 0f) : StageCover {
        color.a = 0f

        afterFadeOutTime = blackTime

        addAction(Actions.sequence(
            Actions.fadeIn(duration),
            UpdateAction {
                afterFadeOutTime -= it
                return@UpdateAction afterFadeOutTime <= 0
            },
            andThen))

        return this
    }

    fun fadeOutAndRemove(duration: Float = 0.6f) : StageCover {
        color.a = 1f

        addAction(Actions.sequence(Actions.fadeOut(duration), Actions.removeActor()))

        return this
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        sprite.setAlpha(color.a)
        if (stage != null)
            sprite.setSize(width, height)
        sprite.draw(batch)
    }
}
