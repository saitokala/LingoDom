package com.lingodom.app.fake

import com.lingodom.app.core.model.GameRound
import com.lingodom.app.data.WordRepository

/**
 * Fake word repository that returns a predetermined word
 * and treats it as the only valid word.
 */
class FakeWordRepository(
    private val fixedWord: String = "TEST"
) : WordRepository {

    private val validWords = mutableSetOf(fixedWord.uppercase())

    fun addValidWord(word: String) {
        validWords.add(word.uppercase())
    }

    override fun getRandomWord(round: GameRound): String = fixedWord

    override fun isValidWord(word: String): Boolean =
        validWords.contains(word.uppercase())
}
