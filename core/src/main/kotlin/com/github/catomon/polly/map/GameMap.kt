package com.github.catomon.polly.map

import com.badlogic.gdx.files.FileHandle
import com.github.catomon.polly.map.osu.OsuParser

class GameMap(val file: FileHandle) {
    val osuBeatmap = OsuParser.parse(file.readString())

    override fun toString(): String {
        return file.nameWithoutExtension()
    }
}
