package com.github.catomon.polly.playstage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.Note
import com.github.catomon.polly.PlayScreen
import com.github.catomon.polly.gameplay.NoteListener
import com.github.catomon.polly.utils.SpriteActor

class PlayStage(val playScreen: PlayScreen) : Stage(ScreenViewport(playScreen.camera), playScreen.batch), NoteListener {

    val background = BackgroundActor(Sprite(Texture("bg.jpg")))
    val centerActor = CenterActor(playScreen)
    val notesDrawer = NotesDrawer(playScreen)

    init {
        addActor(background)
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

    override fun onNoteEvent(id: Int, note: Note) {
        val notePos = with(playScreen) { note.calcPosition() }
        when (id) {
            0 -> {
                //todo leak
                addActor(SpriteActor(Sprite(Texture("textures/note2.png"))).apply {
                    setSize(playScreen.noteRadius * 2, playScreen.noteRadius * 2)
                    setPosition(notePos.x, notePos.y)
                    addAction(
                        Actions.sequence(
                            Actions.parallel(Actions.moveBy(0f, -16f, 1f), Actions.fadeOut(1f)),
                            Actions.removeActor()
                        )
                    )
                })
            }

            1, 2, 3 -> {
                //todo leak
                addActor(SpriteActor(Sprite(Texture("textures/note2.png"))).apply {
                    setSize(playScreen.noteRadius, playScreen.noteRadius)
                    setPosition(notePos.x, notePos.y)
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
