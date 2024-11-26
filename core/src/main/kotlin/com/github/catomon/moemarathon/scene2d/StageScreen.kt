package com.github.catomon.moemarathon.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions

open class  StageScreen<T : Stage>(
    stage: T? = null,
) : ScreenAdapter() {

    var stage: T? = stage
        private set(value) {
            field = value
            Gdx.input.inputProcessor = value
        }

    open fun changeStage(newStage: T) {
//        val stage = stage
        this.stage = newStage

        return
//        if (stage != null) {
//            stage.addAction(
//                Actions.sequence(
//                    Actions.alpha(0f, 0.25f),
//                    Actions.run {
//                        newStage.addAction(Actions.alpha(1f, 0.25f))
//                        this.stage = newStage
//                    }
//                )
//            )
//        } else {
//            newStage.addAction(Actions.alpha(1f, 0.25f))
//            this.stage = newStage
//        }
    }

    override fun render(delta: Float) {
        super.render(delta)

        stage?.act()
        stage?.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)

        stage?.viewport?.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()

        stage?.dispose()
    }
}
