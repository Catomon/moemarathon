package com.github.catomon.polly.mainmenu

import com.github.catomon.polly.difficulties.EasyDiff
import com.github.catomon.polly.difficulties.HardDiff
import com.github.catomon.polly.difficulties.NormalDiff
import com.github.catomon.polly.difficulties.PlaySettings
import com.github.catomon.polly.game
import com.github.catomon.polly.utils.createTable
import com.github.catomon.polly.widgets.addChangeListener
import com.github.catomon.polly.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisTextButton

class DifficultySelectStage() :
    BgStage() {

    private val difficulties: List<PlaySettings> = listOf(
        EasyDiff(), NormalDiff(), HardDiff()
    )

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        createTable().apply {
            difficulties.forEach { diff ->
                add(newTextButton(diff.name).also { button ->
                    button.userObject = button
                    button.addChangeListener {
                        chooseDiff(diff)
                    }
                })
                row()
            }
        }

        createTable(VisTextButton("<Menu").addChangeListener {
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }

    private fun chooseDiff(diff: PlaySettings) {
        menuScreen.changeStage(MapSelectStage(diff))
    }
}
