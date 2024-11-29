package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.scene2d.actions.UpdateAction
import com.github.catomon.moemarathon.utils.RegionAnimation
import com.github.catomon.moemarathon.utils.addClickListener
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import kotlin.concurrent.thread

class SkinsStage() :
    BgStage() {

    private val skins: List<Skin> = Skins.skins

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    private val userSave = GamePref.userSave

    init {
        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            center().top()
        }


        createTable().apply {
            name = "skins"
            skins.forEachIndexed { i, skin ->
                add(Button(null, null, VisUI.getSkin().getDrawable("button-down-50")).apply {
                    val unlocked = skin.name == Skins.default.name || userSave.unlocks.contains("skin:" + skin.name)
                    val frames = RegionAnimation(0.4f, assets.mainAtlas.findRegions(skin.center).let { if (it.isEmpty) assets.mainAtlas.findRegions("question") else it })
                    val sprite = Sprite(frames.currentFrame)
                    if (!unlocked) {
                        color.a = 0.5f
                        sprite.setAlpha(0.5f)
                    }
                    addAction(UpdateAction {
                        frames.update(it)
                        sprite.setRegion(frames.currentFrame)
                        false
                    })
                    add(newLabel(skin.name, 0.5f))
                    row()
                    add(
                        VisImage(SpriteDrawable(sprite))
                    ).size(100f, 100f)
                    isChecked = userSave.skin == skin.name
                    addClickListener {
                        if (!unlocked) {
                            isChecked = false
                            return@addClickListener
                        }

                        if (isChecked) {
                            stage.root.findActor<VisTable>("skins").children.forEach {
                                if (it is Button)
                                    it.isChecked = false
                            }
                            userSave.skin = skin.name
                        }

                        isChecked = true
                    }
                })
                if ((i + 1) % 3 == 0)
                    row()
            }
        }

        createTable(VisTextButton("<Menu").addChangeListener {
            GamePref.userSave = userSave
            GamePref.save()
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }
}
