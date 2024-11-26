package com.github.catomon.moemarathon.utils

import com.badlogic.gdx.Gdx

fun Any.logMsg(msg: String) {
    Gdx.app.log(this::class.simpleName, msg)
}

fun Any.logErr(msg: String) {
    Gdx.app.error(this::class.simpleName, msg)
}
