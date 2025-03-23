package com.github.catomon.moemarathon.playscreen

import com.badlogic.gdx.Input

fun getHitZoneIdByButton(button: Int): Int {
    //todo different hitZoneAmount
    return when (button) {
        Input.Keys.F -> 3
        Input.Keys.D -> 4
        Input.Keys.S -> 5
        Input.Keys.J -> 2
        Input.Keys.K -> 1
        Input.Keys.L -> 6
        else -> -1
    }
}

fun getHitZoneKeyById(i: Int) = when (i) {
    0 -> "K"
    1 -> "J"
    2 -> "F"
    3 -> "D"
    4 -> "S"
    5 -> "L"
    else -> "?"
}
