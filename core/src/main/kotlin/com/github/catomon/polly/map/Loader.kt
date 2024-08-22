package com.github.catomon.polly.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Queue
import com.github.catomon.polly.NoteMap
import com.github.catomon.polly.Note
import com.github.catomon.polly.map.osu.OsuParser
import com.github.catomon.polly.map.osu.toNote
import kotlin.random.Random

private val colors = listOf(Color.GREEN, Color.SKY, Color.TAN, Color.BLUE, Color.GOLD)

fun loadNoteMap(fileName: String): NoteMap {
    val hits = OsuParser.parseHitObjects(
        Gdx.files.internal("maps/$fileName").readString()
    )

    val noteMap = NoteMap(Queue(hits.size / 10 + 1))
    noteMap.chunks.addFirst(NoteMap.Chunk())
    hits.forEach { hit ->
        val chunk = noteMap.chunks.first()
        chunk.notes.addFirst(hit.toNote())

        if (chunk.notes.size >= 10)
            noteMap.chunks.addFirst(NoteMap.Chunk())
    }

    return noteMap
}

fun loadTestNoteMap(): NoteMap = NoteMap().apply {
    chunks.apply {
        var second = 2f
        repeat(10) {
            addFirst(NoteMap.Chunk().apply {
                repeat(10) {
                    second += 1 * Random.nextFloat() + 0.25f
                    val tracingStart = it == 3
                    val tracingEnd = it == 4
                    notes.addFirst(
                        Note(
                            timing = second,
                            initialPosition = Random.nextFloat(),
                            tracingNext = tracingStart,
                            tracingPrev = tracingEnd,
                            color = Random.nextInt(0, colors.size)
                        )
                    )
                }
            })
        }
    }
}
