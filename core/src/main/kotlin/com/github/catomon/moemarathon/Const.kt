package com.github.catomon.moemarathon

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

object Const {
    const val APP_NAME = "Moe Marathon"
    const val APP_VER = "1.4"
    const val IS_RELEASE = true
    const val WINDOW_WIDTH = 1200
    const val WINDOW_HEIGHT = 700
    const val DEBUG = false

    val IS_DESKTOP = Gdx.app.type == Application.ApplicationType.Desktop
    val IS_MOBILE = Gdx.app.type == Application.ApplicationType.Android
        || Gdx.app.type == Application.ApplicationType.iOS

    const val SCORE_GAIN_TRACE = 900
    const val SCORE_GAIN_GREAT = 300
    const val SCORE_GAIN_OK = 200
}
