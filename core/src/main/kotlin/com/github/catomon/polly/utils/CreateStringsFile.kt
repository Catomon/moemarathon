package com.github.catomon.polly.utils

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.github.catomon.polly.Strings

fun main() {
    val fileHande = FileHandle("assets/lang/strings.json")
    fileHande.writeString(Json().prettyPrint(Strings()), false, Charsets.UTF_8.name())
}
