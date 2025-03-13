package com.github.catomon.moemarathon.playscreen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.atan2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.Const.IS_MOBILE
import com.github.catomon.moemarathon.Const.SCORE_GAIN_GREAT
import com.github.catomon.moemarathon.Const.SCORE_GAIN_OK
import com.github.catomon.moemarathon.Const.SCORE_GAIN_TRACE
import com.github.catomon.moemarathon.GameMain.Companion.screenHeight
import com.github.catomon.moemarathon.GameMain.Companion.screenWidth
import com.github.catomon.moemarathon.difficulties.PlaySettings
import com.github.catomon.moemarathon.mainmenu.StatsStage
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.playstage.PlayStage
import com.github.catomon.moemarathon.playscreen.ui.PlayHud
import com.github.catomon.moemarathon.utils.addCover
import com.github.catomon.moemarathon.utils.removeCover
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisWindow
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PlayScreen(
    val gameMap: GameMap,
    val playSets: PlaySettings,
) : ScreenAdapter() {

    object Config {
        val defaultGameplay = if (IS_MOBILE) Gameplay.POINTER else Gameplay.BOTH
        var gameplay = defaultGameplay

        //amount of thing where notes should land idk
        var hitZonesAmount = 6

//        var layout = 0
    }

    enum class Gameplay {
        POINTER, KEYBOARD, BOTH
    }

    private val initFirst = (0).also {
        Config.hitZonesAmount = playSets.hitZonesAmount
    }

    val camera = OrthographicCamera().apply {
        setToOrtho(false)
    }
    val batch = SpriteBatch()

    val noteMap = MapsManager.createNoteMap(gameMap.osuBeatmap)

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

    val pointerSize = 0.05f

    var noteSpawnTime = 1f //1 hard //3.5f
    val noteClickTimeWindow = 0.200f // actual time window is twice this value
    val noteClickGreat = 0.075f // actual time window is twice this value
    val missClickRad get() = circleRadius - circleRadius * 2 * ((noteClickTimeWindow / noteSpawnTime))
    val earlyClickRad get() = circleRadius + circleRadius * 2 * (noteClickTimeWindow / noteSpawnTime)

    var isTracing = false
    var tracingButton = -1

    private val tempVec3 = Vector3()
    fun getPointer(): Vector3 {
        return tempVec3.apply {
            if (noAim) {
                x = camera.position.x
                y = camera.position.y
            } else {
                x = Gdx.input.x.toFloat()
                y = Gdx.input.y.toFloat()
                camera.unproject(tempVec3)
            }
        }
    }

    val stats = Stats()

    private val shapes = ShapeRenderer().apply { setAutoShapeType(true) }
    private val debugRenderer = DebugRenderer(this, shapes)

    val noteListeners = Array<NoteListener>()

    var paused = false
        set(value) {
            field = value

            if (action == null) {
                if (value)
                    AudioManager.pauseMapMusic()
                else
                    AudioManager.playMapMusic()
            }
        }
    var autoPlay = false
    var noAim = playSets.noAim
    var skin: Skin = Skins.getSkin(GamePref.userSave.skin) ?: Skins.lucky
    var noHoldNotes = true
    var isDone = false
    var debug = false

    val playStage = PlayStage(this)
    val playHud = PlayHud(this)

    init {
        Config.hitZonesAmount = playSets.hitZonesAmount

        Gdx.input.inputProcessor = InputMultiplexer(playHud, PlayInputProcessor(this))

        noteListeners.add(playStage)
        noteListeners.add(playHud)
    }

    private var isReady = false
    fun ready() {
        if (isReady) return

        noteSpawnTime = playSets.noteSpawnTime
        noHoldNotes = playSets.noHoldNotes

        ///

        AudioManager.loadMapMusic(gameMap)

        if (noHoldNotes) {
            noteMap.chunks.forEach { chunk ->
                chunk.notes.forEach { note ->
                    note.tracingNext = false; note.tracingPrev = false
                }
            }
        }

        if (skin.noteEnemy.isEmpty()) {
            noteMap.chunks.forEach { chunk ->
                chunk.notes.forEach { note ->
                    note.visual = -1
                }
            }
        }

        GamePref.userSave.let { userSave ->
            var needSave = false
            userSave.notify.removeIf {
                if (it == "tutorial") {
                    paused = true
                    playHud.addCover()
                    playHud.addActor(VisWindow("Press corresponding button when a note reaches the hit zone:").also { window ->
                        window.centerWindow()
                        window.add(VisImage(assets.mainAtlas.findRegion("tutor"))).size(400f, 400f)
                        window.row()
                        window.add(newTextButton("OK!").addChangeListener {
                            window.remove()
                            playHud.removeCover()
                            paused = false
                        })
                        window.pack()
                    })
                    needSave = true
                    true
                } else false
            }

            if (needSave) {
                GamePref.userSave = userSave
                GamePref.save()
            }
        }

        isReady = true
    }

    var action: (() -> Unit)? = {
        AudioManager.playMapMusic()
        time = AudioManager.getMapMusicPosition()
    }

    private fun update(delta: Float) {
        playHud.act()

        if (paused) return

        if (action != null) {
            time += delta
        } else {
            time = AudioManager.getMapMusicPosition()
        }

        if (action != null && time >= 0) {
            action?.invoke()
            action = null
        }

        updateNotes()

        playStage.act()
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

                    onNoteEvent(NoteListener.MISS, notes.removeLast())

//                    if (noteMap.chunks.lastOrNull()?.notes?.lastOrNull()?.tracingPrev == true)
//                        onNoteEvent(NoteListener.MISS, noteMap.chunks.lastOrNull()?.notes?.removeLast()!!)
                }

                if (autoPlay) {
                    if (note.timing <= time && !notes.isEmpty) {
                        notes.removeLast()
                        onNoteEvent(NoteListener.HIT, note)
                    }
                }
            }

            if (notes.isEmpty) {
                noteMap.chunks.removeLast()
            }
        } else {
            onDone()
        }
    }

    fun onDone() {
        if (isDone) return
        playStage.addAction(Actions.sequence(Actions.delay(if (Const.IS_RELEASE) 2f else 0f), Actions.run {
            game.screen = game.menuScreen
            game.menuScreen.stage?.background?.sprite = Sprite(playStage.background.sprite)
            game.menuScreen.changeStage(StatsStage(this))
        }))
        isDone = true
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
        val pointer = getPointer()
        val pointerX = pointer.x
        val pointerY = pointer.y

        val cameraX = camera.position.x
        val cameraY = camera.position.y
        val angle = atan2(cameraY - pointerY, cameraX - pointerX)
        val clickerX = cameraX + -circleRadius * cos(angle)
        val clickerY = cameraY + -circleRadius * sin(angle)

        return vector2.set(clickerX, clickerY)
    }

    private fun onNoteEvent(id: Int, note: Note) {
        when (id) {
            NoteListener.MISS -> {
                stats.combo = 0
                stats.misses++
            }

            1, 2, 3 -> {
                stats.combo++
                stats.score +=
                    if (note.tracingPrev) SCORE_GAIN_TRACE
                    else if (note.isGreat()) SCORE_GAIN_GREAT
                    else SCORE_GAIN_OK
                if (note.tracingPrev) stats.greats++
                else if (note.isGreat()) stats.greats++
                else stats.oks++
            }

            7 -> {
                stats.combo++
                stats.score += SCORE_GAIN_TRACE
                stats.greats++
            }
        }

        if (stats.maxCombo < stats.combo)
            stats.maxCombo = stats.combo

        noteListeners.forEach { it.onNoteEvent(id, note) }
    }

    fun processButtonDown(button: Int) {
        playStage.hitZonesDrawer.onHitZoneActivated(hitZoneByButton(button))

        val note = noteMap.chunks.lastOrNull()?.notes?.lastOrNull() ?: return
        val notePos = note.calcPosition(Vector2())
        val isInTiming = note.timing > time - noteClickTimeWindow && note.timing < time + noteClickTimeWindow
        val clickerPos = calcClickerPos(Vector2())
        val clickerToNoteDst = if (Config.gameplay == Gameplay.POINTER) Vector2.dst(
            clickerPos.x,
            clickerPos.y,
            notePos.x,
            notePos.y
        ) else 0f
        val curPointerRad = pointerSize * (mapSize / 2)
        val isHitZoneActivated = isHitZoneActivated(clickerToNoteDst, curPointerRad, note, button)
        if (isInTiming && isHitZoneActivated) {
            noteMap.chunks.last().notes.removeLast()

            if (note.tracingNext) {
                isTracing = true
                tracingButton = button

                onNoteEvent(NoteListener.HIT, note)
            } else {
                if (isTracing) {
                    isTracing = false
                    tracingButton = -1
                    onNoteEvent(NoteListener.HIT_TRACE, note)
                } else {
                    onNoteEvent(NoteListener.HIT, note)
                }
            }
        } else {
            if (isTracing) {
                isTracing = false

                onNoteEvent(NoteListener.MISS, note)
            } else {
                if (note.timing - time > noteSpawnTime / 4f) {
                    onNoteEvent(NoteListener.TOO_EARLY, note)
                } else {
                    if (clickerToNoteDst <= curPointerRad * 2) {
                        onNoteEvent(NoteListener.MISS, note)
                    } else {
                        onNoteEvent(NoteListener.TOO_FAR, note)
                    }
                }
            }
        }
    }

    fun getHitZone(note: Note) = ((Config.hitZonesAmount + 1) * note.initialPosition).toInt() + 1

    fun hitZoneByButton(button: Int): Int {
        //todo different hitZoneAmount
        return when (button) {
            Input.Keys.F -> 3
            Input.Keys.D -> 4
            Input.Keys.S -> 5
            Input.Keys.J -> 2
            Input.Keys.K -> 1
            Input.Keys.L -> 6
            else -> -1
        }
    }

    private fun isHitZoneActivated(
        clickerToNoteDst: Float,
        curPointerRad: Float,
        note: Note,
        button: Int
    ): Boolean {
        fun isKeyHitZonePressed(): Boolean {
            val hitZone = getHitZone(note)
//            logMsg("Key pressed: hitZone=$hitZone; key=$button.")
            return when (button) {
                Input.Keys.F -> {
                    hitZone == 3
                }

                Input.Keys.D -> {
                    hitZone == 4
                }

                Input.Keys.S -> {
                    hitZone == 5
                }

                Input.Keys.J -> {
                    hitZone == 2
                }

                Input.Keys.K -> {
                    hitZone == 1
                }

                Input.Keys.L -> {
                    hitZone == 6
                }

                else -> false
            } || Config.hitZonesAmount > 9
        }

        return when (Config.gameplay) {
            Gameplay.POINTER -> {
                (clickerToNoteDst <= curPointerRad * 2) || noAim
            }

            Gameplay.KEYBOARD -> {
                isKeyHitZonePressed()
            }

            Gameplay.BOTH -> {
//                if (button in 0..1 || button == Input.Keys.Z || button == Input.Keys.X) {
//                    (clickerToNoteDst <= curPointerRad * 2) || noAim
//                } else {
                    isKeyHitZonePressed()
//                }
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
