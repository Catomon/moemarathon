package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.github.catomon.moemarathon.GamePref
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisTextButton

private val skin = GamePref.userSave.skin

fun newButton(drawableName: String) = Button(VisUI.getSkin().getDrawable(drawableName))

fun newBackButton() : Button {
    return if (skin == "lucky") Button(VisUI.getSkin().getDrawable("menu-back")) else VisTextButton("<Back")
}

fun newEndButton() : Button {
    return if (skin == "lucky") Button(VisUI.getSkin().getDrawable("end")) else VisTextButton("<End")
}

fun newContinueButton() : Button {
    return if (skin == "lucky") Button(VisUI.getSkin().getDrawable("continue")) else VisTextButton("<Continue")
}
