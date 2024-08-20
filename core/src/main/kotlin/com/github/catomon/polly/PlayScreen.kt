package com.github.catomon.polly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.atan2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.github.catomon.polly.map.loadNoteMap
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PlayScreen : ScreenAdapter() {

    val camera = OrthographicCamera(100f, 100f).apply {
        setToOrtho(false)
    }
    private val batch = SpriteBatch()

    val playHud = PlayHud()

    val noteMap = loadNoteMap("cYsmix feat. Emmy - Tear Rain (jonathanlfj) [Insane].osu")

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

    var time = 0f

    var pointerX: Float = 0f
    var pointerY: Float = 0f
    val pointerSize = 0.05f

    val noteSpawnTime = 3
    val noteClickTimeWindow = 0.150f
    val missClickRad get() = circleRadius - circleRadius * 2 * ((noteClickTimeWindow / noteSpawnTime))
    val earlyClickRad get() = circleRadius + circleRadius * 2 * (noteClickTimeWindow / noteSpawnTime)
    //circleRadius - circleRadius * ((noteClickTimeWindow / noteSpawnTime) * 2)
    // circleRadius - circleRadius * (noteClickTimeWindow / noteSpawnTime)

    var isTracing = false

    private val shapes = ShapeRenderer().apply { setAutoShapeType(true) }

    private var paused = false

    private val noteSprite = Sprite(Texture("textures/note.png"))
    private val clickZoneSprite = Sprite(Texture("textures/click_zone.png"))

    companion object {
        var width = 100
        var height = 100
    }

    init {
        Gdx.input.inputProcessor = PlayScreenIP(this)

        AudioManager.music.play()
    }

    fun update(delta: Float) {
        if (paused) return

        time += delta

        updateNotes()

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
                    playHud.onNoteEvent(0, note.calcPosition(Vector2()))
                }
            }

            if (notes.isEmpty) {
                noteMap.chunks.removeLast()
            }
        }
    }

    fun draw() {
        val cameraX = camera.position.x
        val cameraY = camera.position.y

        //drawShapeRenderer()

        batch.projectionMatrix = camera.combined
        batch.begin()

        clickZoneSprite.setSize(circleRadius * 2 + noteRadius * 2, circleRadius * 2 + noteRadius * 2)
        clickZoneSprite.setPosition(cameraX - clickZoneSprite.width / 2, cameraY - clickZoneSprite.height / 2)
        clickZoneSprite.setAlpha(0.5f)
        clickZoneSprite.draw(batch)

        drawNotes()

        batch.end()

        playHud.draw()
    }

    private fun drawNotes() {
        val notes = noteMap.chunks.lastOrNull()?.notes?.toMutableList()
        if (notes != null) {
            notes.addAll(noteMap.chunks.elementAtOrNull(noteMap.chunks.size - 2)?.notes ?: emptyList())
            val notePos = Vector2()
            for (note in notes) {
                val timeLeft = note.timing - time - noteClickTimeWindow
                if (timeLeft <= noteSpawnTime) {
                    note.calcPosition(notePos)

                    noteSprite.setSize(noteRadius * 2, noteRadius * 2)
                    noteSprite.setPosition(notePos.x - noteSprite.width / 2, notePos.y - noteSprite.height / 2)
                    noteSprite.setAlpha(0f)
                    var a = (noteSpawnTime - timeLeft) / (noteSpawnTime * 0.1f)
                    if (a > 1) a = 1f
                    noteSprite.setAlpha(a)
                    noteSprite.draw(batch)

                    if (note.tracingNext) {

                        val nextNotePos = notes.getOrNull(notes.indexOf(note) - 1)?.calcPosition(Vector2())
                        if (nextNotePos != null) {
                            //shapes.line(notePos.x, notePos.y, nextNotePos.x, nextNotePos.y)
                        }
                    }
                }
            }
        }
    }

    private fun drawShapeRenderer() {
        val cameraX = camera.position.x
        val cameraY = camera.position.y

        shapes.projectionMatrix = camera.combined
        shapes.begin()

        shapes.color = Color.LIGHT_GRAY
        shapes.rect(cameraX - mapSize / 2, cameraY - mapSize / 2, mapSize, mapSize)

        shapes.color = Color.WHITE
        shapes.circle(cameraX, cameraY, circleRadius)
        shapes.color = Color.GRAY
        shapes.circle(cameraX, cameraY, missClickRad)
        shapes.circle(cameraX, cameraY, earlyClickRad)

        val clickerPos = calcClickerPos(Vector2())
        shapes.color = Color.LIGHT_GRAY
        shapes.circle(clickerPos.x, clickerPos.y, pointerSize * (mapSize / 2))
        //        shapes.circle(pointerX, pointerY, pointerSize * (mapSize / 2))

        val notes = noteMap.chunks.lastOrNull()?.notes?.toMutableList()
        if (notes != null) {
            notes.addAll(noteMap.chunks.elementAtOrNull(noteMap.chunks.size - 2)?.notes ?: emptyList())
            val notePos = Vector2()
            for (note in notes) {
                val timeLeft = note.timing - time - noteClickTimeWindow
                if (timeLeft <= noteSpawnTime) {
                    note.calcPosition(notePos)
                    shapes.color =
                        if (note.tracingNext)
                            Color.GREEN
                        else
                            if (note.tracingPrev) Color.RED
                            else Color.WHITE //colors.getOrNull(note.color) ?: Color.GREEN
                    shapes.point(notePos.x, notePos.y, 0f)
                    shapes.circle(notePos.x, notePos.y, noteRadius)

                    if (note.tracingNext) {

                        val nextNotePos = notes.getOrNull(notes.indexOf(note) - 1)?.calcPosition(Vector2())
                        if (nextNotePos != null)
                            shapes.line(notePos.x, notePos.y, nextNotePos.x, nextNotePos.y)
                    }
                }
            }
        }

        shapes.end()
    }

    // (3 - 2 - 0.1f) / 2f
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
                playHud.onNoteEvent(1, notePos)
            }
        } else {
            if (isTracing) {
                isTracing = false

                noteMap.chunks.last().notes.removeLast()
                playHud.onNoteEvent(0, notePos)
            } else {
                if (note.timing - time > noteSpawnTime / 4f) {
                    playHud.onNoteEvent(4, notePos)
                } else {
                    if (clickerToNoteDst <= curPointerRad * 2) {
                        noteMap.chunks.last().notes.removeLast()
                        playHud.onNoteEvent(0, notePos)
                    } else {
                        playHud.onNoteEvent(5, notePos)
                    }
                }
            }
        }
    }

    override fun resize(newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight

        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.position.set(newWidth / 2f, newHeight / 2f, 0f)
        camera.update()

//        mapSize = min(width , height).toFloat()
        mapSize = min(width - width * mapOffset, height - height * mapOffset)

        playHud.viewport.update(newWidth, newHeight, true)
    }
}

class PlayScreenIP(val playScreen: PlayScreen) : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        playScreen.clickNote()

        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (playScreen.isTracing) {
            playScreen.clickNote()
        }

        return super.keyUp(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playScreen.clickNote()

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (playScreen.isTracing) {
            playScreen.clickNote()
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        tmpVec.x = screenX.toFloat()
        tmpVec.y = screenY.toFloat()
        playScreen.camera.unproject(tmpVec)
        playScreen.pointerX = tmpVec.x
        playScreen.pointerY = tmpVec.y

        return super.touchDragged(screenX, screenY, pointer)
    }

    private val tmpVec = Vector3()
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        tmpVec.x = screenX.toFloat()
        tmpVec.y = screenY.toFloat()
        playScreen.camera.unproject(tmpVec)
        playScreen.pointerX = tmpVec.x
        playScreen.pointerY = tmpVec.y

        return super.mouseMoved(screenX, screenY)
    }
}
