package com.lingodom.app.core.model

/**
 * Persisted player statistics across all game sessions.
 */
data class PlayerStats(
    val totalScore: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalWordsPlayed: Int = 0,
    val totalWordsWon: Int = 0,
    val wordsWonPerRound: Map<Int, Int> = emptyMap(),   // roundNumber -> count
    val guessDistribution: List<Int> = List(6) { 0 }    // index 0 = solved in 1 guess, etc.
)
