package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.moemarathon.AudioManager
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.assets
import com.github.catomon.moemarathon.difficulties.PlaySets
import com.github.catomon.moemarathon.difficulties.PlaySets.DefaultPlaySets
import com.github.catomon.moemarathon.difficulties.PlaySets.HardMarathon
import com.github.catomon.moemarathon.difficulties.PlaySets.InsaneMarathon
import com.github.catomon.moemarathon.difficulties.PlaySets.NormalMarathon
import com.github.catomon.moemarathon.difficulties.PlaySets.UnlockedOnlyPlaySets
import com.github.catomon.moemarathon.difficulties.PlaySettings
import com.github.catomon.moemarathon.difficulties.RankUtil
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.*
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable

class MapSelectStage(
    val playSets: PlaySettings = DefaultPlaySets,
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

    private val loadingTable = createTable(newLabel("Collecting maps..."))

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

        Thread {
            val buttonGroup = ButtonGroup<MapListItem>()
            try {
                addCover()
                isLoading = true
                val marathonMaps =
                    PlaySets.NormalMarathon.maps + PlaySets.HardMarathon.maps + PlaySets.InsaneMarathon.maps + PlaySets.NonStop.maps
                loadedItems =
                    (if (mapFileNames.isEmpty()) {
                        if (playSets == UnlockedOnlyPlaySets) {
                            val userSaveMapRanks = GamePref.userSave.mapRanks
                            MapsManager.collectMapFiles().map { GameMap(it) }
                                .filter { userSaveMapRanks.contains(it.file.name()) && (marathonMaps.contains(it.file.name()) || it.file.parent().parent()
                                    .name() == "marathon") }
                        } else {
                            MapsManager.collectMapFiles().map { GameMap(it) }
                                .filter {
                                    !(marathonMaps.contains(it.file.name()) || it.file.parent().parent()
                                        .name() == "marathon") && !it.file.name().contains("Taiko")
                                }
                            // .filter { !marathonMaps.contains(it.file.name()) && !it.file.name().contains("Taiko") }
                            //!(marathonMaps.contains(it.file.name()) || it.file.parent().parent().name() == "marathon")
                        }
                    } else
                        MapsManager.collectMapFiles().map { GameMap(it) }
                            .filter { mapFileNames.any { mapFileName -> mapFileName == it.file.name() } }
                        ).reversed()

                mapList = ListView(object : ArrayListAdapter<GameMap, MapListItem>(ArrayList(loadedItems)) {
                    init {
                        buttonGroup.setMaxCheckCount(1)
                        buttonGroup.setMinCheckCount(0)
                        selectionMode = SelectionMode.DISABLED
                    }

                    var selectedItem: MapListItem? = null

                    override fun createView(item: GameMap): MapListItem {
                        return MapListItem(item.file.nameWithoutExtension(), item).also { newMapListItem ->
                            buttonGroup.add(newMapListItem)

                            newMapListItem.addChangeListener {
                                if (it.isChecked) {
                                    background.sprite =
                                        Sprite(
                                            textureBgCache[it.map.file.name()] ?: throw IllegalStateException("no bg")
                                        )

                                    if (selectedItem == null)
                                        selectedItem = it

                                    if (AudioManager.lastMapMusic?.osuBeatmap?.audioFileName == it.map.osuBeatmap.audioFileName) {
                                        AudioManager.playMapMusic()
                                    } else {
                                        AudioManager.loadMapMusic(it.map)
                                        AudioManager.playMapMusic()
                                    }
                                }
                            }
                            newMapListItem.addClickListener {
                                if (buttonGroup.checked == selectedItem) {
                                    this@MapSelectStage.fadeInAndThen(1f) {
                                        var playSets = playSets
                                        if (playSets == UnlockedOnlyPlaySets) {
                                            when {
                                                NormalMarathon.maps.contains(newMapListItem.map.file.name()) -> {
                                                    playSets =
                                                        playSets.copy(noteSpawnTime = NormalMarathon.noteSpawnTime)
                                                }

                                                HardMarathon.maps.contains(newMapListItem.map.file.name()) -> {
                                                    playSets = playSets.copy(noteSpawnTime = HardMarathon.noteSpawnTime)
                                                }

                                                InsaneMarathon.maps.contains(newMapListItem.map.file.name()) -> {
                                                    playSets =
                                                        playSets.copy(noteSpawnTime = InsaneMarathon.noteSpawnTime)
                                                }
                                            }
                                        }
                                        game.screen = PlayScreen(newMapListItem.map, playSets)
                                    }
                                }
                            }
                            newMapListItem.addClickListener {
                                if (newMapListItem.isChecked) {
                                    selectedItem = newMapListItem
                                }
                            }
                        }
                    }
                }).also { listView ->
                    listView.header = Actor().also { it.setSize(50f, 50f) }
                }

                loadingTable.remove()

                if (playSets == PlaySets.DefaultPlaySets) {
                    createTable().apply {
                        if (loadedItems.isEmpty() && playSets == UnlockedOnlyPlaySets) {
                            add("Here will be your maps\nfrom the 'other maps' folder")
                            center()
                        } else {
                            add(newLabel("Maps from the 'other maps' folder").also { it.setFontScale(0.6f) })
                            center().top()
                        }
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
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                removeCover()
                isLoading = false
            }

            if (loadedItems.isNotEmpty())
                Gdx.app.postRunnable {
                    loadedItems.forEach {
                        if (!textureBgCache.containsKey(it.file.name()))
                            textureBgCache.put(it.file.name(), it.newBackgroundTexture())
                    }

                    val alreadyPlayingMapMusic =
                        buttonGroup.buttons.firstOrNull { it.map.file.name() == AudioManager.lastMapMusic?.file?.name() }
                    if (alreadyPlayingMapMusic != null) {
                        alreadyPlayingMapMusic.isChecked = true
                    } else {
                        buttonGroup.buttons.random().isChecked = true
                    }

                    buttonGroup.setMinCheckCount(1)
                    scrollFocus = mapList.scrollPane
                }
        }.start()
    }

    override fun dispose() {
        super.dispose()

        textureBgCache.forEach { it.value.dispose() }
        textureBgCache.clear()
    }

    class MapListItem(mapName: String, val map: GameMap) :
        Button(VisUI.getSkin().get("mapItem", ButtonStyle::class.java)) {
        init {
            add(VisImage().also { image ->
                GamePref.userSave.mapRanks.forEach {
                    if (it.key == map.file.name()) {
                        image.drawable =
                            SpriteDrawable(assets.mainAtlas.createSprite(RankUtil.getRankChar(it.value.id)))
                    }
                }
            }).size(50f)
            add(VisLabel(if (mapName.length > 100) mapName.substring(0, 100) else mapName).also {
                it.setFontScale(0.4f)
            }).height(50f)
        }
    }
}
