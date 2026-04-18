package com.github.catomon.moemarathon.playscreen.playstage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.moemarathon.Skins
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.ui.actions.AccelAction
import com.github.catomon.moemarathon.utils.SpriteActor
import com.github.catomon.moemarathon.utils.copyAndScale
import com.github.catomon.moemarathon.utils.cornerX
import com.github.catomon.moemarathon.utils.cornerY
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign
import kotlin.random.Random


class PlayStage(val playScreen: PlayScreen) : Stage(ScreenViewport(playScreen.camera), playScreen.batch), NoteListener {

    var bgTextureScale = 1f

    val bgTexture = run {
        val texture = playScreen.gameMap.newBackgroundTexture()
        if (bgTextureScale == 1f) {
            texture
        } else {
            val bg = texture.copyAndScale(bgTextureScale)
            texture.dispose()
            bg
        }
    }

    val background = BackgroundActor(Sprite(bgTexture))
    private val centerActor: Actor = when (playScreen.skin.centerType) {
        Skins.ANI_DIR_CENTER -> AniDirCenter(playScreen)
        Skins.ANIMATED_CENTER -> AnimatedCenter(playScreen, playScreen.skin.center, playScreen.skin.timingsCircle)
        Skins.BEAT_ANI_CENTER -> BeatAnimatedCenter(playScreen, playScreen.skin.timingsCircle)
        else -> StaticCenter(playScreen, playScreen.skin.timingsCircle)
    }
    val hitZonesDrawer = HitZonesDrawer(playScreen)
    val notesDrawer = NotesDrawer(playScreen)

    val beatLight = BackgroundActor(Sprite(Texture(Gdx.files.internal("textures/beatlight.png")))).also {
        it.scale = BackgroundActor.Scale.FILL
    }

    private val noteMiss = assets.mainAtlas.findRegion(playScreen.skin.miss)
    private val noteHit = assets.mainAtlas.findRegion(playScreen.skin.pop)

    private val particlePool = ParticleEffectPool(ParticleEffect().apply {
        load(
            Gdx.files.internal("particles/note_pop.p"),
            assets.mainAtlas
        )
    }, 24, 32)
    private val effects = Array<PooledEffect>(32)

    init {
        addActor(background)
        addActor(beatLight)
        addActor(centerActor)
        addActor(hitZonesDrawer)
        addActor(notesDrawer)
    }

    override fun draw() {
        val camera = viewport.camera
        camera.update()

        if (!root.isVisible) return

        beatLight.color.a = 0.4f + playScreen.beat * .4f

        val batch = this.batch
        batch.projectionMatrix = camera.combined
        batch.begin()
        root.draw(batch, 1f)

        effects.forEachIndexed { i, e ->
            e.update(Gdx.graphics.deltaTime)
            e.draw(batch)
            if (e.isComplete) {
                e.free()
                effects.removeIndex(i)
            }
        }

        batch.end()
    }

    override fun onNoteEvent(id: Int, note: Note) {
        val notePos = with(playScreen) { note.calcPosition() }
        when (id) {
            NoteListener.MISS -> {
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

                val popEffect = particlePool.obtain()
                popEffect.setPosition(notePos.x, notePos.y)
                effects.add(popEffect)

                if (note.visual >= 0 && !note.tracingNext) {
                    addActorBeforeNotes(SpriteActor(Sprite(notesDrawer.getNoteTexture(note.visual))).apply {
                        setSize(playScreen.noteRadius * 3, playScreen.noteRadius * 3)
                        setPosition(notePos.x, notePos.y)
                        addAction(
                            Actions.parallel(
                                Actions.moveBy(Random.nextFloat() * 200 - 100, 0f, 1.5f, Interpolation.fastSlow),
                                Actions.sequence(
                                    Actions.moveBy(0f, Random.nextFloat() * 100, 0.3f, Interpolation.fastSlow),
                                    AccelAction {
                                        y -= it * 25
                                        y < -viewport.worldHeight - playScreen.noteRadius * 2
                                    },
                                    Actions.removeActor()
                                ),
                            )
                        )
                    })
                }
            }
        }
    }

    fun playStarEffect() {
        val camera = this.playScreen.camera
        this.addActorBeforeNotes(
            SpriteActor(Sprite(assets.mainAtlas.findRegion("star_big"))).apply {
                val size = Random.nextFloat()
                setSize(size * (sprite.width / 3) + 64, size * (sprite.height / 3) + 64)

                val biased = biasedEdge01()
                val x = camera.cornerX() + biased * camera.viewportWidth

                val distFromCenter = abs(biased - 0.5f)

                val edgeFactor = (distFromCenter * 2f).pow(2f)

                val baseMoveY = 256f
                val extraMoveY = 256f * edgeFactor
                val totalMoveY = baseMoveY + extraMoveY

                setPosition(x, camera.cornerY() - height / 2)

                addAction(
                    Actions.sequence(
                        Actions.parallel(
                            Actions.moveBy(0f, totalMoveY, 1.25f),
                            Actions.fadeOut(1f),
                            Actions.scaleTo(0f, 0f, 1.75f)
                        ),
                        Actions.removeActor()
                    )
                )
            }
        )
    }

    private fun biasedEdge01(power: Float = 1.5f): Float {
        val u = Random.nextFloat()
        val d = u - 0.5f
        val s = sign(d) * (1f - (1f - 2f * abs(d)).pow(power))
        return 0.5f + 0.5f * s
    }

    fun addActorBeforeNotes(actor: Actor) {
        root.addActorBefore(notesDrawer, actor)
    }

    override fun dispose() {
        super.dispose()

        bgTexture.dispose()
        beatLight.sprite?.texture?.dispose()
    }
}
