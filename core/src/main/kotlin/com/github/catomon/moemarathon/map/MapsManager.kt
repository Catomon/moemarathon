package com.github.catomon.moemarathon.map

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Queue
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.map.osu.OsuBeatmap
import com.github.catomon.moemarathon.map.osu.OsuParser
import com.github.catomon.moemarathon.map.osu.hitObjectToNotePosition
import com.github.catomon.moemarathon.map.osu.toNote
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteMap
import kotlin.math.abs
import kotlin.math.absoluteValue

object MapsManager {
    fun collectMapFiles(): List<FileHandle> {
        return when (Gdx.app.type) {
            Application.ApplicationType.Android -> {
                collectOsuFilesFromFolder(
                    Gdx.files.internal("maps/")
                ) + collectOsuFilesFromFolder(Gdx.files.local("maps/"))
            }

            Application.ApplicationType.Desktop -> {
                if (Config.IS_RELEASE)
                    collectOsuFilesFromFolder(Gdx.files.local("maps/"))
                else
                    collectOsuFilesFromFolder(Gdx.files.internal("maps/"))
            }

            Application.ApplicationType.WebGL -> {
                collectOsuFilesFromFolder(Gdx.files.internal("maps/"))
            }

            else -> {
                collectOsuFilesFromFolder(
                    Gdx.files.internal("maps/")
                ) + collectOsuFilesFromFolder(Gdx.files.local("maps/"))
            }
        }
    }

    fun getInternalMapFile(name: String): FileHandle {
        return Gdx.files.internal("maps/$name")
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

    fun createNoteMap(osuBeatmap: OsuBeatmap, hitZones: Int): NoteMap {
        val hits = OsuParser.parseHitObjects(osuBeatmap.hitObjects)
        val noteMap = NoteMap(Queue(hits.size / 10 + 1))
        noteMap.chunks.addFirst(NoteMap.Chunk())
        var prevNote: Note? = null
        hits.forEach { hit ->
            val chunk = noteMap.chunks.first()

            if (hit.objectParams.size < 8) {
                val note = hit.toNote(hitZones)
                //swap note position if hit time window between prev and current is less than 190ms
                if (hitZones <= 9)
                    prevNote?.let { prevNote ->
                        val diff = note.initialPosition - prevNote.initialPosition.absoluteValue
                        if ((diff == 0f || diff == 1f) && note.timing - prevNote.timing < 0.190f) {
                            note.initialPosition = ((note.initialPosition - 0.5f).absoluteValue).coerceIn(0f, 1f)
                        }
                    }
                prevNote = note
                chunk.notes.addFirst(note)
            } else {
                chunk.notes.addFirst(hit.toNote(hitZones, tracingNext = true))

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

                val sliderEndTimeMillis = (hit.time + sliderEndTime * slides).toInt()
                val sliderEndNote =
                    Note(
                        sliderEndTimeMillis / 1000f,
                        hitObjectToNotePosition(hitZones, hit.x, hit.y), //sliderEndXY[0], sliderEndXY[1]
                        tracingPrev = true
                    )
                chunk.notes.addFirst(sliderEndNote)
                prevNote = sliderEndNote
            }

            if (chunk.notes.size >= 10)
                noteMap.chunks.addFirst(NoteMap.Chunk())
        }

        return noteMap
    }
}
