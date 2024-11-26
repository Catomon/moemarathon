package com.github.catomon.moemarathon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json

var strings: Strings = getLangStringsByPrefs()

object GameLocale {
    const val ENGLISH = "en";
    const val JAPANESE = "jp"
    const val SPANISH = "es"
    const val RUSSIAN = "ru"
}

fun getLangStringsByPrefs() : Strings {
    val file = Gdx.files.internal("lang/${GamePref.locale}.json")
    if (!file.exists()) {
        check(GamePref.locale != GameLocale.ENGLISH)

        GamePref.locale = GameLocale.ENGLISH
        GamePref.save()
        return getLangStringsByPrefs()
    }

    return Json().fromJson(Strings::class.java, file.readString(Charsets.UTF_8.name()))
}

class Strings {

    var menu = Menu()
    var play = Play()

    class Menu {
        val play = "Play"
        val exit = "Exit"
    }

    class Play {

    }
}
