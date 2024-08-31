package com.github.catomon.polly.map.osu

import com.badlogic.gdx.utils.ArrayMap

object OsuParser {

    fun parseHitObjects(text: String) = parseHitObjects(text.lines())

    fun parseHitObjects(lines: List<String>): List<HitObject> {
        val hitObjects = mutableListOf<HitObject>()
        lines.forEach { line ->
            val params = line.split(",")
            when {
                params.size > 7 -> {
                    val hitObject = HitObject(
                        x = params[0].toInt(),
                        y = params[1].toInt(),
                        time = params[2].toInt(),
                        type = params.getOrNull(3)?.toInt() ?: 0,
                        objectParams = params //params.getOrNull(5) ?: "",
                    )
                    hitObjects.add(hitObject)
                }

                params.size > 1 -> {
                    val hitObject = HitObject(
                        x = params[0].toInt(),
                        y = params[1].toInt(),
                        time = params[2].toInt(),
                        type = params.getOrNull(3)?.toInt() ?: 0,
                    )
                    hitObjects.add(hitObject)
                }
            }
        }
        return hitObjects
    }

    fun parse(text: String): OsuBeatmap {
        val lines = text.lines()

        val versionLine = lines.first().let {
            if (it.contains("["))
                ""
            else
                it
        }

        val osuBeatmap = OsuBeatmap(versionLine.split("\\s+".toRegex()).lastOrNull() ?: "unknown")

        var section = ""
        for (line in lines) {
            if (line.startsWith("[")) section = line.split("[", "]")[1]

            if (section != "") {
                val options = text
                    .split(line)[1]
                    .lines()
                    .drop(1)
                    .let {
                        val nextSecInd = it.indexOfFirst { it.contains("[") }
                        if (nextSecInd > -1)
                            it.subList(0, nextSecInd)
                        else it
                    }
                    .mapNotNull { if (it.contains("//") || it.isEmpty()) null else it }

                if (!osuBeatmap.sections.containsKey(section))
                    osuBeatmap.sections.put(section, ArrayMap())

                options.forEachIndexed { i, opt ->
                    val nameVal = opt.split(": ", ":")
                    if (nameVal.size == 2 && section != "HitObjects" && section != "TimingPoints")
                        osuBeatmap.sections[section].put(nameVal[0], nameVal.drop(1).first())
                    else
                        osuBeatmap.sections[section].put(i.toString(), opt)
                }

                section = ""
            }
        }

        return osuBeatmap
    }
}
