package com.github.catomon.moemarathon.map

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.github.catomon.moemarathon.map.osu.OsuParser

class GameMap(val file: FileHandle) {
    val osuBeatmap = OsuParser.parse(file.readString())

    fun newBackgroundTexture() = Texture(file.parent().child(osuBeatmap.backgroundFileName))

    override fun toString(): String {
        return file.nameWithoutExtension()
    }
}
