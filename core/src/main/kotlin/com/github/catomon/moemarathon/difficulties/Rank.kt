package com.github.catomon.moemarathon.difficulties

import com.badlogic.gdx.graphics.Color
import com.kotcrab.vis.ui.widget.VisLabel

data class Rank(
    val id: Int = 0,
    val score: Int = 0,
    val great: Int = 0,
    val ok: Int = 0,
    val miss: Int = 0,
    val combo: Int = 0,
)

object RankUtil {

    fun newRankLabel(rank: String) = VisLabel(rank, getRankColor(rank))

    fun newRankLabel(rank: Int) = VisLabel(getRankChar(rank), getRankColor(getRankChar(rank)))

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

    fun getRankColor(rank: String): Color {
        return when (rank) {
            "" -> Color.WHITE
            "F" -> Color.PURPLE
            "D" -> Color.NAVY
            "C" -> Color.BLUE
            "B" -> Color.ROYAL
            "A" -> Color.GREEN
            "S" -> Color.YELLOW
            "SS" -> Color.ORANGE
            else -> throw IllegalArgumentException("rank $rank doesn't exist")
        }
    }
}
