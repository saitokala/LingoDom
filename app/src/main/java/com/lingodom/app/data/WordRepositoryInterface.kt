package com.lingodom.app.data

import com.lingodom.app.core.model.GameRound

interface WordRepositoryInterface {
    fun getRandomWord(round: GameRound): String
    fun isValidWord(word: String): Boolean
}
