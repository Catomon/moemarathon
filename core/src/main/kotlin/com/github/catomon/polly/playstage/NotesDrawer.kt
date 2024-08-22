package com.github.catomon.polly.playstage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.polly.Note
import com.github.catomon.polly.PlayScreen

class NotesDrawer(private val playScreen: PlayScreen) : Actor() {

    private val noteMap = playScreen.noteMap
    private val noteSpawnTime get() = playScreen.noteSpawnTime
    private val noteRadius get() = playScreen.noteRadius

    private val noteName = "note3"
    private val noteSprite = Sprite(Texture("textures/$noteName.png"))

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawNotes(batch)
    }

    private fun drawNotes(batch: Batch) {
        val firstNote = noteMap.chunks.lastOrNull()?.notes?.lastOrNull()

        var notes = noteMap.chunks.lastOrNull()?.notes?.toMutableList()
        if (notes != null) {
            notes = ((noteMap.chunks.elementAtOrNull(noteMap.chunks.size - 2)?.notes?.toList()
                ?: emptyList()) + notes).toMutableList()
            notes.remove(firstNote)
            val notePos = Vector2()
            for (note in notes) {
                val timeLeft = playScreen.calcNoteTimeLeft(note)
                if (timeLeft <= noteSpawnTime) {
                    note.calcPosition(notePos)

                    drawNote(batch, notePos, timeLeft)

                    if (note.tracingNext) {

                        val nextNotePos = notes.getOrNull(notes.indexOf(note) - 1)?.calcPosition(Vector2())
                        if (nextNotePos != null) {
                            //shapes.line(notePos.x, notePos.y, nextNotePos.x, nextNotePos.y)
                        }
                    }
                }
            }
        }

        if (firstNote != null) {
            drawNote(batch, firstNote.calcPosition(Vector2()), playScreen.calcNoteTimeLeft(firstNote), Color.YELLOW)
        }
    }

    private fun drawNote(batch: Batch, notePos: Vector2, timeLeft: Float, color: Color? = null) {
        noteSprite.setSize(noteRadius * 2, noteRadius * 2)
        noteSprite.setPosition(notePos.x - noteSprite.width / 2, notePos.y - noteSprite.height / 2)
        noteSprite.setAlpha(0f)
        var a = (noteSpawnTime - timeLeft) / (noteSpawnTime * 0.1f)
        if (a > 1) a = 1f
        noteSprite.color = color ?: Color.WHITE
        noteSprite.setAlpha(a)
        noteSprite.draw(batch)
    }

    fun Note.calcPosition(vector2: Vector2): Vector2 = playScreen.calcNotePosition(this, vector2)
}
