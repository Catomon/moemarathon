package com.github.catomon.polly.map.osu

data class OsuFile(
    var version: String = "",
    var general: General? = null,
    var editor: Editor? = null,
    var metadata: OsuMetadata? = null,
    var difficulty: Difficulty? = null,
    var events: List<Event> = listOf(),
    var timingPoints: List<TimingPoint> = listOf(),
    var colours: Colours? = null,
    var hitObjects: List<HitObject> = listOf(),
)

data class General(
    val audioFilename: String,
    val audioLeadIn: Int,
    val audioHash: String,
    val previewTime: Int,
    val countdown: Int,
    val sampleSet: String,
    val stackLeniency: Double,
    val mode: Int,
    val letterboxInBreaks: Int,
    val storyFireInFront: Int,
    val useSkinSprites: Int,
    val alwaysShowPlayfield: Int,
    val overlayPosition: String,
    val skinPreference: String,
    val epilepsyWarning: Int,
    val countdownOffset: Int,
    val specialStyle: Int,
    val widescreenStoryboard: Int,
    val samplesMatchPlaybackRate: Int
)

data class Editor(
    val bookmarks: String,
    val distanceSpacing: Double,
    val beatDivisor: Int,
    val gridSize: Int,
    val timelineZoom: Double
)

data class OsuMetadata(
    val title: String,
    val titleUnicode: String,
    val artist: String,
    val artistUnicode: String,
    val creator: String,
    val version: String,
    val source: String,
    val tags: List<String>,
    val beatmapID: Int,
    val beatmapSetID: Int
)

data class Difficulty(
    val hpdDrainRate: Double,
    val circleSize: Double,
    val overallDifficulty: Double,
    val approachRate: Double,
    val sliderMultiplier: Double,
    val sliderTickRate: Double
)

data class Event(
    val eventType: String?,
    val startTime: Int,
    val eventParams: String
)

data class TimingPoint(
    val time: Int,
    val beatLength: Double,
    val meter: Int,
    val sampleSet: Int,
    val sampleIndex: Int,
    val volume: Int,
    val uninherited: Boolean,
    val effects: Int
)

data class Colours(
    val comboColour1_8_255_255_255: Int = 0, // Additive combo colours
    // ...
)

data class HitObject(
    val x: Int, // Position in osu! pixels of the object
    val y: Int, // Position in osu! pixels of the object
    val time: Int, // Time when the object is to be hit, in milliseconds from the beginning of the beatmap's audio
    val type : Int, // Bit flags indicating the type of the object
    val hitSound : Int = 0, // Bit flags indicating the hitsound applied to the object
    val objectParams : String = "", // Extra parameters specific to the object's type
    val hitSample : String = "" // Information about which samples are played when the object is hit
)
