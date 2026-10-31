package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.catomon.moemarathon.Skins
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.degrees
import com.github.catomon.moemarathon.utils.setPositionByCenter
import kotlin.math.sqrt

private val holdTrailTexture: Texture by lazy {
    Texture(1, 1, Pixmap.Format.RGBA8888).apply {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        draw(pixmap, 0, 0)
        pixmap.dispose()
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }
}

private val holdCapTexture: Texture by lazy {
    val radius = 64
    val width = radius
    val height = radius * 2
    Texture(width, height, Pixmap.Format.RGBA8888).apply {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val dx = x.toFloat()
                val dy = y + 0.5f - radius
                val distance = sqrt(dx * dx + dy * dy)
                val a = ((distance - radius + 1f) / 1.5f).coerceIn(0f, 1f)
                pixmap.setColor(1f, 1f, 1f, a)
                pixmap.drawPixel(x, y)
            }
        }
        draw(pixmap, 0, 0)
        pixmap.dispose()
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }
}

private class HoldBand(
    val from: Vector2,
    val to: Vector2,
    val startAlpha: Float,
    val endAlpha: Float,
    val color: Color,
)

class NotesDrawer(private val playScreen: PlayScreen) : Actor() {

    private val noteMap = playScreen.noteMap
    private val noteSpawnTime get() = playScreen.noteSpawnTime
    private val noteRadius get() = playScreen.noteRadius

    private val skin = playScreen.skin
    private val noteName = playScreen.skin.note
    private val noteOuterTex =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_outer")
    private val noteInnerTex =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_inner")
    private val noteTraceTex = assets.mainAtlas.findRegion(playScreen.skin.holdNote + "_trace")
    private val noteTraceTexKona = assets.mainAtlas.findRegion(playScreen.skin.holdNote + "_trace_inner_kona")
    private val noteTraceTexYuki = assets.mainAtlas.findRegion(playScreen.skin.holdNote + "_trace_inner_yuki")
    private val noteTraceTexTsuka = assets.mainAtlas.findRegion(playScreen.skin.holdNote + "_trace_inner_tsuka")
    private val noteTraceTexKaga = assets.mainAtlas.findRegion(playScreen.skin.holdNote + "_trace_inner_kaga")

    private val enemy0 = assets.mainAtlas.findRegion("note_enemy0")
    private val enemy1 = assets.mainAtlas.findRegion("note_enemy1")
    private val enemy2 = assets.mainAtlas.findRegion("note_enemy2")

    private val enemySprite = Sprite(enemy0)

    private val noteInnerSprite = Sprite(enemy0)
    private val noteOuterSprite = Sprite(noteOuterTex)
    private val noteTraceSprite = Sprite(noteTraceTex)
    private val holdTrailSprite = Sprite(holdTrailTexture)
    private val holdCapSprite = Sprite(holdCapTexture)

    private val outerColor = Color(0.631f, 0.541f, 0.584f, 1f)
    private val innerColor = Color(0.89f, 0.455f, 0.667f, 1f)
    private val traceColor = Color(0.929f, 0.929f, 0.929f, 1f)
    private val nextColor = Color(0.929f, 0.929f, 0.929f, 1f)

    private val noteOuterTexKona =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_outer_kona")
    private val noteInnerTexKona =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_inner_kona")

    private val noteOuterTexYuki =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_outer_yuki")
    private val noteInnerTexYuki =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_inner_yuki")

    private val noteOuterTexTsuka =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_outer_tsuka")
    private val noteInnerTexTsuka =
        if (noteName.isEmpty()) assets.mainAtlas.findRegion("transparent") else assets.mainAtlas.findRegion(noteName + "_inner_tsuka")

    private val rotationColors: List<Triple<Sprite, Sprite, Sprite>> =
        if (skin == Skins.lucky) {
            listOf(
                Triple(Sprite(noteOuterTexKona), Sprite(noteInnerTexKona), Sprite(noteTraceTexKona)),
                Triple(Sprite(noteOuterTex), Sprite(noteInnerTex), Sprite(noteTraceTexKaga)),
                Triple(Sprite(noteOuterTexYuki), Sprite(noteInnerTexYuki), Sprite(noteTraceTexYuki)),
                Triple(Sprite(noteOuterTexTsuka), Sprite(noteInnerTexTsuka), Sprite(noteTraceTexTsuka)),
            )
        } else {
            listOf(
                Triple(Sprite(noteOuterTex), Sprite(enemy0), Sprite(noteTraceTex)),
                Triple(Sprite(noteOuterTex), Sprite(enemy1), Sprite(noteTraceTex)),
                Triple(Sprite(noteOuterTex), Sprite(enemy2), Sprite(noteTraceTex)),
            )
        }

    private val applyColor = false
    private val drawEnemies = true

    private val trailColors: List<Color> =
        if (skin == Skins.lucky) {
            listOf(
                Color(66 / 255f, 84 / 255f, 180 / 255f, 1f),
                Color(109 / 255f, 66 / 255f, 180 / 255f, 1f),
                Color(220 / 255f, 105 / 255f, 145 / 255f, 1f),
                Color(109 / 255f, 66 / 255f, 180 / 255f, 1f),
            )
        } else {
            listOf(
                Color(164 / 255f, 164 / 255f, 164 / 255f, 1f),
                Color(164 / 255f, 164 / 255f, 164 / 255f, 1f),
                Color(164 / 255f, 164 / 255f, 164 / 255f, 1f),
            )
        }

    private fun trailColor(note: Note): Color = trailColors[note.colorIndex % trailColors.size]

    init {
        if (applyColor) {
            noteInnerSprite.color = innerColor
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
//        noteInnerSprite.setSize(spriteWidth, spriteHeight)
//        noteInnerSprite.setOriginCenter()
//        noteOuterSprite.setSize(spriteWidth, spriteHeight)
//        noteOuterSprite.setOriginCenter()
        noteTraceSprite.setSize(spriteWidth, spriteHeight)
        noteTraceSprite.setOriginCenter()

        for (sprite in rotationColors) {
            sprite.first.setSize(spriteWidth, spriteHeight)
            sprite.second.setSize(spriteWidth, spriteHeight)
            sprite.third.setSize(spriteWidth, spriteHeight)
            sprite.first.setOriginCenter()
            sprite.second.setOriginCenter()
            sprite.third.setOriginCenter()
        }

        val firstNote = noteMap.chunks.lastOrNull()?.notes?.lastOrNull() ?: return
        val firstNotePos = firstNote.calcPosition(Vector2())

        val currentNotes = noteMap.chunks.lastOrNull()?.notes?.toMutableList()
        if (currentNotes != null) {
            val notes = ((noteMap.chunks.elementAtOrNull(noteMap.chunks.size - 2)?.notes?.toList()
                ?: emptyList()) + currentNotes).toMutableList()
            notes.remove(firstNote)

            val visibleNotes = ArrayList<Triple<Note, Vector2, Vector2?>>(notes.size)
            val bands = ArrayList<HoldBand>(notes.size)

            var firstNoteArrowTo: Vector2? = null
            if (firstNote.tracingNext) {
                val endNote = notes.lastOrNull()
                if (endNote != null) {
                    val endPos = endNote.calcPosition(Vector2())
                    bands.add(HoldBand(firstNotePos, endPos, noteAlpha(firstNote), noteAlpha(endNote), trailColor(firstNote)))
                    firstNoteArrowTo = endPos
                }
            } else if (firstNote.tracingPrev) {
                val zonePos = playScreen.getHitZonePosition(playScreen.getHitZoneIdByNote(firstNote))
                firstNoteArrowTo = zonePos
                if (!playScreen.isHoldingNote) {
                    val a = noteAlpha(firstNote)
                    bands.add(HoldBand(zonePos, firstNotePos, a, a, trailColor(firstNote)))
                }
            }

            for ((index, note) in notes.withIndex()) {
                val timeLeft = playScreen.calcNoteTimeLeft(note)
                if (timeLeft <= noteSpawnTime) {
                    val notePos = note.calcPosition(Vector2())
                    var arrowTo: Vector2? = null

                    when {
                        note.tracingNext -> {
                            val endNote = notes.getOrNull(index - 1)
                            if (endNote != null) {
                                val endPos = endNote.calcPosition(Vector2())
                                bands.add(HoldBand(notePos, endPos, noteAlpha(note), noteAlpha(endNote), trailColor(note)))
                                arrowTo = endPos
                            }
                        }

                        note.tracingPrev -> {
                            arrowTo = playScreen.getHitZonePosition(playScreen.getHitZoneIdByNote(note))
                        }
                    }

                    visibleNotes.add(Triple(note, notePos, arrowTo))
                }
            }

            var heldNote: Note? = null
            var heldNotePos: Vector2? = null
            playScreen.holdNote?.let { holdNote ->
                if (firstNote.tracingPrev) {
                    heldNote = holdNote
                    heldNotePos = playScreen.getHitZonePosition(playScreen.getHitZoneIdByNote(holdNote))
                    bands.add(HoldBand(heldNotePos!!, firstNotePos, noteAlpha(holdNote), noteAlpha(firstNote), trailColor(holdNote)))
                }
            }

            for (band in bands) {
                drawHoldTrail(batch, band)
            }

            heldNote?.let { note ->
                drawNote(batch, note, visual = note.visual, position = heldNotePos, arrowTo = firstNotePos)
            }

            for ((note, pos, arrowTo) in visibleNotes) {
                drawNote(batch, note, visual = note.visual, position = pos, arrowTo = arrowTo)
            }

            drawNote(batch, firstNote, visual = firstNote.visual, position = firstNotePos, arrowTo = firstNoteArrowTo)
        }
    }

    fun getNoteTexture(visual: Int): TextureRegion =
        when (visual) {
            0 -> enemy0
            1 -> enemy1
            2 -> enemy2
            else -> noteInnerTex
        }

    private fun noteAlpha(note: Note): Float {
        val timeLeft = playScreen.calcNoteTimeLeft(note)
        var a = (noteSpawnTime - timeLeft) / (noteSpawnTime * 0.1f)
        if (a > 1f) a = 1f
        if (a < 0f) a = 0f
        return a
    }

    private fun drawHoldTrail(batch: Batch, band: HoldBand) {
        val from = band.from
        val to = band.to
        val dx = to.x - from.x
        val dy = to.y - from.y
        val distance = sqrt(dx * dx + dy * dy)
        if (distance < 0.5f) return

        val color = band.color
        val startAlpha = band.startAlpha * 0.9f
        val endAlpha = band.endAlpha * 0.9f
        if (startAlpha <= 0f && endAlpha <= 0f) return

        val thickness = noteRadius * 1.6f
        val capRadius = thickness / 2f
        val angle = degrees(from.x, from.y, to.x, to.y)
        val ux = dx / distance
        val uy = dy / distance

        val bodyStartX = from.x + ux * capRadius
        val bodyStartY = from.y + uy * capRadius
        val bodyDx = to.x - ux * capRadius - bodyStartX
        val bodyDy = to.y - uy * capRadius - bodyStartY
        val bodyLength = sqrt(bodyDx * bodyDx + bodyDy * bodyDy)

        if (bodyLength > 1f) {
            val segments = 16
            for (i in 0 until segments) {
                val t0 = i / segments.toFloat()
                val t1 = (i + 1) / segments.toFloat()
                val a = startAlpha + (endAlpha - startAlpha) * ((t0 + t1) / 2f)
                if (a <= 0.001f) continue

                holdTrailSprite.setSize(bodyLength / segments + 1f, thickness)
                holdTrailSprite.setOrigin(0f, thickness / 2f)
                holdTrailSprite.setPosition(bodyStartX + bodyDx * t0, bodyStartY + bodyDy * t0 - thickness / 2f)
                holdTrailSprite.rotation = angle
                holdTrailSprite.setColor(color.r, color.g, color.b, a)
                holdTrailSprite.draw(batch)
            }
        }

        holdCapSprite.setSize(capRadius, thickness)
        holdCapSprite.setOrigin(0f, thickness / 2f)

        holdCapSprite.rotation = angle
        holdCapSprite.setPosition(from.x, from.y - thickness / 2f)
        holdCapSprite.setColor(color.r, color.g, color.b, startAlpha)
        holdCapSprite.draw(batch)

        holdCapSprite.rotation = angle + 180f
        holdCapSprite.setPosition(to.x, to.y - thickness / 2f)
        holdCapSprite.setColor(color.r, color.g, color.b, endAlpha)
        holdCapSprite.draw(batch)
    }

    private fun drawNote(
        batch: Batch,
        note: Note,
        visual: Int = -1,
        position: Vector2? = null,
        arrowTo: Vector2? = null
    ) {
        val color = rotationColors[note.colorIndex % rotationColors.size]
        val notePos = position ?: note.calcPosition(Vector2())

        val noteOuterSprite = color.first
        val isHold = note.tracingNext || note.tracingPrev
        val noteInnerSprite = if (isHold) color.third else color.second

        if (drawEnemies)
//            noteInnerSprite.setRegion(getNoteTexture(visual))
        else {
//            noteInnerSprite.setRegion(getNoteTexture(-1))
//
//            noteOuterSprite.setRegion(color.first)
//            noteInnerSprite.setRegion(color.second)
        }

        val yOffset = if (playScreen.isLost)
            (notePos.y + 48) - ((notePos.y + 48) * smoothstep((1f - playScreen.lostSec / 2f).coerceIn(0f, 1f)))
        else 0f
        val drawY = notePos.y - yOffset

        noteInnerSprite.setPositionByCenter(notePos.x, drawY)
        noteOuterSprite.setPositionByCenter(notePos.x, drawY)

        val a = noteAlpha(note)

        noteInnerSprite.setAlpha(a)
        noteOuterSprite.setAlpha(a)

        if (isHold) {
            noteInnerSprite.draw(batch)

            if (arrowTo != null) {
                noteTraceSprite.setPositionByCenter(notePos.x, drawY)
                noteTraceSprite.rotation = degrees(notePos.x, notePos.y, arrowTo.x, arrowTo.y)
                noteTraceSprite.setAlpha(a)
                noteTraceSprite.draw(batch)
            }

            noteOuterSprite.draw(batch)
        } else {
            noteOuterSprite.draw(batch)
            noteInnerSprite.draw(batch)
        }
    }

    private fun smoothstep(dist: Float): Float {
        val t = dist.coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    private fun Note.calcPosition(vector2: Vector2): Vector2 = playScreen.calcNotePosition(this, vector2)
}
