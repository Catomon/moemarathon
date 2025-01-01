package com.github.catomon.moemarathon.mainmenu

import com.github.catomon.moemarathon.ui.StageScreen
import com.github.catomon.moemarathon.utils.logMsg

class MenuScreen(initialStage: (() -> BgStage)? = null) :
    StageScreen<BgStage>() {

    init {
        if (initialStage == null) {
            changeStage(MenuStage(this))
        } else {
            changeStage(initialStage.invoke())
        }
    }

    override fun changeStage(newStage: BgStage) {
        if (newStage is MenuStage) {
            if (stage == null) newStage.setRandomBg()
            else {
                if (stage?.background?.sprite != null) {
                    newStage.background.sprite = stage?.background?.sprite
                }
            }
        } else {
            if (stage != null) {
                if (stage?.background?.sprite != null) {
                    newStage.background.sprite = stage?.background?.sprite
                }
            }
        }

        logMsg("Current menu stage: ${newStage::class.simpleName}")
        super.changeStage(newStage)
    }
}
