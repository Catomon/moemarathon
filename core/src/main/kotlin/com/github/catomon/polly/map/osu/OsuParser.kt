package com.github.catomon.polly.map.osu

object OsuParser {
    fun parseHitObjects(text: String): List<HitObject> {
        val hitObjects = mutableListOf<HitObject>()
        text.split("\n") // split by line breaks
            //.drop(1) // skip the first line "[HitObjects]"
            .forEach { line ->
                val parts = line.split(",")
                when {
//                    parts.size > 10 -> {
//                        //todo
//                        val hitObject = HitObject(
//                            x = parts[0].toInt(),
//                            y = parts[1].toInt(),
//                            time = parts[2].toInt(),
//                            type = parts.getOrNull(3)?.toInt() ?: 0,
////                        hitSound = parts.getOrNull(4)?.toInt() ?: 0,
////                        objectParams = parts.getOrNull(5) ?: "",
////                        hitSample = parts.getOrNull(6) ?: "",
//                        )
//                        hitObjects.add(hitObject)
//                    }

                    parts.size > 1 -> {
                        val hitObject = HitObject(
                            x = parts[0].toInt(),
                            y = parts[1].toInt(),
                            time = parts[2].toInt(),
                            type = parts.getOrNull(3)?.toInt() ?: 0,
//                        hitSound = parts.getOrNull(4)?.toInt() ?: 0,
//                        objectParams = parts.getOrNull(5) ?: "",
//                        hitSample = parts.getOrNull(6) ?: "",
                        )
                        hitObjects.add(hitObject)
                    }
                }
            }
        return hitObjects
    }

    fun parse(text: String): OsuFile {
        val osuFile = OsuFile()

        val lines = text.split("\n")

        val versionLine = lines.first()
        osuFile.version = versionLine.split("\\s+".toRegex())[1]

        // Parse the sections
        var section = ""
        for (line in lines) {
            if (line.startsWith("[")) section = line

            if (section != "") {
                val options2 = text.split(section + "\n")[1].split("\n[")[0].split("\n")

                val options = options2.mapNotNull { it.split(": ").getOrNull(1) }

                println(options.joinToString(","))

                when (section.drop(1).dropLast(2)) {
                    "General" -> {
                        osuFile.general = General(
                            audioFilename = options[0],
                            audioLeadIn = options[1].toInt(),
                            audioHash = options[2],
                            previewTime = options[3].toInt(),
                            countdown = options[4].toInt(),
                            sampleSet = options[5],
                            stackLeniency = options[6].toDouble(),
                            mode = options[7].toInt(),
                            letterboxInBreaks = options[8].toInt(),
                            storyFireInFront = options[9].toInt(),
                            useSkinSprites = options[10].toInt(),
                            alwaysShowPlayfield = options[11].toInt(),
                            overlayPosition = options[12],
                            skinPreference = options[13],
                            epilepsyWarning = options[14].toInt(),
                            countdownOffset = options[15].toInt(),
                            specialStyle = options[16].toInt(),
                            widescreenStoryboard = options[17].toInt(),
                            samplesMatchPlaybackRate = options[18].toInt()
                        )
                    }

                    "Editor" -> {
                        osuFile.editor = Editor(
                            bookmarks = options[0],
                            distanceSpacing = options[1].toDouble(),
                            beatDivisor = options[2].toInt(),
                            gridSize = options[3].toInt(),
                            timelineZoom = options[4].toDouble()
                        )
                    }

                    "Metadata" -> {
                        osuFile.metadata = OsuMetadata(
                            title = options[0],
                            titleUnicode = options[1],
                            artist = options[2],
                            artistUnicode = options[3],
                            creator = options[4],
                            version = options[5],
                            source = options[6],
                            tags = ArrayList(options.subList(7, options.size)),
                            //
                            beatmapID = 0,
                            beatmapSetID = 0,
                        )
                    }

                    "Difficulty" -> {
                        osuFile.difficulty = Difficulty(
                            hpdDrainRate = options[0].toDouble(),
                            circleSize = options[1].toDouble(),
                            overallDifficulty = options[2].toDouble(),
                            approachRate = options[3].toDouble(),
                            sliderMultiplier = options[4].toDouble(),
                            sliderTickRate = options[5].toDouble()
                        )
                    }

                    "TimingPoints" -> {
                        // Parse the timing point
                        val timingPointOptions = line.split("\\s+".toRegex())
//                        osuFile.timingPoints.add(
//                            TimingPoint(
//                                time = timingPointOptions[0].toInt(),
//                                beatLength = timingPointOptions[1].toDouble(),
//                                meter = timingPointOptions[2].toInt(),
//                                sampleSet = timingPointOptions[3].toInt(),
//                                sampleIndex = timingPointOptions[4].toInt(),
//                                volume = timingPointOptions[5].toInt(),
//                                uninherited = TODO(),
//                                effects = TODO(),
//                            )
//                        )
                    }

                    "HitObjects" -> {
                        osuFile.hitObjects = parseHitObjects(options.joinToString("\n"))
                    }

                    else -> throw IllegalArgumentException("Unknown section ${section.drop(1).dropLast(2)}")
                }
            }
        }

        return osuFile
    }
}
