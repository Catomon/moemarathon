package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.degrees
import com.github.catomon.moemarathon.utils.setPositionByCenter

class NotesDrawer(private val playScreen: PlayScreen) : Actor() {

    private val noteMap = playScreen.noteMap
    private val noteSpawnTime get() = playScreen.noteSpawnTime
    private val noteRadius get() = playScreen.noteRadius

    private val noteName = playScreen.skin.note
    private val noteOuterTex =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_outer")
    private val noteInnerTex =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_inner")
    private val noteTraceTex = assets.mainAtlas.findRegion(playScreen.skin.holdNote + "_trace")
    private val pointerTraceTex = assets.mainAtlas.findRegion(playScreen.skin.holdNotePointer + "_pointer_trace")

    private val enemy0 = assets.mainAtlas.findRegion("note_enemy0")
    private val enemy1 = assets.mainAtlas.findRegion("note_enemy1")
    private val enemy2 = assets.mainAtlas.findRegion("note_enemy2")

    private val enemySprite = Sprite(enemy0)

    private val noteInnerSprite = Sprite(enemy0)
    private val noteOuterSprite = Sprite(noteOuterTex)
    private val noteTraceSprite = Sprite(noteTraceTex)
    private val pointerTraceSprite = Sprite(pointerTraceTex)

    private val outerColor = Color(0.631f, 0.541f, 0.584f, 1f)
    private val innerColor = Color(0.89f, 0.455f, 0.667f, 1f)
    private val traceColor = Color(0.929f, 0.929f, 0.929f, 1f)
    private val nextColor = Color(0.929f, 0.929f, 0.929f, 1f)

    private val applyColor = false
    private val drawEnemies = true

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
                            drawNote(batch, notePos, timeLeft, traceToNote = prevNotePos, visual = note.visual)
                        }

                        else -> {
                            drawNote(batch, notePos, timeLeft, visual = note.visual)
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
                            drawNote(batch, firstNotePos, timeLeft, nextColor, prevNotePos, visual = firstNote.visual)
                        }

                        else -> {
                            drawNote(batch, firstNotePos, timeLeft, nextColor)
                        }
                    }

                    pointerTraceSprite.setPositionByCenter(pointer.x, pointer.y)
                    pointerTraceSprite.rotation = degrees(pointer.x, pointer.y, firstNotePos.x, firstNotePos.y)
                    pointerTraceSprite.draw(batch)
                } else {
                    drawNote(batch, firstNotePos, timeLeft, nextColor, visual = firstNote.visual)
                }
            }
        }
    }

    fun getNoteTexture(visual: Int): TextureRegion =
        when (visual) {
            0 -> enemy0
            1 -> enemy1
            2 -> enemy2
            else -> noteInnerTex
        }

    private fun drawNote(
        batch: Batch,
        notePos: Vector2,
        timeLeft: Float,
        color: Color? = null,
        traceToNote: Vector2? = null,
        visual: Int = -1
    ) {
        if (drawEnemies)
            noteInnerSprite.setRegion(getNoteTexture(visual))
        else
            noteInnerSprite.setRegion(getNoteTexture(-1))

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

        if (traceToNote != null && visual < 0) {
            noteTraceSprite.setPositionByCenter(notePos.x, notePos.y)
            noteTraceSprite.rotation = degrees(notePos.x, notePos.y, traceToNote.x, traceToNote.y)
            noteTraceSprite.setAlpha(a)
            noteTraceSprite.draw(batch)
            noteOuterSprite.draw(batch)
        } else {
            noteOuterSprite.draw(batch)
            noteInnerSprite.draw(batch)
        }
    }

    private fun Note.calcPosition(vector2: Vector2): Vector2 = playScreen.calcNotePosition(this, vector2)
}
