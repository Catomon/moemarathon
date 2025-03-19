package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.PlayScreen

class JustCircleCenter(private val playScreen: PlayScreen, timingsCircleName: String = "bekky_click_zone") : Actor(), NoteListener {

    private val clickZoneSprite = Sprite(assets.mainAtlas.findRegion(timingsCircleName))

    init {
        playScreen.noteListeners.add(this)
        clickZoneSprite.setAlpha(0.5f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        playScreen.apply {
            val cameraX = camera.position.x
            val cameraY = camera.position.y
            clickZoneSprite.setSize(hitZoneCircleRadius * 2 + noteRadius * 2, hitZoneCircleRadius * 2 + noteRadius * 2)
            clickZoneSprite.setPosition(cameraX - clickZoneSprite.width / 2, cameraY - clickZoneSprite.height / 2)
            clickZoneSprite.draw(batch)
        }
    }

    override fun onNoteEvent(id: Int, note: Note) {

    }
}
