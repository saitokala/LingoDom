package com.lingodom.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class GameRoundTest {

    @Test
    fun `fromRoundNumber returns correct round`() {
        assertEquals(GameRound.STARTER, GameRound.fromRoundNumber(1))
        assertEquals(GameRound.CLASSIC, GameRound.fromRoundNumber(2))
        assertEquals(GameRound.CHALLENGE, GameRound.fromRoundNumber(3))
        assertEquals(GameRound.MASTER, GameRound.fromRoundNumber(4))
    }

    @Test
    fun `fromRoundNumber returns STARTER for invalid number`() {
        assertEquals(GameRound.STARTER, GameRound.fromRoundNumber(0))
        assertEquals(GameRound.STARTER, GameRound.fromRoundNumber(99))
    }

    @Test
    fun `rounds have correct word lengths`() {
        assertEquals(4, GameRound.STARTER.wordLength)
        assertEquals(5, GameRound.CLASSIC.wordLength)
        assertEquals(6, GameRound.CHALLENGE.wordLength)
        assertEquals(7, GameRound.MASTER.wordLength)
    }

    @Test
    fun `rounds have correct max attempts`() {
        assertEquals(5, GameRound.STARTER.maxAttempts)
        assertEquals(5, GameRound.CLASSIC.maxAttempts)
        assertEquals(6, GameRound.CHALLENGE.maxAttempts)
        assertEquals(6, GameRound.MASTER.maxAttempts)
    }

    @Test
    fun `unlock scores are non-decreasing`() {
        val scores = GameRound.entries.map { it.unlockScore }
        assertEquals(scores, scores.sorted())
    }

    @Test
    fun `STARTER is free to play`() {
        assertEquals(0, GameRound.STARTER.unlockScore)
    }
}
