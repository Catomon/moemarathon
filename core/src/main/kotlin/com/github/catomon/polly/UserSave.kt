package ctmn.petals

data class UserSave(
    var name: String = "Player",
    var level: Int = 1,
    var xp: Int = 0,
    var mapRanks: Map<String, Int> = mapOf("Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Easy].osu" to 7) //emptyMap(),
)
