package com.github.catomon.polly.playscreen.playstage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.playscreen.Note
import com.github.catomon.polly.assets
import com.github.catomon.polly.playscreen.NoteListener
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.scene2d.actions.AccelAction
import com.github.catomon.polly.utils.SpriteActor
import kotlin.random.Random

class PlayStage(val playScreen: PlayScreen) : Stage(ScreenViewport(playScreen.camera), playScreen.batch), NoteListener {

    val background = BackgroundActor(Sprite(Texture("bg.jpg")))
    val centerActor = CenterActor(playScreen)
    val notesDrawer = NotesDrawer(playScreen)

    private val noteMiss = assets.mainAtlas.findRegion("note2")
    private val noteHit = assets.mainAtlas.findRegion("note2")

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
                addActor(SpriteActor(Sprite(noteMiss)).apply {
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

            1, 2, 3, 7 -> {
                addActor(SpriteActor(Sprite(noteHit)).apply {
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

                if (note.visual >= 0 && !note.tracingNext) {
                    addActorBeforeNotes(SpriteActor(Sprite(notesDrawer.getNoteTexture(note.visual))).apply {
                        setSize(playScreen.noteRadius * 3, playScreen.noteRadius * 3)
                        setPosition(notePos.x, notePos.y)
                        addAction(Actions.parallel(
                            Actions.moveBy(Random.nextFloat() * 200 - 100, 0f, 1.5f, Interpolation.fastSlow),
                            Actions.sequence(
                                Actions.moveBy(0f, Random.nextFloat() * 100, 0.3f, Interpolation.fastSlow),
                                AccelAction {
                                    y -= it * 25
                                    y < -viewport.worldHeight - playScreen.noteRadius * 2
                                },
                                Actions.removeActor()
                            ),
                        ))
                    })
                }
            }
        }
    }

    fun addActorBeforeNotes(actor: Actor) {
        root.addActorBefore(notesDrawer, actor)
    }
}
