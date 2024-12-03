package com.github.catomon.moemarathon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.github.catomon.moemarathon.Const.APP_NAME
import com.github.catomon.moemarathon.Const.IS_MOBILE
import com.github.catomon.moemarathon.utils.*
import com.github.catomon.moemarathon.UserSave
import java.lang.Exception
import java.util.*

fun GamePref.overridePrefs() {
    musicVolume = 0f
}

object GamePref {

    const val PREF_FILE_NAME = "settings.xml"

    private const val LOCALE = "locale"

    var prefs: Preferences = Gdx.app.getPreferences(PREF_FILE_NAME)

    val savesFolder =
        when {
            IS_MOBILE -> {
                Gdx.files.local("saves/")
            }

            else -> {
                Gdx.files.external("Documents/$APP_NAME/saves/")
                    .apply { mkdirs() }
            }
        }

    val clientId: String =
        if (!Const.IS_RELEASE)
            UUID.randomUUID().toString()
        else
            if (prefs.contains("client_id"))
                prefs.getString("client_id")
            else
                UUID.randomUUID().toString().also { prefs.putString("client_id", it).flush() }

    var locale: String
        get() = prefs.getString(LOCALE, GameLocale.ENGLISH)
        set(value) {
            prefs.putString(LOCALE, value)
        }

    var targetFps: Int
        get() = prefs.getInteger("target_fps", 60)
        set(value) {
            prefs.putInteger("target_fps", value)
        }

    var vSync: Boolean
        get() = prefs.getBoolean("vSync", true)
        set(value) {
            prefs.putBoolean("vSync", value)
        }

    var fullscreen: Boolean
        get() = prefs.getBoolean("fullscreen", false)
        set(value) {
            prefs.putBoolean("fullscreen", value)
        }

    var soundVolume: Float
        get() = prefs.getFloat("sound_volume", 0.50f)
        set(value) {
            prefs.putFloat("sound_volume", value)
        }

    var musicVolume: Float
        get() = prefs.getFloat("music_volume", 0.50f)
        set(value) {
            prefs.putFloat("music_volume", value)
        }

    var userSave: UserSave
        get() {
            return try {
                fromGson(decryptData(prefs.getString("userSave"), generateSecretKey("moe")), UserSave::class.java) ?: newUserSave("Player")
            } catch (e: Exception) {
                e.printStackTrace()
                UserSave()
            }
        }
        set(value) {
            prefs.putString("userSave", encryptData(value.toGson(), generateSecretKey("moe")))
        }

    fun save() {
        logMsg("Saving: $userSave")

        prefs.flush()
    }
}
