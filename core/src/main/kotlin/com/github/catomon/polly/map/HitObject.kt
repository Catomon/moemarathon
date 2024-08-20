package com.github.catomon.polly.map

import com.github.catomon.polly.Note
import com.github.catomon.polly.utils.calculateDegrees

data class HitObject(
    val x: Int,
    val y: Int,
    val time: Int,
    val type: Int = 0,
    val hitSound: Int = 0,
    val objectParams: String = "",
    val hitSample: String = "",
)

fun HitObject.toNote(): Note { //512x384
    return Note(time / 1000f, calculateDegrees(256f, 193f, x.toFloat(), y.toFloat()) / 360)
}

class HitObjectParser(private val textSize: String) {
    fun parse(): List<HitObject> {
        val hitObjects = mutableListOf<HitObject>()
        textSize.split("\n") // split by line breaks
            .drop(1) // skip the first line "[HitObjects]"
            .forEach { line ->
                val parts = line.split(",")
                when {
//                    parts.size > 10 -> {
//                        //todo
//                        val hitObject = HitObject(
//                            x = parts[0].toInt(),
//                            y = parts[1].toInt(),
//                            time = parts[2].toInt(),
//                            type = parts.getOrNull(3)?.toInt() ?: 0,
////                        hitSound = parts.getOrNull(4)?.toInt() ?: 0,
////                        objectParams = parts.getOrNull(5) ?: "",
////                        hitSample = parts.getOrNull(6) ?: "",
//                        )
//                        hitObjects.add(hitObject)
//                    }

                    parts.size > 1 -> {
                        val hitObject = HitObject(
                            x = parts[0].toInt(),
                            y = parts[1].toInt(),
                            time = parts[2].toInt(),
                            type = parts.getOrNull(3)?.toInt() ?: 0,
//                        hitSound = parts.getOrNull(4)?.toInt() ?: 0,
//                        objectParams = parts.getOrNull(5) ?: "",
//                        hitSample = parts.getOrNull(6) ?: "",
                        )
                        hitObjects.add(hitObject)
                    }
                }
            }
        return hitObjects
    }
}
