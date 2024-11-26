package com.github.catomon.polly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.github.catomon.polly.map.GameMap
import kotlin.concurrent.thread

object AudioManager {

    var soundVolume = GamePref.soundVolume

    var musicVolume = GamePref.musicVolume
        set(value) {
            field = value
            mapMusic?.volume = value
            music?.volume = value
        }

    @set:Synchronized
    @get:Synchronized
    var mapMusic: Music? = null
        private set

    @set:Synchronized
    @get:Synchronized
    var music: Music? = null
        private set

    lateinit var hitSound: Sound
        private set

    @Synchronized
    fun loadMapMusic(name: String): Music {
        return loadMapMusic(Gdx.files.internal("maps/$name"))
    }

    @Synchronized
    fun loadMapMusic(file: FileHandle): Music {
        mapMusic?.stop()
        mapMusic?.dispose()
        mapMusic = Gdx.audio.newMusic(file)
        mapMusic!!.isLooping = false
        mapMusic!!.volume = musicVolume
        return mapMusic!!
    }

    @Synchronized
    fun loadMapMusic(map: GameMap): Music {
        return loadMapMusic(map.file.parent().child(map.osuBeatmap.audioFileName))
    }

    @Synchronized
    fun loadMusic(file: FileHandle): Music {
        music = Gdx.audio.newMusic(file)
        music!!.volume = musicVolume
        return music!!
    }

    @Synchronized
    fun loadMusic(name: String): Music = loadMusic(Gdx.files.internal("maps/$name"))

    fun onMusicLoaded() {
        this.hitSound = assets.getSound("hit.ogg")
    }

    fun disposeMusic() {
        mapMusic?.dispose()
        music?.dispose()
    }

    fun play(music: Music, volume: Float = musicVolume) {
        music.volume = volume
        music.play()
    }

    fun play(sound: Sound, volume: Float = soundVolume) {
        sound.play(soundVolume)
    }

    fun playSound(name: String, volume: Float = soundVolume) {
        assets.getSound(name).play(volume)
    }

    fun sound(name: String): Long {
        return sound(assets.getSound("$name.ogg"))
    }

    private fun sound(sound: Sound): Long {
        return sound.play(soundVolume)
    }
}
