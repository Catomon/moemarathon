package com.github.catomon.polly

import com.badlogic.gdx.utils.Queue

class Map(
    val chunks: Queue<Chunk>
) {

    class Chunk(
        val notes: Queue<Note> = Queue<Note>(64)


    ) {
        override fun toString(): String {
            return "Chunk(${notes.joinToString()})"
        }
    }

    override fun toString(): String {
        return "Map(${chunks.joinToString()})"
    }
}
