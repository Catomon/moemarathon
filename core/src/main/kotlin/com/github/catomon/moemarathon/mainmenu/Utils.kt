package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.VisUI

private val skin = "lucky"

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
            newLabel(text, "wborder", 0.75f)
        ).also { it.top().padTop(25f).padRight(8f).setFillParent(true) })
    } else newTextButton("Continue>")
}
