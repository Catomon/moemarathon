package com.github.catomon.polly

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.github.catomon.polly.GameMain.Companion.screenHeight
import com.github.catomon.polly.GameMain.Companion.screenWidth

class DebugRenderer(val playScreen: PlayScreen, val shapes: ShapeRenderer) {

    fun draw() {
        playScreen.apply {

            val cameraX = camera.position.x
            val cameraY = camera.position.y

            shapes.projectionMatrix = camera.combined
            shapes.begin()

            shapes.color = Color.LIGHT_GRAY
            shapes.line(pointerX, 0f, pointerX, screenHeight.toFloat())
            shapes.line(0f, pointerY, screenWidth.toFloat(), pointerY)

            shapes.color = Color.LIGHT_GRAY
            shapes.rect(cameraX - mapSize / 2, cameraY - mapSize / 2, mapSize, mapSize)

            shapes.color = Color.WHITE
            shapes.circle(cameraX, cameraY, circleRadius)
            shapes.color = Color.GRAY
            shapes.circle(cameraX, cameraY, missClickRad)
            shapes.circle(cameraX, cameraY, earlyClickRad)

            val clickerPos = calcClickerPos(Vector2())
            shapes.color = Color.LIGHT_GRAY
            shapes.circle(clickerPos.x, clickerPos.y, pointerSize * (mapSize / 2))
            //        shapes.circle(pointerX, pointerY, pointerSize * (mapSize / 2))

            val notes = noteMap.chunks.lastOrNull()?.notes?.toMutableList()
            if (notes != null) {
                notes.addAll(noteMap.chunks.elementAtOrNull(noteMap.chunks.size - 2)?.notes ?: emptyList())
                val notePos = Vector2()
                for (note in notes) {
                    val timeLeft = note.timing - time - noteClickTimeWindow
                    if (timeLeft <= noteSpawnTime) {
                        note.calcPosition(notePos)
                        shapes.color =
                            if (note.tracingNext)
                                Color.GREEN
                            else
                                if (note.tracingPrev) Color.RED
                                else Color.WHITE //colors.getOrNull(note.color) ?: Color.GREEN
                        shapes.point(notePos.x, notePos.y, 0f)
                        shapes.circle(notePos.x, notePos.y, noteRadius)

                        if (note.tracingNext) {

                            val nextNotePos = notes.getOrNull(notes.indexOf(note) - 1)?.calcPosition(Vector2())
                            if (nextNotePos != null)
                                shapes.line(notePos.x, notePos.y, nextNotePos.x, nextNotePos.y)
                        }
                    }
                }
            }

            shapes.end()
        }

    }
}
