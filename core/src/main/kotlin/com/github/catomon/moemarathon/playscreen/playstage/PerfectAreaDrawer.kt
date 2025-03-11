package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.setPosByCenter
import com.github.catomon.moemarathon.utils.setPositionByCenter
import com.github.catomon.moemarathon.widgets.newLabel
import kotlin.math.cos
import kotlin.math.sin

class PerfectAreaDrawer(private val playScreen: PlayScreen) : Actor() {

    private val noteRadius get() = playScreen.noteRadius
    private val notePlaceTexture = Sprite(assets.mainAtlas.findRegion("note_place"))
    private val keyName = newLabel("?")

    override fun draw(batch: Batch, parentAlpha: Float) {
        val circleRadius = playScreen.circleRadius
        val angleBetweenParts = 360f / PlayScreen.Config.circleParts

        // batch.draw(circleTexture, x, y, circleRadius, circleRadius)

        for (i in 0 until PlayScreen.Config.circleParts) {
            val angle = i * angleBetweenParts
            val x = circleRadius * cos(MathUtils.degRad * angle)// + playScreen.circleX
            val y = circleRadius * sin(MathUtils.degRad * angle)// + playScreen.circleY

            val spriteWidth = noteRadius * 4
            val spriteHeight = noteRadius * 4
            val cameraX = playScreen.camera.position.x
            val cameraY = playScreen.camera.position.y
            notePlaceTexture.setSize(spriteWidth, spriteHeight)
            notePlaceTexture.setOriginCenter()
            notePlaceTexture.setPositionByCenter(cameraX + x, cameraY + y)
            notePlaceTexture.draw(batch)

            keyName.setText(when (i) {
                0 -> "K"
                1 -> "J"
                2 -> "F"
                3 -> "D"
                4 -> "S"
                5 -> "L"
                else -> "?"
            })
            keyName.pack()
            keyName.setPosByCenter(cameraX + x, cameraY + y)
            keyName.draw(batch, 1f)
        }
    }


}
