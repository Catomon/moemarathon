package com.github.catomon.moemarathon.lwjgl3;

import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Resampling

object RunTexturePacker {

    @JvmStatic
    fun main(args: Array<String>) {
        pack()
    }

    fun pack() {
        println(RunTexturePacker::class.java.simpleName + "Packing...")
        val settings = TexturePacker.Settings()
        settings.pot = true
        settings.fast = true
        settings.combineSubdirectories = true
        settings.paddingX = 2
        settings.paddingY = 2
        settings.edgePadding = true
        settings.duplicatePadding = true
        settings.minHeight = 640
        settings.minWidth = 640
        settings.maxHeight = 4096
        settings.maxWidth = 4096
        settings.scaleResampling = arrayOf(Resampling.nearest)

        process(settings, "assets/textures/", "./assets", "textures")

//        process(settings, "assets/textures/skin/tinted/tinted-raw", "./assets/skin/tinted", "tinted")
    }

    private fun process(settings: TexturePacker.Settings, input: String, output: String, packFileName: String) {
        try {
            TexturePacker.process(settings, input, output, packFileName)
        } catch (e: Exception) {
            println("Error processing " + packFileName + ". " + e.message)
            e.printStackTrace()
        }
    }
}
