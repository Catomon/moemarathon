package com.github.catomon.moemarathon.playscreen.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.TimeUtils
import com.github.catomon.moemarathon.AudioManager
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import kotlin.math.max

class HealthBar(
    private val healthHit: Float = 15f,
    private val healthMiss: Float = 10f,
    private val onGameOver: () -> Unit
) : Actor(), NoteListener {

    private val scaleUp = 0.1f

    private var targetHealth = 100f
    private var visualHealth = 100f
    private var lastUpdateTime = TimeUtils.nanoTime()
    private val smoothSpeed = 5f

    private val heartImage = Sprite(assets.mainAtlas.findRegion("heart"))
    private val heartSize = 64f

    init {
        heartImage.setSize(heartSize, heartSize)
        heartImage.setOriginCenter()
        setSize(heartSize * 10f, heartSize)
        setOrigin(Align.center)
    }

    override fun act(delta: Float) {
        super.act(delta)
        updateVisualHealth(delta)
    }

    private fun updateVisualHealth(delta: Float) {
        val currentTime = TimeUtils.nanoTime()
        val dt = (currentTime - lastUpdateTime) / 1_000_000_000f
        lastUpdateTime = currentTime

        if (visualHealth != targetHealth) {
            visualHealth += (targetHealth - visualHealth) * smoothSpeed * dt
            if (Math.abs(visualHealth - targetHealth) < 0.1f) {
                visualHealth = targetHealth
            }
        }
    }

    override fun scaleChanged() {

    }

    override fun setScaleX(scaleX: Float) {
        heartImage.setScale(scaleX, heartImage.scaleY)
    }

    override fun setScaleY(scaleY: Float) {
        heartImage.setScale(heartImage.scaleX, scaleY)
    }

    override fun setScale(scaleXY: Float) {
        heartImage.setScale(scaleXY)
    }

    override fun setScale(scaleX: Float, scaleY: Float) {
        heartImage.setScale(scaleX, scaleY)
    }

    override fun scaleBy(scaleX: Float, scaleY: Float) {
        if (scaleX != 0f || scaleY != 0f) {
            val nX = heartImage.scaleX + scaleX
            val nY = heartImage.scaleY + scaleY
            heartImage.setScale(nX, nY)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        val numFullHearts = visualHealth.toInt() / 10
        val partialAlpha = (visualHealth % 10f) / 10f

        var heartX = x
        for (i in 0 until 10) {
            heartImage.setPosition(heartX, y)
            heartImage.setAlpha(if (i < numFullHearts) 1f else if (i == numFullHearts) partialAlpha else 0f)
            heartImage.draw(batch)
            heartX += heartSize
        }
    }

    override fun onNoteEvent(id: Int, note: Note) {
        when (id) {
            NoteListener.HIT, NoteListener.HIT_HOLD_NOTE, NoteListener.HOLD_NOTE_START -> {
                if (targetHealth < 100f && targetHealth > 0f) {
                    targetHealth += healthHit
                    if (targetHealth > 100f) targetHealth = 100f

                    addAction(
                        Actions.sequence(
                            Actions.scaleBy(scaleUp, scaleUp, 0.1f, Interpolation.fade),
                            Actions.scaleBy(-scaleUp, -scaleUp, 0.1f, Interpolation.smooth)
                        )
                    )
                }
            }

            NoteListener.MISS -> {
                if (targetHealth > 0f) {
                    targetHealth -= healthMiss
                    targetHealth = max(0f, targetHealth)

                    addAction(
                        Actions.sequence(
                            Actions.scaleBy(-scaleUp, -scaleUp, 0.1f, Interpolation.fade),
                            Actions.scaleBy(scaleUp, scaleUp, 0.1f, Interpolation.smooth)
                        )
                    )

                    if (targetHealth <= 0f) {
                        onGameOver.invoke()

                        AudioManager.playSound(AudioManager.lost)
                    }
                }
            }
        }
    }
}
