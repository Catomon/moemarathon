@file:JvmName("Lwjgl3Launcher")

package com.github.catomon.polly.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.catomon.polly.Const.APP_NAME
import com.github.catomon.polly.Const.APP_VER
import com.github.catomon.polly.GameMain

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(GameMain(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("$APP_NAME $APP_VER")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
