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

    val noteMap = loadNoteMap("Jun.A - Bucuresti no Ningyoushi (Ryaldin) [Lunatic].osu")

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
    val noteClickTimeWindow = 0.200f
    val missClickRad get() = circleRadius - circleRadius * 2 * ((noteClickTimeWindow / noteSpawnTime))
    val earlyClickRad get() = circleRadius + circleRadius * 2 * (noteClickTimeWindow / noteSpawnTime)

    var isTracing = false

    val stats = Stats()

    private val shapes = ShapeRenderer().apply { setAutoShapeType(true) }
    private val debugRenderer = DebugRenderer(this, shapes)

    private var paused = false
    private var autoPlay = false

    val noteListeners = Array<NoteListener>()

    val playStage = PlayStage(this)
    val playHud = PlayHud(this)


    init {
        Gdx.input.inputProcessor = PlayInputProcessor(this)
        AudioManager

//        val osuMap = OsuParser.parse(Gdx.files.internal("maps/3L - Endless night (sjoy) [Eternal].osu").readString())
//        println(osuMap)

        noteListeners.add(playStage)
        noteListeners.add(playHud)
    }

    var action: (() -> Unit)? = {
        AudioManager.testMusic.play()
        time = AudioManager.testMusic.position
    }

    private fun update(delta: Float) {
        if (paused) return

        if (action != null) {
            time += delta
        } else {
            time = AudioManager.testMusic.position
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
                    onNoteEvent(0, note.calcPosition(Vector2()))
                }

                if (autoPlay) {
                    if (note.timing <= time) {
                        notes.removeLast()
                        onNoteEvent(1, note.calcPosition(Vector2()))
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

        if (Const.DEBUG)
            debugRenderer.draw()
    }

    fun calcNoteTimeLeft(note: Note): Float = note.calcTimeLeft()

    fun Note.calcTimeLeft(): Float = timing - time - noteClickTimeWindow

    fun calcNotePosition(note: Note, vector2: Vector2 = Vector2()): Vector2 = note.calcPosition(vector2)

    fun Note.calcPosition(vector2: Vector2): Vector2 {
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

    private fun onNoteEvent(id: Int, notePos: Vector2) {
        when (id) {
            0 -> stats.combo = 0
            1, 2, 3 -> {
                stats.combo++
                stats.score += SCORE_GAIN_GREAT
            }
        }

        noteListeners.forEach { it.onNoteEvent(id, notePos) }
    }

    fun clickNote() {
        val note = noteMap.chunks.lastOrNull()?.notes?.lastOrNull() ?: return
        val notePos = note.calcPosition(Vector2())
        val isInTiming = note.timing > time - noteClickTimeWindow && note.timing < time + noteClickTimeWindow
        val clickerPos = calcClickerPos(Vector2())
        val clickerToNoteDst = Vector2.dst(clickerPos.x, clickerPos.y, notePos.x, notePos.y)
        val curPointerRad = pointerSize * (mapSize / 2)
        val isPointerNear = clickerToNoteDst <= curPointerRad * 2
        if (isInTiming && isPointerNear) {
            noteMap.chunks.last().notes.removeLast()

            if (note.tracingNext)
                isTracing = true
            else {
                if (note.tracingPrev)
                    isTracing = false

                onNoteEvent(1, notePos)
            }
        } else {
            if (isTracing) {
                isTracing = false

                noteMap.chunks.last().notes.removeLast()
                onNoteEvent(0, notePos)
            } else {
                if (note.timing - time > noteSpawnTime / 4f) {
                    onNoteEvent(4, notePos)
                } else {
                    if (clickerToNoteDst <= curPointerRad * 2) {
                        noteMap.chunks.last().notes.removeLast()
                        onNoteEvent(0, notePos)
                    } else {
                        onNoteEvent(5, notePos)
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

