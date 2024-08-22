package com.github.catomon.polly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

object AudioManager {

    val testMusic = Gdx.audio.newMusic(Gdx.files.internal("maps/juna.mp3"))

    var soundVolume = GamePref.soundVolume

    var musicVolume = GamePref.musicVolume
        set(value) {
            field = value
            mapMusic?.volume = value
            currentMusic2?.volume = value
        }

    var mapMusic: Music? = null
    var currentMusic2: Music? = null

    lateinit var hitSound: Sound

    fun onMusicLoaded() {
        this.hitSound = assets.getSound("hit.ogg")
    }

    fun disposeMusic() {
        mapMusic?.dispose()
        currentMusic2?.dispose()
    }

    fun sound(name: String): Long {
        return sound(assets.getSound("$name.ogg"))
    }

    private fun sound(sound: Sound): Long {
        return sound.play(soundVolume)
    }
}
