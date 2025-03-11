@file:JvmName("Lwjgl3Launcher")

package com.github.catomon.moemarathon.lwjgl3

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.catomon.moemarathon.Const.APP_NAME
import com.github.catomon.moemarathon.Const.APP_VER
import com.github.catomon.moemarathon.Const.WINDOW_HEIGHT
import com.github.catomon.moemarathon.Const.WINDOW_WIDTH
import com.github.catomon.moemarathon.GameMain
import com.github.catomon.moemarathon.IPlatformSpecific
import com.github.catomon.moemarathon.platformSpecific
import java.awt.Desktop

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return

    val onGameCreate = {
        platformSpecific = object : IPlatformSpecific {
            override fun desktopOpenMapsFolder() {
                Desktop.getDesktop().open(Gdx.files.local("maps/other maps/").file().also { it.mkdirs() })
            }
        }
    }

    Lwjgl3Application(GameMain(onGameCreate), Lwjgl3ApplicationConfiguration().apply {
        setTitle("$APP_NAME $APP_VER")
        setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))

        setPreferencesConfig("Documents/$APP_NAME", Files.FileType.External)
    })
}
