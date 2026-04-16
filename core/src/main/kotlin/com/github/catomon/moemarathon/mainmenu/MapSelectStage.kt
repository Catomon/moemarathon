package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.difficulties.DefaultMapSets
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.DefaultPlaySets
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.HardMarathon
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.InsaneMarathon
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.NormalMarathon
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.UnlockedOnlyPlaySets
import com.github.catomon.moemarathon.difficulties.GameMapSet
import com.github.catomon.moemarathon.difficulties.RankUtil
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.*
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable

class MapSelectStage(
    val playSets: GameMapSet = DefaultPlaySets,
    val mapFileNames: List<String> = playSets.maps,
) : BgStage() {

    companion object {
        private val loadedMaps = mutableMapOf<String, GameMap>()
        private val bgTextureCache = mutableMapOf<String, Texture>()
        private val bgLoading = mutableSetOf<String>()
    }

    private var isLoading = false
    private var activeLoadToken = 0L

    private var loadedItems = emptyList<GameMap>()

    private var mapList = ListView(object : ArrayListAdapter<GameMap, VisLabel>(ArrayList(loadedItems)) {
        override fun createView(item: GameMap): VisLabel {
            return VisLabel(item.file.nameWithoutExtension()).also { it.setFontScale(0.5f) }
        }
    })

    private val loadingTable = createTable(newLabel("Collecting maps..."))
    private val userSave = GamePref.userSave

    private var selectedItem: MapListItem? = null

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

    private fun setBackgroundFor(map: GameMap) {
        val key = map.file.name()
        val texture = bgTextureCache[key]
        if (texture != null) {
            background.sprite = Sprite(texture)
        } else {
            loadBackgroundTextureAsync(map)
        }
    }

    private fun loadBackgroundTextureAsync(map: GameMap) {
        val key = map.file.name()
        if (bgTextureCache.containsKey(key) || bgLoading.contains(key)) return
        bgLoading.add(key)

        Thread {
            try {
                Gdx.app.postRunnable {
                    val texture = map.newBackgroundTexture()
                    if (activeLoadToken == 0L) {
                        texture.dispose()
                        bgLoading.remove(key)
                        return@postRunnable
                    }
                    bgTextureCache[key]?.dispose()
                    bgTextureCache[key] = texture
                    bgLoading.remove(key)
                    if (selectedItem?.map?.file?.name() == key) {
                        background.sprite = Sprite(texture)
                    }
                }
            } catch (e: Exception) {
                Gdx.app.postRunnable { bgLoading.remove(key) }
                e.printStackTrace()
            }
        }.start()
    }

    private fun buildMapList(items: List<GameMap>, buttonGroup: ButtonGroup<MapListItem>) {
        mapList = ListView(object : ArrayListAdapter<GameMap, MapListItem>(ArrayList(items)) {
            init {
                buttonGroup.setMaxCheckCount(1)
                buttonGroup.setMinCheckCount(0)
                selectionMode = SelectionMode.DISABLED
            }

            override fun createView(item: GameMap): MapListItem {
                return MapListItem(item.file.nameWithoutExtension(), item).also { newMapListItem ->
                    buttonGroup.add(newMapListItem)

                    newMapListItem.addClickListener {
                        if (buttonGroup.checked == selectedItem) {
                            fadeInAndThen(1f) {
                                var selectedPlaySets = playSets
                                if (selectedPlaySets == UnlockedOnlyPlaySets) {
                                    when {
                                        NormalMarathon.maps.contains(newMapListItem.map.file.name()) -> {
                                            selectedPlaySets = selectedPlaySets.copy(noteSpawnTime = NormalMarathon.noteSpawnTime)
                                        }
                                        HardMarathon.maps.contains(newMapListItem.map.file.name()) -> {
                                            selectedPlaySets = selectedPlaySets.copy(noteSpawnTime = HardMarathon.noteSpawnTime)
                                        }
                                        InsaneMarathon.maps.contains(newMapListItem.map.file.name()) -> {
                                            selectedPlaySets = selectedPlaySets.copy(noteSpawnTime = InsaneMarathon.noteSpawnTime)
                                        }
                                    }
                                }
                                game.screen = PlayScreen(newMapListItem.map, selectedPlaySets)
                            }
                        } else {
                            if (newMapListItem.isChecked) {
                                selectedItem = newMapListItem
                                setBackgroundFor(newMapListItem.map)

                                if (AudioManager.lastMapMusic?.osuBeatmap?.audioFileName == newMapListItem.map.osuBeatmap.audioFileName) {
                                    AudioManager.playMapMusic()
                                } else {
                                    AudioManager.loadMapMusic(newMapListItem.map)
                                    AudioManager.playMapMusic()
                                }
                            }
                        }
                    }
                }
            }
        }).also { listView ->
            listView.header = Actor().also { it.setSize(50f, 50f) }
        }
    }

    fun loadMaps() {
        if (isLoading) return
        isLoading = true
        val loadToken = ++activeLoadToken

        if (root.findActor<VisTable>("listTable") == null) {
            root.findActor<VisTable>("listTable")?.remove()
        }

        addCover()
        loadingTable.isVisible = true

        Thread {
            try {
                val marathonMaps =
                    DefaultMapSets.NormalMarathon.maps + DefaultMapSets.HardMarathon.maps + DefaultMapSets.InsaneMarathon.maps + DefaultMapSets.NonStop.maps

                val allMaps = MapsManager.collectMapFiles().map { file ->
                    loadedMaps[file.name()] ?: GameMap(file).also { loadedMaps[file.name()] = it }
                }

                loadedItems = if (mapFileNames.isEmpty()) {
                    if (playSets == UnlockedOnlyPlaySets) {
                        val userSaveMapRanks = GamePref.userSave.mapRanks
                        allMaps.filter {
                            userSaveMapRanks.contains(it.file.name()) &&
                                (marathonMaps.contains(it.file.name()) || it.file.parent().parent().name() == "marathon")
                        }
                    } else {
                        allMaps.filter {
                            !(marathonMaps.contains(it.file.name()) || it.file.parent().parent().name() == "marathon") &&
                                !it.file.name().contains("Taiko")
                        }
                    }
                } else {
                    allMaps.filter { mapFileName -> mapFileNames.any { it == mapFileName.file.name() } }
                }.reversed()

                Gdx.app.postRunnable {
                    if (loadToken != activeLoadToken) return@postRunnable

                    val buttonGroup = ButtonGroup<MapListItem>()
                    buildMapList(loadedItems, buttonGroup)

                    loadingTable.remove()

                    if (playSets == DefaultMapSets.DefaultPlaySets) {
                        createTable().apply {
                            if (loadedItems.isEmpty()) {
                                add("Here will be your maps\nfrom the 'other maps' folder")
                                center()
                            } else {
                                add(newLabel("Maps from the 'other maps' folder").also { it.setFontScale(0.6f) })
                                center().top()
                            }
                        }

                        createTable().apply {
                            add(newTextButton("Open folder").apply {
                                label.setFontScale(0.5f)
                            }.addChangeListener {
                                try {
                                    platformSpecific?.desktopOpenMapsFolder()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            })
                            bottom().right()
                        }
                    } else {
                        createTable().apply {
                            if (loadedItems.isEmpty() && playSets == UnlockedOnlyPlaySets) {
                                add("Unlock new maps by playing marathon!")
                                center()
                            } else {
                                add(newLabel("Unlock new maps by playing marathon!").also { it.setFontScale(0.6f) })
                                center().top()
                            }
                        }
                    }

                    root.findActor<VisTable>("listTable")?.remove()
                    if (loadedItems.isNotEmpty()) {
                        createTable().apply {
                            name = "listTable"
                            add(mapList.scrollPane).center().fillY().expandY().expandX().fillX()
                        }
                    }

                    createTable(newBackButton().addChangeListener {
                        game.menuScreen.changeStage(MenuStage())
                    }).apply {
                        left().bottom()
                    }

                    if (loadedItems.isNotEmpty()) {
                        val alreadyPlayingMapMusic =
                            mapList.adapter.iterable().firstOrNull { it.file.name() == AudioManager.lastMapMusic?.file?.name() }

                        val firstSelection = alreadyPlayingMapMusic ?: loadedItems.random()
                        mapList.adapter.iterable().forEach {
                            if (it.file.name() == firstSelection.file.name()) {
                                val item =  mapList.adapter.indexOf(it)
                                if (item >= 0) {
                                    buttonGroup.buttons[item].isChecked = true
                                }
                            }
                        }

                        selectedItem = buttonGroup.checked as? MapListItem ?: selectedItem
                        if (selectedItem != null) {
                            setBackgroundFor(selectedItem!!.map)
                        }

                        buttonGroup.setMinCheckCount(1)
                        scrollFocus = mapList.scrollPane
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Gdx.app.postRunnable {
                    loadingTable.remove()
                }
            } finally {
                Gdx.app.postRunnable {
                    if (loadToken == activeLoadToken) {
                        removeCover()
                        isLoading = false
                    }
                }
            }
        }.start()
    }

    override fun dispose() {
        super.dispose()
        bgTextureCache.forEach { it.value.dispose() }
        bgTextureCache.clear()
        loadedMaps.clear()
        bgLoading.clear()
    }

    class MapListItem(mapName: String, val map: GameMap) :
        Button(VisUI.getSkin().get("mapItem", ButtonStyle::class.java)) {
        init {
            add(VisImage().also { image ->
                GamePref.userSave.mapRanks.forEach {
                    if (it.key == map.file.name()) {
                        image.drawable = SpriteDrawable(assets.mainAtlas.createSprite(RankUtil.getRankChar(it.value.id)))
                    }
                }
            }).size(50f)
            add(VisLabel(if (mapName.length > 100) mapName.substring(0, 100) else mapName).also {
                it.setFontScale(0.4f)
            }).height(50f)
        }
    }
}
