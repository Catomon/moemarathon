package com.github.catomon.polly.utils

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.github.catomon.polly.scene2d.actions.OneAction
import com.kotcrab.vis.ui.widget.VisTable
import com.github.catomon.polly.scene2d.StageCover
import kotlin.math.atan2

fun Texture.copyAndScale(scale: Float): Texture {
    val originalTexture = this
    originalTexture.textureData.prepare()
    val pixmap: Pixmap = originalTexture.textureData.consumePixmap()

    val newWidth = (originalTexture.width * scale).toInt()
    val newHeight = (originalTexture.height * scale).toInt()
    val resizedPixmap = Pixmap(newWidth, newHeight, pixmap.format)
    resizedPixmap.drawPixmap(pixmap, 0, 0, pixmap.width, pixmap.height, 0, 0, newWidth, newHeight)

    val resizedTexture = Texture(resizedPixmap)
    pixmap.dispose()
    resizedPixmap.dispose()

    return resizedTexture
}

fun Color.rgba(r: Int, g: Int, b: Int, a: Int = 255): Color = set(r / 255f, g / 255f, b / 255f, a / 255f)

fun Table.createTable(actor: Actor? = null): VisTable {
    val table = VisTable()
    table.setFillParent(true)

    if (actor != null)
        table.add(actor)

    add(table)

    return table
}

fun Stage.createTable(actor: Actor? = null): VisTable {
    val table = VisTable()
    table.setFillParent(true)

    if (actor != null)
        table.add(actor)

    addActor(table)

    return table
}

fun NinePatchDrawable.scale(scale: Float) {
    topHeight *= scale
    bottomHeight *= scale
    leftWidth *= scale
    rightWidth *= scale
    patch.scale(scale, scale)
}

fun Stage.addCover(alpha: Float = 0.5f) {
    addActor(StageCover(alpha))
}

fun Stage.removeCover() {
    actors.find { it is StageCover }?.remove()
}

fun Stage.fadeInAndThen(duration: Float = 1f, action: Action.() -> Unit) {
    addActor(StageCover().fadeInAndThen(OneAction(action)))
}

fun Stage.fadeInAndThen(duration: Float = 1f, addTime: Float = 0f, action: Action.() -> Unit) {
    addActor(StageCover().fadeInAndThen(OneAction(action), duration, addTime))
}

fun Stage.fadeOut(duration: Float = 0.6f) {
    addActor(StageCover().fadeOutAndRemove(duration))
}

val Stage.worldCenterX: Float get() = width / 2f

val Stage.worldCenterY: Float get() = height / 2f

fun degrees(fromX: Float, fromY: Float, toX: Float, toY: Float): Float {
    return (atan2((toY - fromY).toDouble(), (toX - fromX).toDouble()) * 180.0 / Math.PI).toFloat()
}

val Actor.centerX get() = x + width / 2

val Actor.centerY get() = y + height / 2

operator fun Vector2.plus(vector: Vector2) {
    this.x + vector.x
    this.y + vector.y
}

operator fun Vector2.minus(vector: Vector2) {
    this.x - vector.x
    this.y - vector.y
}

fun Stage.isOffScreen(x: Float, y: Float, threshold: Float = 100f): Boolean {
    val onScreenCoords = root.localToScreenCoordinates(Vector2(x, y))

    return ((onScreenCoords.x < threshold || onScreenCoords.y < threshold)
            || (onScreenCoords.x > viewport.screenWidth - threshold || onScreenCoords.y > viewport.screenHeight - threshold))
}

fun <T : Actor> Actor.addClickSound(sound: Sound): T {
    addClickListener {
        sound.play()
    }

    return this as T
}

inline fun <reified E : Event> Stage.addListener(crossinline func: E.() -> Unit) {
    addListener {
        if (it is E) {
            func(it)
        }

        false
    }
}

inline fun <reified E : Event> Stage.addOneTimeListener(crossinline func: E.() -> Boolean) {
    val listener = object : EventListener {
        override fun handle(event: Event?): Boolean {
            if (event is E) {
                val done = func(event)

                if (done)
                    removeListener(this)
            }

            return false
        }
    }

    addListener(listener)
}

fun <T : Actor> T.addClickListener(function: (InputEvent) -> Unit): T {
    addListener(object : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            super.clicked(event, x, y)

            function(event)
        }
    })

    return this as T
}

/** Sets actor position so its center x == [x] and center y == [y] */
fun Actor.setPosByCenter(x: Float, y: Float) {
    setPosition(x - width / 2, y - height / 2)
}

/** Sets actor position so its center == screen center */
fun Actor.updatePosScrCenter() {
    if (stage == null) throw IllegalStateException("Stage must not be null.")

    setPosition(
        stage.viewport.screenWidth / 2 - width / 2,
        stage.viewport.screenHeight / 2 - height / 2
    )
}

fun Actor.updatePosWrldCenter() {
    if (stage == null) throw IllegalStateException("Stage must not be null.")

    setPosition(
        stage.viewport.worldWidth / 2 - width / 2,
        stage.viewport.worldHeight / 2 - height / 2
    )
}

/** Sets camera position so its left bottom corner has position (0, 0) */
fun Camera.resetPosition() {
    setPosition(viewportWidth / 2, viewportHeight / 2)
}

fun Camera.setPosition(x: Float, y: Float) {
    position.x = x
    position.y = y
}

fun OrthographicCamera.cornerX(): Float = position.x - viewportWidth * zoom / 2
fun OrthographicCamera.cornerY(): Float = position.y - viewportHeight * zoom / 2

fun Sprite.centerX(): Float = x + width / 2
fun Sprite.centerY(): Float = y + height / 2

fun Sprite.setPositionByCenter(x: Float, y: Float) {
    setPosition(x - width / 2, y - height / 2)
}
