package com.github.catomon.polly.utils

import com.badlogic.gdx.utils.Json

val gson: Json = Json()

fun <T> fromGson(json: String, clazz: Class<T>) : T {
    return gson.fromJson(clazz, json)
}

fun Any.toGson() : String {
    return gson.toJson(this, this::class.java)
}

fun Any.toPrettyGson() : String {
    return gson.prettyPrint(this)
}
