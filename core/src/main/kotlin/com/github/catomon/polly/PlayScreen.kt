package com.github.catomon.polly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Queue
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class PlayScreen : ScreenAdapter() {

    val camera = OrthographicCamera(100f, 100f)
    private val batch = SpriteBatch()

    val colors = listOf(Color.GREEN, Color.SKY, Color.TAN, Color.BLUE, Color.GOLD)

    val map = Map(Queue<Map.Chunk>(10).apply {
        var second = 2f
        repeat(10) {
            addFirst(Map.Chunk().apply {
                repeat(10) {
                    notes.addFirst(
                        Note(
                            timing = second++,
                            initialPosition = Random.nextFloat(),
                            color = Random.nextInt(0, colors.size)
                        )
                    )
                }
            })
        }
    })

    var mapOffset = 0f

    var circleSize = 0.5f
    var noteSize = 0.1f

    var circleRadius = -1f
    var noteRadius = -1f

    var mapSize = 100f
        set(value) {
            field = value

            circleRadius = (circleSize * mapSize) / 2
            noteRadius = (noteSize * mapSize) / 2
        }

    var time = 0f

    var pointerX: Float = 0f
    var pointerY: Float = 0f
    val pointerSize = 0.125f

    val noteSpawnTime = 2
    val noteClickTimeWindow = 0.1f

    private val shapes = ShapeRenderer().apply { setAutoShapeType(true) }

    private var paused = false

    companion object {
        var width = 100
        var height = 100
    }

    init {
        Gdx.input.inputProcessor = PlayScreenIP(this)
    }

    fun update(delta: Float) {
        if (paused) return

        time += delta / 2

        for (chunk in map.chunks) {
            val notes = chunk.notes
            val note = notes.lastOrNull()

            if (note != null) {
                if (note.timing < time - noteClickTimeWindow) {
                    notes.removeLast()
                }
            }

            if (notes.isEmpty) {
                map.chunks.removeLast()
            }
        }
    }

    fun draw(delta: Float) {
        val cameraX = camera.position.x
        val cameraY = camera.position.y

        shapes.projectionMatrix = camera.combined
        shapes.begin()

        shapes.color = Color.LIGHT_GRAY
        shapes.rect(cameraX - mapSize / 2, cameraY - mapSize / 2, mapSize, mapSize)

        shapes.color = Color.WHITE
//        val visCirRad = circleRadius
//        shapes.circle(cameraX, cameraY, visCirRad)
//        shapes.circle(cameraX, cameraY, visCirRad - noteRadius * 2)
        shapes.circle(cameraX, cameraY, circleRadius)
        shapes.circle(cameraX, cameraY, circleRadius - circleRadius * 0.1f)

        shapes.color = Color.LIGHT_GRAY
        shapes.circle(pointerX, pointerY, pointerSize * (mapSize / 2))

        val notes = map.chunks.lastOrNull()?.notes
        if (notes != null) {
            val notePos = Vector2()
            for (note in notes) {
                val timeLeft = note.timing - time - noteClickTimeWindow
                if (timeLeft <= noteSpawnTime) {
                    note.calcPosition(notePos)
                    shapes.color = colors.getOrNull(note.color) ?: Color.GREEN
                    shapes.point(notePos.x, notePos.y, 0f)
                    shapes.circle(notePos.x, notePos.y, noteRadius)
                }
            }
        }

        shapes.end()
    }

    fun Note.calcPosition(vector2: Vector2): Vector2 {
        val timeLeft = (timing - time - noteClickTimeWindow) / noteSpawnTime
        val cameraX = camera.position.x
        val cameraY = camera.position.y
        val degree = 360 * initialPosition
        val distance = circleRadius + ((mapSize / 2 - circleRadius) * timeLeft)
        val radian = Math.toRadians(degree.toDouble()).toFloat()
        val noteX = cameraX + distance * cos(radian)
        val noteY = cameraY + distance * sin(radian)

        return vector2.set(noteX, noteY)
    }

    override fun render(delta: Float) {
        update(delta)
        draw(delta)
    }

    fun clickNote() {
        val note = map.chunks.lastOrNull()?.notes?.lastOrNull() ?: return

        val notePos = note.calcPosition(Vector2())
        val isInTiming = note.timing > time - noteClickTimeWindow && note.timing < time + noteClickTimeWindow
        val isPointerNear = Vector2.dst(pointerX, pointerY, notePos.x, notePos.y) <= pointerSize * (mapSize / 2)
        if (isInTiming && isPointerNear) {
            map.chunks.last().notes.removeLast()
        }
    }

    override fun resize(newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight

        camera.setToOrtho(false, width.toFloat(), height.toFloat())

//        mapSize = min(width , height).toFloat()
        mapSize = min(width - width * mapOffset, height - height * mapOffset)
    }
}

class PlayScreenIP(val playScreen: PlayScreen) : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        playScreen.clickNote()

        return super.keyDown(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playScreen.clickNote()

        return super.touchDown(screenX, screenY, pointer, button)
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
