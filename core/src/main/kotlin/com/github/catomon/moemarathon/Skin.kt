package com.github.catomon.moemarathon

import com.github.catomon.moemarathon.utils.logErr

data class Skin(
    val name: String,
    val note: String,
    val holdNote: String,
    val holdNotePointer: String,
    val noteEnemy: String,
    val center: String,
    val centerType: String,
    val timingsCircle: String,
    val hit: String,
    val pop: String,
    val miss: String,
)

object Skins {

    const val ANIMATED_CENTER = "animation"
    const val ANI_DIR_CENTER = "directional_animation"

    val lucky = Skin(
        name = "lucky",
        note = "lucky_note",
        noteEnemy = "",
        center = "lucky_note_inner",
        centerType = "",
        timingsCircle = "lucky_timings_circle",
        holdNote = "lucky_note",
        holdNotePointer = "lucky_note",
        hit = "lucky_",
        pop = "lucky_pop",
        miss = "lucky_miss",
    )

    val saberBald = Skin(
        name = "saber bald",
        note = "def_note",
        noteEnemy = "",
        center = "def_note_outer", //center is also used as a preview of a skin
        centerType = "",
        timingsCircle = "def_timings_circle",
        holdNote = "def_note",
        holdNotePointer = "def_note",
        hit = "", //$hit_
        pop = "pop",
        miss = "miss",
    )

    val saberStrike = Skin(
        name = "saber strike",
        note = "def_cat_note",
        noteEnemy = "",
        center = "def_cat_note_outer",
        centerType = "",
        timingsCircle = "def_timings_circle",
        holdNote = "def_note",
        holdNotePointer = "def_note",
        hit = "", //$hit_
        pop = "cat_pop",
        miss = "cat_miss",
    )

    val bekky = Skin(
        name = "bekky",
        note = "bun",
        noteEnemy = "",
        center = "bekkydancing/frame",
        centerType = ANIMATED_CENTER,
        timingsCircle = "bekky_timings_circle",
        holdNote = "bun",
        holdNotePointer = "bun",
        hit = "",
        pop = "bun_pop",
        miss = "bun_miss",
    )

    val komugi = Skin(
        name = "komugi",
        note = "",
        noteEnemy = "note_enemy",
        center = "center",
        centerType = ANI_DIR_CENTER,
        timingsCircle = "timings_circle",
        holdNote = "note2",
        holdNotePointer = "note2",
        hit = "pix_",
        pop = "pix_miss_pop",
        miss = "pix_miss_pop",
    )

    val skins: List<Skin> = listOf(
        lucky, saberBald, saberStrike, komugi, bekky
    )

    fun getSkin(name: String): Skin? {
        val skin = skins.firstOrNull { it.name == name }
        if (skin == null) logErr("Skin not found: $name")
        return skin
    }
}
