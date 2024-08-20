package com.github.catomon.polly

import com.badlogic.gdx.Gdx

object AudioManager {

    val hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drum-hitnormal.ogg"))

    val music = Gdx.audio.newMusic(Gdx.files.internal("maps/juna.mp3"))
}
