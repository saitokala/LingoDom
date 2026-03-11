package com.lingodom.app.data

import android.content.Context
import com.lingodom.app.core.model.GameRound
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads and caches word lists from assets/.
 * Files are named words_4.txt … words_7.txt, one word per line.
 */
@Singleton
class WordRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : WordRepositoryInterface {

    private val cache = mutableMapOf<Int, List<String>>()

    override fun getRandomWord(round: GameRound): String {
        val words = loadWords(round.wordLength)
        require(words.isNotEmpty()) { "No words loaded for length ${round.wordLength}" }
        return words.random()
    }

    override fun isValidWord(word: String): Boolean {
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
