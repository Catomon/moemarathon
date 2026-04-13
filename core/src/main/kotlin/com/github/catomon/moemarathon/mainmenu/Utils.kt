package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisTable

private val skin = "lucky"

fun Stage.addGameNameLabel(): VisTable {
    return createTable().apply {
        add(newLabel(Config.APP_NAME))//.apply { color = Color(0.89f, 0.455f, 0.667f, 1f) }
        add(newLabel(Config.APP_VER).apply {
            setFontScale(0.35f); //color = Color(0.89f, 0.455f, 0.667f, 1f)
        }).bottom().padLeft(6f).padBottom(6f)
        center().top()
    }
}

fun newBigButton(text: String) = newTextButton(text, "big")

fun newButton(drawableName: String) = Button(VisUI.getSkin().getDrawable(drawableName))

fun newBackButton(text: String = "Back"): Button {
    return if (skin == "lucky") Button(VisUI.getSkin().getDrawable("menu-back")).also {
        it.add(Container(
            newLabel(text, "wborder", 0.75f)
        ).also { it.top().left().padTop(25f).padRight(8f).setFillParent(true) })
    } else newTextButton("<Back")
}

fun newEndButton(): Button {
    return newBackButton("End")
}

fun newContinueButton(text: String = "Continue"): Button {
    return if (skin == "lucky") Button(VisUI.getSkin().getDrawable("continue")).also {
        it.add(Container(
            newLabel(text, "wborder", 0.75f).also { it.name = "label" }
        ).also { it.top().padTop(25f).padRight(8f).setFillParent(true) })
    } else newTextButton("Continue>")
}
