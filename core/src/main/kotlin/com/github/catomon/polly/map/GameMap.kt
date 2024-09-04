package com.github.catomon.polly.map

import com.badlogic.gdx.files.FileHandle
import com.github.catomon.polly.map.osu.OsuParser

class GameMap(val file: FileHandle) {

    val osuBeatmap = OsuParser.parse(file.readString())
    val noteMap = MapsManager.createNoteMap(osuBeatmap)
}
