package com.lingodom.app.core.model

/**
 * Player ranks awarded at cumulative score thresholds.
 */
enum class Rank(
    val title: String,
    val requiredScore: Int,
    val emoji: String
) {
    NOVICE("Novice", 0, "🔤"),
    WORDSMITH("Wordsmith", 500, "✏\uFE0F"),
    LINGUIST("Linguist", 2000, "📖"),
    LEXICON_MASTER("Lexicon Master", 5000, "🏆"),
    LINGODOM_CHAMPION("LingoDom Champion", 10000, "👑");

    companion object {
        fun fromScore(score: Int): Rank =
            entries.last { score >= it.requiredScore }
    }
}
