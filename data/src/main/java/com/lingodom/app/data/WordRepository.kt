package com.lingodom.app.data

import com.lingodom.app.core.model.GameRound

/** Contract for loading and querying the word dictionary. */
interface WordRepository {
    /** Pick a random target word for the given [round]. */
    fun getRandomWord(round: GameRound): String

    /** Check whether [word] is a valid dictionary entry of the correct length. */
    fun isValidWord(word: String): Boolean
}
