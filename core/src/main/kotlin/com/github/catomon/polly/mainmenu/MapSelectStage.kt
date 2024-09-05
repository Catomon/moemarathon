package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.catomon.polly.map.GameMap
import com.github.catomon.polly.map.MapsManager
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.addCover
import com.github.catomon.polly.utils.createTable
import com.github.catomon.polly.utils.logMsg
import com.github.catomon.polly.utils.removeCover
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisLabel
import ctmn.petals.widgets.addChangeListener
import kotlin.concurrent.thread

class MapSelectStage(val menuScreen: MenuScreen) : Stage(ExtendViewport(2000f, 1200f)) {

    private var isLoading = false
    private var loadedItems = emptyList<GameMap>()

    private var mapList = ListView(object : ArrayListAdapter<GameMap, VisLabel>(ArrayList(loadedItems)) {
        override fun createView(item: GameMap): VisLabel {
            return VisLabel(item.file.nameWithoutExtension())
        }
    })

    init {
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
            try {
                addCover()
                isLoading = true
                loadedItems = MapsManager.collectMapFiles().map { GameMap(it) }

                mapList = ListView(object : ArrayListAdapter<GameMap, VisLabel>(ArrayList(loadedItems)) {
                    init {
                        setItemClickListener {
                            menuScreen.game.screen = PlayScreen(it)
                        }

                        //selectionMode = SelectionMode.SINGLE
                    }

                    override fun createView(item: GameMap): VisLabel {
                        return VisLabel(item.file.nameWithoutExtension())
                    }
                })

                clear()

                createTable().apply {
                    add(mapList.scrollPane).center().fillY().expandY()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                removeCover()
                isLoading = false
            }
        }
    }
}
