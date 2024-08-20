package com.github.catomon.polly

import com.badlogic.gdx.Gdx

object AudioManager {

    val hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drum-hitnormal.ogg"))

    val music = Gdx.audio.newSound(Gdx.files.internal("maps/tearrain.mp3"))
}
