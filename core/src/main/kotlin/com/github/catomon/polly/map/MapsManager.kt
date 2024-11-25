package com.github.catomon.polly.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Queue
import com.github.catomon.polly.Const
import com.github.catomon.polly.map.osu.OsuBeatmap
import com.github.catomon.polly.map.osu.OsuParser
import com.github.catomon.polly.map.osu.toNote
import com.github.catomon.polly.playscreen.Note
import com.github.catomon.polly.playscreen.NoteMap
import com.github.catomon.polly.utils.calculateDegrees
import kotlin.math.abs

object MapsManager {
    fun collectMapFiles(): List<FileHandle> {
        return if (Const.IS_MOBILE) {
            collectOsuFilesFromFolder(
                Gdx.files.internal("maps/")
            ) + collectOsuFilesFromFolder(Gdx.files.local("maps/"))
        } else {
            if (Const.IS_RELEASE)
                collectOsuFilesFromFolder(Gdx.files.local("maps/"))
            else
                collectOsuFilesFromFolder(Gdx.files.internal("maps/"))
        }
    }

    fun getInternalMapFile(name: String) : FileHandle {
     return  Gdx.files.internal("maps/$name")
    }

    fun collectOsuFilesFromFolder(
        folder: FileHandle,
        maps: MutableList<FileHandle> = mutableListOf()
    ): MutableList<FileHandle> {
        val files = folder.list()
        for (file in files) {
            if (file.isDirectory) {
                collectOsuFilesFromFolder(file, maps)
            } else {
                if (file.extension() == "osu")
                    maps.add(file)
            }
        }

        return maps
    }

    fun createNoteMap(osuBeatmap: OsuBeatmap): NoteMap {
        val hits = OsuParser.parseHitObjects(osuBeatmap.hitObjects)
        val noteMap = NoteMap(Queue(hits.size / 10 + 1))
        noteMap.chunks.addFirst(NoteMap.Chunk())
        hits.forEach { hit ->
            val chunk = noteMap.chunks.first()

            if (hit.objectParams.size < 8)
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
                val sliderMultiplier = osuBeatmap.sliderMultiplier
                val sv = 100f / abs(osuBeatmap.timingPoints
                    .reversed()
                    .map { it.split(",").take(2).let { it[0].toFloat().toInt() to it[1].toFloat() } }
                    .firstOrNull { it.first <= hit.time && it.second < 0 }?.second ?: -100f
                )

                val beatLength = osuBeatmap.timingPoints
                    .reversed()
                    .map { it.split(",").take(2).let { it[0].toFloat().toInt() to it[1].toFloat() } }
                    .firstOrNull { it.first <= hit.time && it.second > 0 }?.second ?: 600f

                val sliderEndTime = length / (sliderMultiplier * 100f * sv) * beatLength

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

        return noteMap
    }
}
