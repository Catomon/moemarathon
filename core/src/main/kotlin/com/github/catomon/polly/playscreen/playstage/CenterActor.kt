package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.scene2d.AnimationActor
import com.github.catomon.moemarathon.utils.*
import kotlin.random.Random

class CenterActor(private val playScreen: PlayScreen) : Actor(), NoteListener {

    private val topReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_top"))
    private val bottomReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_bottom"))
    private val leftReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_left"))
    private val rightReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_right"))
    private val topLeftReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_top_left"))
    private val topRightReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_top_right"))
    private val bottomLeftReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_bottom_left"))
    private val bottomRightReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_bottom_right"))

    private val missReg = RegionAnimation(0.35f, assets.mainAtlas.findRegions("center_miss"))

    private val centerReg = RegionAnimation(0.5f, assets.mainAtlas.findRegions("center"))

    private val clickZoneSprite = Sprite(assets.mainAtlas.findRegion("click_zone3")) //todo skin
    private val centerSprite = AnimatedSprite(centerReg)

    private val centerMagicRegs = assets.mainAtlas.findRegions("center_magic")
    private val centerSize = centerReg.currentFrame.regionWidth

    private val regTime = 1f
    private var currRegTime = 0f

    init {
        playScreen.noteListeners.add(this)
    }

    override fun act(delta: Float) {
        super.act(delta)

        if (playScreen.skinName == "default") return

        centerSprite.update(delta)

        currRegTime += delta
        if (currRegTime >= regTime) {
            centerSprite.setRegion(centerReg)
        }
    }

    private fun setCenterReg(reg: RegionAnimation) {
        centerSprite.setRegion(reg)
        currRegTime = 0f
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        playScreen.apply {
            val cameraX = camera.position.x
            val cameraY = camera.position.y
            clickZoneSprite.setSize(circleRadius * 2 + noteRadius * 2, circleRadius * 2 + noteRadius * 2)
            clickZoneSprite.setPosition(cameraX - clickZoneSprite.width / 2, cameraY - clickZoneSprite.height / 2)
            clickZoneSprite.setAlpha(0.5f)
            clickZoneSprite.draw(batch)

            if (playScreen.skinName == "default") return
            //todo only on resize
            centerSprite.setSize(circleRadius * 1.5f, circleRadius * 1.5f)
            centerSprite.setPosition(cameraX - centerSprite.width / 2, cameraY - centerSprite.height / 2)
            centerSprite.draw(batch)
        }
    }

    override fun onNoteEvent(id: Int, note: Note) {
        if (playScreen.skinName == "default") return

        when (id) {
            1, 7 -> {
                val centerX = centerSprite.centerX()
                val centerY = centerSprite.centerY()
                val notePos = playScreen.calcNotePosition(note)
                val degrees = degrees(centerX, centerY, notePos.x, notePos.y).toInt()  // -180/180 <- 90 ^ -90 v -> 0
                val right = degrees in -68..68
                val left = degrees < -112 || degrees > 112
                val top = degrees in 22..158
                val bottom = degrees in -158..-22
                setCenterReg(
                    when {
                        top && right -> topRightReg
                        top && left -> topLeftReg
                        bottom && right -> bottomRightReg
                        bottom && left -> bottomLeftReg
                        top -> topReg
                        bottom -> bottomReg
                        left -> leftReg
                        right -> rightReg
                        else -> centerReg
                    }
                )

                val magicBurst = AnimationActor(RegionAnimation(0.06f, centerMagicRegs))
                magicBurst.setSize(playScreen.circleRadius * 2, playScreen.circleRadius * 2)
                fun AnimationActor.setPos(x: Float, y: Float) {
                    this.setPosition(
                        centerSprite.x + x / centerSize * (playScreen.circleRadius * 2),
                        centerSprite.y + y / centerSize * (playScreen.circleRadius * 2)
                    )
                }
                when {
                    top && right -> magicBurst.setPos(32f, 29f)
                    top && left -> magicBurst.setPos(14f, 29f)
                    bottom && right -> magicBurst.setPos(33f, 14f)
                    bottom && left -> magicBurst.setPos(14f, 13f)
                    top -> magicBurst.setPos(29f, 30f)
                    bottom -> magicBurst.setPos(27f, 10f)
                    left -> magicBurst.setPos(12f, 25f)
                    right -> magicBurst.setPos(34f, 25f)
                    else -> magicBurst.setPos(0f, 0f)
                }
                magicBurst.rotation = Random.nextFloat() * 360
                magicBurst.addAction(
                    Actions.sequence(
                        Actions.parallel(
                            Actions.fadeOut(0.3f),
                            Actions.sizeBy(playScreen.noteRadius * 1.75f, playScreen.noteRadius * 1.75f, 0.3f)
                        ),
                        Actions.removeActor()
                    )
                )
                stage?.addActor(magicBurst)
            }

            0 -> {
                setCenterReg(missReg)
            }
        }
    }
}
