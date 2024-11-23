package com.github.catomon.polly.difficulties

object Ranks {

    fun getRankChar(i: Int): String {
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

    fun getRankInt(rank: String): Int {
        return when (rank) {
            "" -> 0
            "F" -> 1
            "D" -> 2
            "C" -> 3
            "B" -> 4
            "A" -> 5
            "S" -> 6
            "SS" -> 7
            else -> throw IllegalArgumentException("rank $rank doesn't exist")
        }
    }
}
