package com.lingodom.app.data

import android.content.Context
import com.lingodom.app.core.model.GameRound

/**
 * Loads and caches word lists from assets/.
 * Files are named words_4.txt … words_7.txt, one word per line.
 */
class WordRepository(private val context: Context) {

    private val cache = mutableMapOf<Int, List<String>>()

    /** Pick a random target word for the given [round]. */
    fun getRandomWord(round: GameRound): String {
        val words = loadWords(round.wordLength)
        require(words.isNotEmpty()) { "No words loaded for length ${round.wordLength}" }
        return words.random()
    }

    /** Check whether [word] is a valid dictionary entry of the correct length. */
    fun isValidWord(word: String): Boolean {
        val words = loadWords(word.length)
        return words.contains(word.uppercase())
    }

    private fun loadWords(length: Int): List<String> {
        return cache.getOrPut(length) {
            try {
                context.assets.open("words_$length.txt")
                    .bufferedReader()
                    .readLines()
                    .asSequence()
                    .map { it.trim().uppercase() }
                    .filter { it.length == length && it.all(Char::isLetter) }
                    .distinct()
                    .toList()
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
