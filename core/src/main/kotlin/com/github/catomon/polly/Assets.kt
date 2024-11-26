package com.github.catomon.moemarathon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI

class Assets : AssetManager() {

    companion object {
        const val soundsFolderName = "sounds"
        const val musicFolderName = "music"

        const val MAIN_ATLAS = "textures.atlas"

        const val SKIN_NAME = "tinted"
    }

    lateinit var mainAtlas: TextureAtlas
    lateinit var skinAtlas: TextureAtlas

    override fun update(): Boolean {
        val done = super.update()

//        if (done && !finishedLoading) {
//            onFinishLoading()
//            finishedLoading = true
//        }

        return done
    }

    private var finishedLoading = false

    fun onFinishLoading() {
        mainAtlas = get(MAIN_ATLAS)
        skinAtlas = VisUI.getSkin().atlas

        AudioManager.onMusicLoaded()
    }

    fun beginLoadingAll() {
        Gdx.app.debug(Assets::class.simpleName, "Loading textures...")

        //texture atlas
        load(MAIN_ATLAS, TextureAtlas::class.java)

        // images
//        load("sky.png", Texture::class.java)

        Gdx.app.debug(Assets::class.simpleName, "Loading textures V")

        Gdx.app.debug(Assets::class.simpleName, "Loading sounds...")
        fun loadSound(name: String) {
            load("$soundsFolderName/$name", Sound::class.java)
        }
        loadSound("hit.ogg")
        loadSound("click.ogg")

        Gdx.app.debug(Assets::class.simpleName, "Loading sounds V")

//FileHandle.list() not working in jar and isDirectory returns false even if it's a directory
//        for (file in Gdx.files.internal(soundsFolderName).list()) {
//            if (file.extension() != "ogg")
//                continue
//
//            load("$soundsFolderName/${file.name()}", Sound::class.java)
//            Gdx.app.log(Assets::class.simpleName, "Loading $soundsFolderName/${file.name()}")
//        }
    }

    fun loadUI() {
        when (GamePref.locale) {
            //"ru" -> VisUI.load(Gdx.files.internal("skin/wafer-ui_ru.json"))
            else -> VisUI.load(Gdx.files.internal("skin/$SKIN_NAME/$SKIN_NAME.json"))
        }
    }

    fun getTexture(name: String): Texture = get(name, Texture::class.java)

    fun getSound(name: String): Sound = get("$soundsFolderName/$name")

    fun getMusic(name: String): Music = get("$musicFolderName/$name")

    fun getDrawable(name: String): Drawable = VisUI.getSkin().getDrawable(name)
}
