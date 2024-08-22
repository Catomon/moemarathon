package com.github.catomon.polly.playstage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.PlayScreen
import com.github.catomon.polly.utils.SpriteActor

class PlayStage(val playScreen: PlayScreen) : Stage(ScreenViewport(playScreen.camera), playScreen.batch) {

    val centerActor = CenterActor(playScreen)
    val notesDrawer = NotesDrawer(playScreen)

    init {
        addActor(centerActor)
        addActor(notesDrawer)
    }

    override fun draw() {
        val camera = viewport.camera
        camera.update()

        if (!root.isVisible) return

        val batch = this.batch
        batch.projectionMatrix = camera.combined
        batch.begin()
        root.draw(batch, 1f)
        batch.end()
    }

    fun onNoteEvent(id: Int, notePos: Vector2) {
        when (id) {
            0 -> "Miss!"
            1, 2, 3 -> {
                addActor(SpriteActor(Sprite(Texture("textures/note3.png"))).apply {
                    setPosition(notePos.x, notePos.y)
                    setSize(playScreen.noteRadius, playScreen.noteRadius)
                    addAction(
                        Actions.sequence(
                            Actions.parallel(
                                Actions.fadeOut(0.3f),
                                Actions.sizeBy(playScreen.noteRadius * 1.75f, playScreen.noteRadius * 1.75f, 0.3f)
                            ),
                            Actions.removeActor()
                        )
                    )
                })
            }

            4 -> "Too early!"
            5 -> "Too far!"
            else -> "Unknown"
        }
    }

    fun addActorBeforeNotes(actor: Actor) {
        root.addActorBefore(notesDrawer, actor)
    }
}
