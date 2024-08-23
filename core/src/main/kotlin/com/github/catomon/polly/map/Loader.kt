package com.github.catomon.polly.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Queue
import com.github.catomon.polly.AudioManager
import com.github.catomon.polly.Note
import com.github.catomon.polly.NoteMap
import com.github.catomon.polly.map.osu.OsuParser
import com.github.catomon.polly.map.osu.toNote
import com.github.catomon.polly.utils.calculateDegrees
import kotlin.math.abs
import kotlin.random.Random

private val colors = listOf(Color.GREEN, Color.SKY, Color.TAN, Color.BLUE, Color.GOLD)

fun loadNoteMap(fileName: String): NoteMap {
    val osuFile = OsuParser.parse(Gdx.files.internal("maps/$fileName").readString())
    val hits = OsuParser.parseHitObjects(osuFile.hitObjects)

    val noteMap = NoteMap(Queue(hits.size / 10 + 1))
    noteMap.chunks.addFirst(NoteMap.Chunk())
    hits.forEach { hit ->
        val chunk = noteMap.chunks.first()

        if (hit.objectParams.size < 11)
            chunk.notes.addFirst(hit.toNote())
        else {
            chunk.notes.addFirst(hit.toNote(tracingNext = true))

            //Slider syntax:
            // x,y,time,type,hitSound,curveType|curvePoints,slides,length,edgeSounds,edgeSets,hitSample

            val sliderEndXY = hit.objectParams[5].split("|").last().split(":").map { it.toInt() }

            //length / (SliderMultiplier * 100 * SV) * beatLength
            // tells how many milliseconds it takes to complete one slide of
            // the slider (where SV is the slider velocity multiplier given by the
            // effective inherited timing point, or 1 if there is none).

            val slides = hit.objectParams[6].toInt()
            val length = hit.objectParams[7].toFloat()
            val sliderMultiplier = osuFile.sliderMultiplier
            val sv = 100f / abs(osuFile.timingPoints
                .reversed()
                .map { it.split(",").take(2).let { it[0].toFloat().toInt() to it[1].toFloat() } }
                .firstOrNull { it.first <= hit.time && it.second < 0 }?.second ?: -100f)

            val beatLength = osuFile.timingPoints
                .reversed()
                .map { it.split(",").take(2).let { it[0].toFloat().toInt() to it[1].toFloat() } }
                .firstOrNull { it.first <= hit.time && it.second > 0 }?.second ?: 600f

            val sliderEndTime = length / (sliderMultiplier * 100f * sv) * beatLength

            println()
            println(sv)
            println((hit.time + sliderEndTime * slides) / 1000f)
            println(beatLength)
            println(slides)
            println()

            val sliderEndNote =
                Note(
                    (hit.time + sliderEndTime * slides) / 1000f,
                    calculateDegrees(256f, 193f, sliderEndXY[0].toFloat(), sliderEndXY[1].toFloat()) / 360,
                    tracingPrev = true
                )
            chunk.notes.addFirst(sliderEndNote)
        }

        if (chunk.notes.size >= 10)
            noteMap.chunks.addFirst(NoteMap.Chunk())
    }

    AudioManager.setMapMusic(osuFile.audioFileName)

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
