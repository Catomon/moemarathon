package com.github.catomon.polly.difficulties

object Ranks {

    fun getRankChar(i: Int) : String {
        return when (i) {
            0 -> ""
            1 -> "F"
            2 -> "D"
            3 -> "C"
            4 -> "B"
            5 -> "A"
            6 -> "S"
            7 -> "SS"
            else -> throw IllegalArgumentException("rank $i doesn't exist")
        }
    }
}
