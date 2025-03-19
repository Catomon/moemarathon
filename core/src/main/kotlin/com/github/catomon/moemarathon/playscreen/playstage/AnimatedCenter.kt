package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.RegionAnimation

class AnimatedCenter(private val playScreen: PlayScreen, aniName: String = "bekkydancing/frame", timingsCircleName: String = "bekky_click_zone") : Actor(), NoteListener {

    private val bekkyAni = RegionAnimation(0.10125f, assets.mainAtlas.findRegions(aniName))
    private val bekkySprite = Sprite(bekkyAni.currentFrame)

    private val timingsCircleSprite = Sprite(assets.mainAtlas.findRegion(timingsCircleName))

    init {
        playScreen.noteListeners.add(this)
//        timingsCircleSprite.setAlpha(0.5f)
    }

    override fun act(delta: Float) {
        super.act(delta)

        bekkyAni.update(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        playScreen.apply {
            val cameraX = camera.position.x
            val cameraY = camera.position.y
            timingsCircleSprite.setSize(hitZoneCircleRadius * 2 + noteRadius * 2, hitZoneCircleRadius * 2 + noteRadius * 2)
            timingsCircleSprite.setPosition(cameraX - timingsCircleSprite.width / 2, cameraY - timingsCircleSprite.height / 2)
//            clickZoneSprite.setAlpha(0.5f)
            timingsCircleSprite.draw(batch)

            bekkySprite.setSize((hitZoneCircleRadius + noteRadius) * 0.9f, (hitZoneCircleRadius + noteRadius) * 0.9f)
            bekkySprite.setPosition(cameraX - bekkySprite.width / 2, cameraY - bekkySprite.height / 2)
            bekkySprite.setRegion(bekkyAni.currentFrame)
            bekkySprite.draw(batch)
        }
    }

    override fun onNoteEvent(id: Int, note: Note) {

    }
}
