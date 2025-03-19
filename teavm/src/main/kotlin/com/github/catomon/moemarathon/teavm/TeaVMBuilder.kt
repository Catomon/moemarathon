package com.github.catomon.moemarathon.teavm

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.github.catomon.moemarathon.UserSave
import com.github.catomon.moemarathon.difficulties.Rank
import com.github.catomon.moemarathon.leaderboard.DreamloContainer
import com.github.catomon.moemarathon.leaderboard.DreamloLeaderboard
import com.github.catomon.moemarathon.leaderboard.Entry
import com.github.catomon.moemarathon.leaderboard.Leaderboard
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter
import com.kotcrab.vis.ui.util.form.SimpleFormValidator
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.color.ColorPickerStyle
import com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle
import com.kotcrab.vis.ui.widget.file.FileChooserStyle
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.toast.Toast.ToastStyle
import org.teavm.vm.TeaVMOptimizationLevel
import java.io.File

/** Builds the TeaVM/HTML application. */
@SkipClass
object TeaVMBuilder {
    @JvmStatic
    fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(AssetFileHandle("../assets"))
            webappPath = File("build/dist").canonicalPath
            // Register any extra classpath assets here:
            // additionalAssetsClasspathFiles += "com/github/catomon/moemarathon/asset.extension"
        }

        // Register any classes or packages that require reflection here:
//ui
        TeaReflectionSupplier.addReflectionClass(VisTextButton.VisTextButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(BitmapFont::class.java)
        TeaReflectionSupplier.addReflectionClass(Color::class.java)
        TeaReflectionSupplier.addReflectionClass(Skin.TintedDrawable::class.java)
        TeaReflectionSupplier.addReflectionClass(Button.ButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(CheckBox.CheckBoxStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ImageButton.ImageButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ImageTextButton.ImageTextButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Label.LabelStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(List.ListStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ProgressBar.ProgressBarStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ScrollPane.ScrollPaneStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(SelectBox.SelectBoxStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Slider.SliderStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(SplitPane.SplitPaneStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(TextButton.TextButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(TextField.TextFieldStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(TextTooltip.TextTooltipStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Touchpad.TouchpadStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Tree.TreeStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Window.WindowStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(com.kotcrab.vis.ui.Sizes::class.java)
        TeaReflectionSupplier.addReflectionClass(VisTextField.VisTextFieldStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(VisTextButton.VisTextButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(VisImageButton.VisImageButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(VisImageTextButton.VisImageTextButtonStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(VisCheckBox.VisCheckBoxStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(PopupMenu.PopupMenuStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Menu.MenuStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(MenuBar.MenuBarStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Separator.SeparatorStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(VisSplitPane.VisSplitPaneStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(MultiSplitPane.MultiSplitPaneStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(MenuItem.MenuItemStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Tooltip.TooltipStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(LinkLabel.LinkLabelStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(TabbedPane.TabbedPaneStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(Spinner.SpinnerStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(FileChooserStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ColorPickerWidgetStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ColorPickerStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(BusyBar.BusyBarStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ListViewStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(SimpleFormValidator.FormValidatorStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(ToastStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(SimpleListAdapter.SimpleListAdapterStyle::class.java)
        TeaReflectionSupplier.addReflectionClass(UserSave::class.java)
        TeaReflectionSupplier.addReflectionClass(Rank::class.java)
        TeaReflectionSupplier.addReflectionClass(Entry::class.java)
        TeaReflectionSupplier.addReflectionClass(DreamloLeaderboard::class.java)
        TeaReflectionSupplier.addReflectionClass(DreamloContainer::class.java)
        TeaReflectionSupplier.addReflectionClass(Leaderboard::class.java)

        val tool = TeaBuilder.config(teaBuildConfiguration)
        tool.mainClass = "com.github.catomon.moemarathon.teavm.TeaVMLauncher"
        tool.optimizationLevel = TeaVMOptimizationLevel.FULL
        tool.setObfuscated(false)
        TeaBuilder.build(tool)
    }
}
