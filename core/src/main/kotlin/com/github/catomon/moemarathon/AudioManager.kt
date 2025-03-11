package com.github.catomon.moemarathon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.utils.logMsg

object AudioManager {

    var soundVolume = GamePref.soundVolume

    var musicVolume = GamePref.musicVolume
        set(value) {
            field = value
            mapMusic?.volume = value
            music?.volume = value
        }

    @get:Synchronized
    var mapMusic: Music? = null
        private set

    private var scheduledMapMusic: GameMap? = null
    var lastMapMusic: GameMap? = null
        private set

    var mapMusicPlay = false
        private set
    var mapMusicPause = false
        private set
    var mapMusicStop = false
        private set

    @get:Synchronized
    var music: Music? = null
        private set

    lateinit var hitSound: Sound
        private set

    init {
        var playback: Thread? = null
        var playbackRun = {
            while (true) {
                if (scheduledMapMusic != null) {
                    if (mapMusic != null) {
                        val oldMusic = mapMusic!!
                        Gdx.app.postRunnable {
                            oldMusic.stop()
                            oldMusic.dispose()
                            logMsg("Prev. Map music disposed.")
                        }
                        mapMusic = null
                    }
                    mapMusic =
                        Gdx.audio.newMusic(scheduledMapMusic!!.file.parent().child(scheduledMapMusic!!.osuBeatmap.audioFileName))
                    mapMusic!!.isLooping = false
                    mapMusic!!.volume = musicVolume

                    lastMapMusic = scheduledMapMusic
                    scheduledMapMusic = null
                    logMsg("Map music loaded.")
                    continue
                }

                if (mapMusic != null) {
                    if (mapMusicPause) {
                        mapMusicPause = false
                        if (mapMusic!!.isPlaying) {
                            Gdx.app.postRunnable {
                                mapMusic!!.pause()
                            }
                            logMsg("Map music pause.")
                            continue
                        }
                    }

                    if (mapMusicPlay) {
                        mapMusicPlay = false
                        if (!mapMusic!!.isPlaying) {
                            Gdx.app.postRunnable {
                                mapMusic!!.play()
                            }
                            logMsg("Map music play.")
                            continue
                        }
                    }

                    if (mapMusicStop) {
                        mapMusicStop = false
                        Gdx.app.postRunnable {
                            mapMusic!!.stop()
                        }
                        logMsg("Map music stop.")
                        continue
                    }
                }

                Thread.sleep(250)
            }
        }

        Thread {
            while (true) {
                if (playback?.isAlive != true) {
                    try {
                        playback = Thread(playbackRun).apply { isDaemon = true; start() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                Thread.sleep(1000)
            }
        }.apply { isDaemon = true; start() }
    }

    fun playSound(sound: Sound) {
        sound.play(soundVolume)
    }

    @Synchronized
    fun loadMapMusic(name: String): Music {
        return loadMapMusic(Gdx.files.internal("maps/$name"))
    }

    @Synchronized
    private fun loadMapMusic(file: FileHandle): Music {
        mapMusic?.stop()
        mapMusic?.dispose()
        mapMusic = Gdx.audio.newMusic(file)
        mapMusic!!.isLooping = false
        mapMusic!!.volume = musicVolume

        return mapMusic!!
    }

    fun getMapMusicPosition(): Float = mapMusic?.position ?: 0f

    fun playMapMusic() {
        mapMusicPlay = true
        mapMusicPause = false
        mapMusicStop = false
    }

    fun pauseMapMusic() {
        mapMusicPause = true
        mapMusicPlay = false
        mapMusicStop = false
    }

    fun stopMapMusic() {
        mapMusicPause = false
        mapMusicPlay = false
        mapMusicStop = true
    }

    @Synchronized
    fun loadMapMusic(map: GameMap) {
        scheduledMapMusic = map
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
        sound.play(volume)
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
