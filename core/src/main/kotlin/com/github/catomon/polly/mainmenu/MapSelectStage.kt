package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.polly.GamePref
import com.github.catomon.polly.assets
import com.github.catomon.polly.difficulties.DefaultPlaySets
import com.github.catomon.polly.difficulties.PlaySettings
import com.github.catomon.polly.difficulties.Ranks
import com.github.catomon.polly.game
import com.github.catomon.polly.map.GameMap
import com.github.catomon.polly.map.MapsManager
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.*
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.github.catomon.polly.widgets.addChangeListener
import kotlin.concurrent.thread

class MapSelectStage(
    val playSets: PlaySettings = DefaultPlaySets(),
    val mapFileNames: List<String> = playSets.maps,
) :
    BgStage() {

    private var isLoading = false
    private var loadedItems = emptyList<GameMap>()

    private var mapList = ListView(object : ArrayListAdapter<GameMap, VisLabel>(ArrayList(loadedItems)) {
        override fun createView(item: GameMap): VisLabel {
            return VisLabel(item.file.nameWithoutExtension()).also { it.setFontScale(0.5f) }
        }
    })

    private val textureBgCache = mutableMapOf<String, Texture>()

    init {
        addActor(background)
        loadMaps()
    }

    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Input.Keys.F5) {
            loadMaps()
        }

        return super.keyDown(keyCode)
    }

    fun loadMaps() {
        if (isLoading) return

        thread {
            val buttonGroup = ButtonGroup<MapListItem>()
            try {
                addCover()
                isLoading = true
                loadedItems =
                    (if (mapFileNames.isEmpty())
                        MapsManager.collectMapFiles().map { GameMap(it) }
                    else
                        MapsManager.collectMapFiles().map { GameMap(it) }
                            .filter { mapFileNames.any { mapFileName -> mapFileName == it.file.name() } }
                        ).reversed()

                mapList = ListView(object : ArrayListAdapter<GameMap, MapListItem>(ArrayList(loadedItems)) {
                    init {
                        buttonGroup.setMaxCheckCount(1)
                        buttonGroup.setMinCheckCount(0)
                        selectionMode = SelectionMode.DISABLED
                    }

                    var selected: MapListItem? = null

                    override fun createView(item: GameMap): MapListItem {
                        return MapListItem(item.file.nameWithoutExtension(), item).also { newMapListItem ->
                            buttonGroup.add(newMapListItem)

                            newMapListItem.addChangeListener {
                                if (it.isChecked) {
                                    background.sprite =
                                        Sprite(
                                            textureBgCache[it.map.file.name()] ?: throw IllegalStateException("no bg")
                                        )

                                    if (selected == null)
                                        selected = it
                                }
                            }
                            newMapListItem.addClickListener {
                                if (buttonGroup.checked == selected) {
                                    this@MapSelectStage.fadeInAndThen(0.5f) {
                                        game.screen = PlayScreen(newMapListItem.map, playSets)
                                    }
                                }
                            }
                            newMapListItem.addClickListener {
                                if (newMapListItem.isChecked) {
                                    selected = newMapListItem
                                }
                            }
                        }
                    }
                }).also { listView ->
                    listView.header = Actor().also { it.setSize(50f, 50f) }
                }

                root.findActor<VisTable>("listTable")?.remove()
                createTable().apply {
                    name = "listTable"
                    add(mapList.scrollPane).center().fillY().expandY().expandX().fillX()
                }

                createTable(VisTextButton("<Menu").addChangeListener {
                    game.menuScreen.changeStage(MenuStage())
                }).apply {
                    left().bottom()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                removeCover()
                isLoading = false
            }

            Gdx.app.postRunnable {
                loadedItems.forEach {
                    if (!textureBgCache.containsKey(it.file.name()))
                        textureBgCache.put(it.file.name(), it.newBackgroundTexture())
                }

                buttonGroup.buttons.first().isChecked = true
                buttonGroup.setMinCheckCount(1)
                scrollFocus = mapList.scrollPane
            }
        }
    }

    override fun dispose() {
        super.dispose()

        textureBgCache.forEach { it.value.dispose() }
        textureBgCache.clear()
    }

    class MapListItem(val mapName: String, val map: GameMap) :
        Button(VisUI.getSkin().get("mapItem", ButtonStyle::class.java)) {
        init {
            add(VisImage().also { image ->
                GamePref.userSave.mapRanks.forEach {
                    if (it.key == map.file.name()) {
                        image.drawable = SpriteDrawable(assets.mainAtlas.createSprite(Ranks.getRankChar(it.value)))
                    }
                }
            }).size(50f)
            add(VisLabel(if (mapName.length > 100) mapName.substring(0, 100) else mapName).also {
                it.setFontScale(0.4f)
            }).height(50f)
        }
    }
}
