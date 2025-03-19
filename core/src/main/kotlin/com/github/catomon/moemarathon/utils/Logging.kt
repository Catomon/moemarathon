package com.github.catomon.moemarathon.utils

import com.badlogic.gdx.Gdx

fun Any.logInf(msg: String) {
    Gdx.app.log(this::class.simpleName, msg)
}

fun Any.logErr(msg: String) {
    Gdx.app.error(this::class.simpleName, msg)
}

fun echoInf(msg: String) {
    Gdx.app.log("I", msg)
}

fun echoErr(msg: String) {
    Gdx.app.error("E", msg)
}
