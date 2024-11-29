package com.github.catomon.moemarathon

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

object Const {
    const val APP_NAME = "Moe Marathon"
    const val APP_VER = "0.1"
    const val IS_RELEASE = false
    const val WINDOW_WIDTH = 1000
    const val WINDOW_HEIGHT = 600
    const val DEBUG = false

    val IS_DESKTOP = Gdx.app.type == Application.ApplicationType.Desktop
    val IS_MOBILE = Gdx.app.type == Application.ApplicationType.Android
        || Gdx.app.type == Application.ApplicationType.iOS

    const val SCORE_GAIN_TRACE = 900
    const val SCORE_GAIN_GREAT = 300
    const val SCORE_GAIN_OK = 200
}
