package com.github.catomon.polly.playstage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.polly.Note
import com.github.catomon.polly.PlayScreen
import com.github.catomon.polly.assets
import com.github.catomon.polly.utils.degrees
import com.github.catomon.polly.utils.setPositionByCenter

class NotesDrawer(private val playScreen: PlayScreen) : Actor() {

    private val noteMap = playScreen.noteMap
    private val noteSpawnTime get() = playScreen.noteSpawnTime
    private val noteRadius get() = playScreen.noteRadius

    private val noteName = "note2"
    private val noteOuterTex = assets.mainAtlas.findRegion(noteName + "_outer")
    private val noteInnerTex = assets.mainAtlas.findRegion(noteName + "_inner")
    private val noteTraceTex = assets.mainAtlas.findRegion(noteName + "_trace")
    private val pointerTraceTex = assets.mainAtlas.findRegion(noteName + "_pointer_trace")

    private val noteInnerSprite = Sprite(noteInnerTex)
    private val noteOuterSprite = Sprite(noteOuterTex)
    private val noteTraceSprite = Sprite(noteTraceTex)
    private val pointerTraceSprite = Sprite(pointerTraceTex)

    private val outerColor = Color.RED.cpy()
    private val innerColor = Color.DARK_GRAY.cpy()
    private val traceColor = Color.RED.cpy()
    private val nextColor = Color.YELLOW.cpy()

    private val applyColor = false

    init {
        if (applyColor) {
            noteInnerSprite.color = innerColor
            pointerTraceSprite.color = traceColor
            noteOuterSprite.color = outerColor
            noteTraceSprite.color = traceColor
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawNotes(batch)
    }

    private fun drawNotes(batch: Batch) {
        val spriteWidth = noteRadius * 4
        val spriteHeight = noteRadius * 4
        noteInnerSprite.setSize(spriteWidth, spriteHeight)
        noteInnerSprite.setOriginCenter()
        noteOuterSprite.setSize(spriteWidth, spriteHeight)
        noteOuterSprite.setOriginCenter()
        noteTraceSprite.setSize(spriteWidth, spriteHeight)
        noteTraceSprite.setOriginCenter()
        pointerTraceSprite.setSize(spriteWidth, spriteHeight)
        pointerTraceSprite.setOriginCenter()

        val firstNote = noteMap.chunks.lastOrNull()?.notes?.lastOrNull() ?: return
        val firstNotePos = firstNote.calcPosition(Vector2())

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

                    when {
                        note.tracingNext -> {
                            val nextNotePos = notes.getOrNull(notes.indexOf(note) - 1)?.calcPosition(Vector2())
                            if (nextNotePos != null) {
                                drawNote(batch, notePos, timeLeft, traceToNote = nextNotePos)
                            } else {
                                drawNote(batch, notePos, timeLeft)
                            }
                        }

                        note.tracingPrev -> {
                            val prevNotePos =
                                notes.getOrNull(notes.indexOf(note) + 1)?.calcPosition(Vector2()) ?: firstNotePos
                            drawNote(batch, notePos, timeLeft, traceToNote = prevNotePos)
                        }

                        else -> {
                            drawNote(batch, notePos, timeLeft)
                        }
                    }
                }
            }

            val timeLeft = playScreen.calcNoteTimeLeft(firstNote)

            if (firstNote.tracingNext) {
                val nextNotePos = notes.lastOrNull()?.calcPosition(Vector2())
                if (nextNotePos != null) {
                    drawNote(batch, firstNotePos, timeLeft, nextColor, nextNotePos)
                } else {
                    drawNote(batch, firstNotePos, timeLeft, nextColor)
                }
            } else {
                if (playScreen.isTracing) {
                    val pointer = playScreen.getPointer()

                    when {
                        firstNote.tracingPrev -> {
                            val prevNotePos = Vector2(pointer.x, pointer.y)
                            drawNote(batch, firstNotePos, timeLeft, nextColor, prevNotePos)
                        }

                        else -> {
                            drawNote(batch, firstNotePos, timeLeft, nextColor)
                        }
                    }

                    pointerTraceSprite.setPositionByCenter(pointer.x, pointer.y)
                    pointerTraceSprite.rotation = degrees(pointer.x, pointer.y, firstNotePos.x, firstNotePos.y)
                    pointerTraceSprite.draw(batch)
                } else {
                    drawNote(batch, firstNotePos, timeLeft, nextColor)
                }
            }
        }
    }

    private fun drawNote(
        batch: Batch,
        notePos: Vector2,
        timeLeft: Float,
        color: Color? = null,
        traceToNote: Vector2? = null
    ) {
        noteInnerSprite.setPositionByCenter(notePos.x, notePos.y)
        noteOuterSprite.setPositionByCenter(notePos.x, notePos.y)

        if (applyColor)
            noteOuterSprite.color = color ?: outerColor

        noteInnerSprite.setAlpha(0f)
        noteOuterSprite.setAlpha(0f)

        var a = (noteSpawnTime - timeLeft) / (noteSpawnTime * 0.1f)
        if (a > 1) a = 1f

        noteOuterSprite.setAlpha(a)
        noteInnerSprite.setAlpha(a)

        if (traceToNote != null) {
            noteTraceSprite.setPositionByCenter(notePos.x, notePos.y)
            noteTraceSprite.rotation = degrees(notePos.x, notePos.y, traceToNote.x, traceToNote.y)
            noteTraceSprite.setAlpha(a)
            noteTraceSprite.draw(batch)
        }

        noteOuterSprite.draw(batch)
        noteInnerSprite.draw(batch)
    }

    fun Note.calcPosition(vector2: Vector2): Vector2 = playScreen.calcNotePosition(this, vector2)
}
