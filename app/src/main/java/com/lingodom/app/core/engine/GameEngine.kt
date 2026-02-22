package com.lingodom.app.core.engine

import com.lingodom.app.core.model.LetterResult
import com.lingodom.app.core.model.LetterState

/**
 * Stateless Lingo game engine — evaluates guesses and calculates scores.
 *
 * Letter evaluation handles duplicate letters correctly:
 *  1. First pass marks exact-position matches (CORRECT / green).
 *  2. Second pass marks remaining letters present elsewhere (WRONG_POSITION / yellow),
 *     consuming each target letter at most once.
 */
object GameEngine {

    /**
     * Evaluate a [guess] against the [target] word.
     * Both strings are compared case-insensitively.
     *
     * @return A list of [LetterResult] — one per character in [guess].
     */
    fun evaluateGuess(guess: String, target: String): List<LetterResult> {
        val g = guess.uppercase().toCharArray()
        val t = target.uppercase().toCharArray()
        val results = Array(g.size) { LetterResult(g[it], LetterState.ABSENT) }
        val used = BooleanArray(t.size)

        // Pass 1 — exact matches
        for (i in g.indices) {
            if (i < t.size && g[i] == t[i]) {
                results[i] = LetterResult(g[i], LetterState.CORRECT)
                used[i] = true
            }
        }

        // Pass 2 — wrong-position matches
        for (i in g.indices) {
            if (results[i].state == LetterState.CORRECT) continue
            for (j in t.indices) {
                if (!used[j] && g[i] == t[j]) {
                    results[i] = LetterResult(g[i], LetterState.WRONG_POSITION)
                    used[j] = true
                    break
                }
            }
        }

        return results.toList()
    }

    /** Points awarded for solving on the given [guessNumber] (1-based). */
    fun scoreForGuess(guessNumber: Int): Int = when (guessNumber) {
        1 -> 100
        2 -> 80
        3 -> 60
        4 -> 40
        5 -> 20
        6 -> 10
        else -> 5
    }

    /** Bonus points for completing a word in [roundNumber]. */
    fun roundBonus(roundNumber: Int): Int = 50 * roundNumber

    /** Bonus for maintaining a [streak] of consecutive correct words. */
    fun streakBonus(streak: Int): Int = if (streak > 1) 10 * streak else 0
}
