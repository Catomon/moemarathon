package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.widget.*

class CreditsStage() :
    BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            center().top()
        }

        fun VisTable.addLinkLabel(text: String, link: String = "") {
            row()
            if (link.isNotEmpty()) {
                add(LinkLabel(text, link)).width(900f).actor.also { label ->
                    label.setFontScale(0.50f)
                    label.wrap = true
                }
            } else {
                add(VisLabel(text)).width(900f).actor.also { label ->
                    label.setFontScale(0.50f)
                    label.wrap = true
                }
            }
        }

        val contentTable = VisTable().apply {
            width = 900f
            setFillParent(true)
            defaults().left()
            add("Beatmaps:")
            row()
            addLinkLabel("Ichijo - Roulette Roulette", "https://osu.ppy.sh/beatmapsets/10886")
            row()
            addLinkLabel("Yousei Teikoku - Torikago", "https://osu.ppy.sh/beatmapsets/11781")
            row()
            addLinkLabel("U - the first the last", "https://osu.ppy.sh/beatmapsets/17068")
            row()
            addLinkLabel("Yousei Teikoku - Senketsu no Chikai", "https://osu.ppy.sh/beatmapsets/27104")
            row()
            addLinkLabel("iyuna - Emukko Kyun Kyun", "https://osu.ppy.sh/beatmapsets/39484")
            row()
            addLinkLabel(
                "IOSYS - Princess Party ~Seishun Kinshi Rei~",
                "https://osu.ppy.sh/beatmapsets/48189#osu/149132"
            )
            row()
            addLinkLabel("Katakiri Rekka - (^3^)chu Dere Rhapsody", "https://osu.ppy.sh/beatmapsets/51530")
            row()
            addLinkLabel("KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~", "https://osu.ppy.sh/beatmapsets/51802")
            row()
            addLinkLabel("nao - Kirihirake! Gracie_Star", "https://osu.ppy.sh/beatmapsets/72743")
            row()
            addLinkLabel("solfa feat Chata - Colorful precious life", "https://osu.ppy.sh/beatmapsets/112655")
            row()
            addLinkLabel("Lucky Star no Minna - Kumikyoku 'Lucky Star Douga'", "https://osu.ppy.sh/beatmapsets/91214")
            row()
            addLinkLabel("JunA - Bucuresti no Ningyoushi", "https://osu.ppy.sh/beatmapsets/1501476")
            row()
            add("Graphics:").padTop(50f)
            row()
            addLinkLabel("Lucky skin is based on Osu Lucky Star skin by Utena", "https://osu.ppy.sh/community/forums/topics/5498")
            row()
            addLinkLabel("Osu SaberStrike skin", "https://sbrstrkkdwmdr.github.io/skins/main-skins.html")
            row()
            addLinkLabel("logo, komugi skin, bekky skin by carroteater9000", "")
            row()
            add("Other stuff:").padTop(50f)
            row()
            addLinkLabel("monscout.itch.io", "https://monscout.itch.io/")
        }

        addActor(VisScrollPane(contentTable).apply {
            setFillParent(true)
            scrollFocus = this
        })

        createTable(newBackButton().addChangeListener {
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }
}
