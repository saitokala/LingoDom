package com.lingodom.app.core.model

/**
 * Defines the four game rounds with escalating difficulty.
 *
 * @property displayName  Human-readable round name
 * @property wordLength   Number of letters in the target word
 * @property maxAttempts   Maximum guesses allowed
 * @property roundNumber   1-based round index
 * @property unlockScore   Cumulative score required to unlock this round
 */
enum class GameRound(
    val displayName: String,
    val wordLength: Int,
    val maxAttempts: Int,
    val roundNumber: Int,
    val unlockScore: Int
) {
    STARTER("Starter", 4, 5, 1, 0),
    CLASSIC("Classic", 5, 5, 2, 500),
    CHALLENGE("Challenge", 6, 6, 3, 2000),
    MASTER("Master", 7, 6, 4, 5000);

    companion object {
        fun fromRoundNumber(number: Int): GameRound =
            entries.find { it.roundNumber == number } ?: STARTER
    }
}
