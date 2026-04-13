package com.github.catomon.moemarathon.mainmenu

import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable

class CreditsStage() :
    BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        addGameNameLabel()

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
            addLinkLabel("Ne Ni Ge de Reset!", "https://osu.ppy.sh/beatmapsets/226242#osu/541744")
            row()
            addLinkLabel("Ne Ni Ge de Risetto!", "https://osu.ppy.sh/beatmapsets/12392#osu/46536")
            row()
            addLinkLabel("Shiawase Negai Kanata Kara", "https://osu.ppy.sh/beatmapsets/8398#osu/34548")
            row()
            addLinkLabel("Hamatte Sabotte Oh My Ga! (Short Ver.)", "https://osu.ppy.sh/beatmapsets/2164#osu/18955")
            row()
            addLinkLabel("Mo, Mousou Machine", "https://osu.ppy.sh/beatmapsets/24474#osu/83202")
            row()
            addLinkLabel(
                "Dondake Fanfare",
                "https://osu.ppy.sh/beatmapsets/19618#osu/68873"
            )
            row()
            addLinkLabel("Moe Yousotte nan desu ka?", "https://osu.ppy.sh/beatmapsets/9533#osu/37995")
            row()
            addLinkLabel("Motteke! Sailor Fuku (TV Size)", "https://osu.ppy.sh/beatmapsets/924885#osu/1932010")
            row()
            addLinkLabel("Nande Dattakke Idol? (Short ver.)", "https://osu.ppy.sh/beatmapsets/12610#osu/47304")
            row()
            addLinkLabel("Lucky Star no Minna - Kumikyoku 'Lucky Star Douga'", "https://osu.ppy.sh/beatmapsets/91214")
            row()
            add("Graphics:").padTop(50f)
            row()
            addLinkLabel("Osu SaberStrike skin", "https://sbrstrkkdwmdr.github.io/skins/main-skins.html")
            row()
            add("Me:").padTop(50f)
            row()
            addLinkLabel("github.com/Catomon", "https://github.com/Catomon/")
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
