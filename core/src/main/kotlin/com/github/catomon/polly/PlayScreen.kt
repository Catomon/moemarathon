package com.github.catomon.polly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.atan2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.github.catomon.polly.Const.SCORE_GAIN_GREAT
import com.github.catomon.polly.Const.SCORE_GAIN_OK
import com.github.catomon.polly.GameMain.Companion.screenHeight
import com.github.catomon.polly.GameMain.Companion.screenWidth
import com.github.catomon.polly.gameplay.NoteListener
import com.github.catomon.polly.gameplay.Stats
import com.github.catomon.polly.map.loadNoteMap
import com.github.catomon.polly.playstage.PlayStage
import com.github.catomon.polly.ui.PlayHud
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PlayScreen : ScreenAdapter() {

    val camera = OrthographicCamera(100f, 100f).apply {
        setToOrtho(false)
    }
    val batch = SpriteBatch()

    val noteMap = loadNoteMap("cYsmix feat. Emmy - Tear Rain (jonathanlfj) [Hard].osu")

    var mapOffset = -1.5f

    var circleSize = 0.20f
    var noteSize = 0.05f

    /* Aka perfect click radius */
    var circleRadius = -1f
    var noteRadius = -1f

    var mapSize = -1f
        set(value) {
            field = value

            circleRadius = (circleSize * (mapSize / 2))
            noteRadius = (noteSize * (mapSize / 2))
        }

    var time = -3f

    var pointerX: Float = 0f
    var pointerY: Float = 0f
    val pointerSize = 0.05f

    val noteSpawnTime = 1f //1 hard //3.5f
    val noteClickTimeWindow = 0.200f // actual time window is twice this value
    val noteClickGreat = 0.075f // actual time window is twice this value
    val missClickRad get() = circleRadius - circleRadius * 2 * ((noteClickTimeWindow / noteSpawnTime))
    val earlyClickRad get() = circleRadius + circleRadius * 2 * (noteClickTimeWindow / noteSpawnTime)

    var isTracing = false
    var tracingButton = -1

    val stats = Stats()

    private val shapes = ShapeRenderer().apply { setAutoShapeType(true) }
    private val debugRenderer = DebugRenderer(this, shapes)

    var paused = false
    var autoPlay = false

    val noteListeners = Array<NoteListener>()

    val playStage = PlayStage(this)
    val playHud = PlayHud(this)

    var debug = false

    init {
        Gdx.input.inputProcessor = PlayInputProcessor(this)
        AudioManager

        noteListeners.add(playStage)
        noteListeners.add(playHud)
    }

    var action: (() -> Unit)? = {
        AudioManager.mapMusic?.play() ?: IllegalStateException("AudioManager.mapMusic must be not null at this point")
        time = AudioManager.mapMusic!!.position
    }

    private fun update(delta: Float) {
        if (paused) return

        if (action != null) {
            time += delta
        } else {
            time = AudioManager.mapMusic!!.position
        }

        if (action != null && time >= 0) {
            action?.invoke()
            action = null
        }

        updateNotes()

        playStage.act()
        playHud.act()
    }

    private fun updateNotes() {
        val chunk = noteMap.chunks.lastOrNull()
        if (chunk != null) {
            val notes = chunk.notes
            val note = notes.lastOrNull()

            if (note != null) {
                if (note.timing < time - noteClickTimeWindow) {
                    if (note.tracingPrev)
                        isTracing = false

                    notes.removeLast()
                    onNoteEvent(NoteListener.MISS, note)
                }

                if (autoPlay) {
                    if (note.timing <= time) {
                        notes.removeLast()
                        onNoteEvent(NoteListener.HIT, note)
                    }
                }
            }

            if (notes.isEmpty) {
                noteMap.chunks.removeLast()
            }
        }
    }

    private fun draw() {
        playStage.draw()

        playHud.draw()

        if (Const.DEBUG || debug)
            debugRenderer.draw()
    }

    fun calcNoteTimeLeft(note: Note): Float = note.calcTimeLeft()

    fun Note.calcTimeLeft(): Float = timing - time - noteClickTimeWindow

    fun calcNotePosition(note: Note, vector2: Vector2 = Vector2()): Vector2 = note.calcPosition(vector2)

    fun Note.isGreat(): Boolean = time - timing in -noteClickGreat..noteClickGreat

    fun Note.calcPosition(vector2: Vector2 = Vector2()): Vector2 {
        val timeLeft = (timing - time) / noteSpawnTime
        val cameraX = camera.position.x
        val cameraY = camera.position.y
        val degree = 360f * initialPosition
        val distance = circleRadius + (circleRadius * 2 * timeLeft)
        val radian = Math.toRadians(degree.toDouble()).toFloat()
        val noteX = cameraX + distance * cos(radian)
        val noteY = cameraY + distance * sin(radian)

        return vector2.set(noteX, noteY)
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    fun calcClickerPos(vector2: Vector2): Vector2 {
        val cameraX = camera.position.x
        val cameraY = camera.position.y
        val angle = atan2(cameraY - pointerY, cameraX - pointerX)
        val clickerX = cameraX + -circleRadius * cos(angle)
        val clickerY = cameraY + -circleRadius * sin(angle)

        return vector2.set(clickerX, clickerY)
    }

    private fun onNoteEvent(id: Int, note: Note) {
        when (id) {
            NoteListener.MISS -> stats.combo = 0
            1, 2, 3 -> {
                stats.combo++
                stats.score += if (note.isGreat()) SCORE_GAIN_GREAT else SCORE_GAIN_OK
            }
        }

        noteListeners.forEach { it.onNoteEvent(id, note) }
    }

    fun clickNote(button: Int) {
        val note = noteMap.chunks.lastOrNull()?.notes?.lastOrNull() ?: return
        val notePos = note.calcPosition(Vector2())
        val isInTiming = note.timing > time - noteClickTimeWindow && note.timing < time + noteClickTimeWindow
        val clickerPos = calcClickerPos(Vector2())
        val clickerToNoteDst = Vector2.dst(clickerPos.x, clickerPos.y, notePos.x, notePos.y)
        val curPointerRad = pointerSize * (mapSize / 2)
        val isPointerNear = clickerToNoteDst <= curPointerRad * 2
        if (isInTiming && isPointerNear) {
            noteMap.chunks.last().notes.removeLast()

            if (note.tracingNext) {
                isTracing = true
                tracingButton = button

                onNoteEvent(NoteListener.NOTE_TRACE_START, note)
            } else {
                if (note.tracingPrev) {
                    isTracing = false
                    tracingButton = -1
                }

                onNoteEvent(NoteListener.HIT, note)
            }
        } else {
            if (isTracing) {
                isTracing = false

                noteMap.chunks.last().notes.removeLast()
                onNoteEvent(NoteListener.MISS, note)
            } else {
                if (note.timing - time > noteSpawnTime / 4f) {
                    onNoteEvent(NoteListener.TOO_EARLY, note)
                } else {
                    if (clickerToNoteDst <= curPointerRad * 2) {
                        noteMap.chunks.last().notes.removeLast()
                        onNoteEvent(NoteListener.MISS, note)
                    } else {
                        onNoteEvent(NoteListener.TOO_FAR, note)
                    }
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = screenWidth.toFloat()
        camera.viewportHeight = screenHeight.toFloat()
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        mapSize = min(screenWidth - screenWidth * mapOffset, screenHeight - screenHeight * mapOffset)

        playHud.viewport.update(width, height, true)
        playStage.viewport.update(width, height, true)
    }
}

