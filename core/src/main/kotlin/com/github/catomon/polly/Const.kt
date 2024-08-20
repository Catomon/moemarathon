package com.github.catomon.polly

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

object Const {
    const val APP_NAME = "Polly"
    const val APP_VER = "0.1"
    const val WINDOW_WIDTH = 640
    const val WINDOW_HEIGHT = 480

    val IS_DESKTOP = Gdx.app.type == Application.ApplicationType.Desktop
    val IS_MOBILE = Gdx.app.type == Application.ApplicationType.Android
        || Gdx.app.type == Application.ApplicationType.iOS
}
