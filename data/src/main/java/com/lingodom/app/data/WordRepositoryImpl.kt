package com.lingodom.app.data

import android.content.Context
import com.lingodom.app.core.model.GameRound
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads and caches word lists from assets/.
 * Files are named words_4.txt … words_7.txt, one word per line.
 */
@Singleton
class WordRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WordRepository {

    private val cache = ConcurrentHashMap<Int, List<String>>()

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
        cache[length]?.let { return it }
        val words = try {
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
        // Only cache successful loads to allow retry on transient failures
        if (words.isNotEmpty()) {
            cache[length] = words
        }
        return words
    }
}
