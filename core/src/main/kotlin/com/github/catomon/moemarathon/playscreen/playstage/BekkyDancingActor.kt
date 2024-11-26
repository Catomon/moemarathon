package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.RegionAnimation

class BekkyDancingActor(private val playScreen: PlayScreen) : Actor(), NoteListener {

    private val bekkyAni = RegionAnimation(0.10125f, assets.mainAtlas.findRegions("bekkydancing/frame"))
    private val bekkySprite = Sprite(bekkyAni.currentFrame)

    private val clickZoneSprite = Sprite(assets.mainAtlas.findRegion("bekky_click_zone"))

    init {
        playScreen.noteListeners.add(this)
    }

    override fun act(delta: Float) {
        super.act(delta)

        bekkyAni.update(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        playScreen.apply {
            val cameraX = camera.position.x
            val cameraY = camera.position.y
            clickZoneSprite.setSize(circleRadius * 2 + noteRadius * 2, circleRadius * 2 + noteRadius * 2)
            clickZoneSprite.setPosition(cameraX - clickZoneSprite.width / 2, cameraY - clickZoneSprite.height / 2)
//            clickZoneSprite.setAlpha(0.5f)
            clickZoneSprite.draw(batch)

            bekkySprite.setSize((circleRadius + noteRadius) * 0.9f, (circleRadius + noteRadius) * 0.9f)
            bekkySprite.setPosition(cameraX - bekkySprite.width / 2, cameraY - bekkySprite.height / 2)
            bekkySprite.setRegion(bekkyAni.currentFrame)
            bekkySprite.draw(batch)
        }
    }

    override fun onNoteEvent(id: Int, note: Note) {

    }
}
