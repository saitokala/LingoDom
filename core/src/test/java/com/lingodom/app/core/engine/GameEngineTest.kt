package com.lingodom.app.core.engine

import com.lingodom.app.core.model.LetterState
import org.junit.Assert.assertEquals
import org.junit.Test

class GameEngineTest {

    // ── evaluateGuess ────────────────────────────────────────────────

    @Test
    fun `exact match returns all CORRECT`() {
        val result = GameEngine.evaluateGuess("APPLE", "APPLE")
        assertEquals(5, result.size)
        result.forEach { assertEquals(LetterState.CORRECT, it.state) }
    }

    @Test
    fun `no matching letters returns all ABSENT`() {
        val result = GameEngine.evaluateGuess("XXXYZ", "APPLE")
        result.forEach { assertEquals(LetterState.ABSENT, it.state) }
    }

    @Test
    fun `wrong position letters are marked correctly`() {
        val result = GameEngine.evaluateGuess("ELPPA", "APPLE")
        // E at 0: not at 0 in APPLE (A is at 0), but E is at 4 → WRONG_POSITION
        assertEquals(LetterState.WRONG_POSITION, result[0].state) // E
        assertEquals(LetterState.WRONG_POSITION, result[1].state) // L
        assertEquals(LetterState.CORRECT, result[2].state)        // P
        assertEquals(LetterState.WRONG_POSITION, result[3].state) // P
        assertEquals(LetterState.WRONG_POSITION, result[4].state) // A
    }

    @Test
    fun `duplicate letters handled correctly - only one yellow for one remaining`() {
        // Target: APPLE -> A(0) P(1) P(2) L(3) E(4)
        // Guess:  PPPXX -> P(0) P(1) P(2) X(3) X(4)
        // Pass 1: i=1 P==P → CORRECT; i=2 P==P → CORRECT
        // Pass 2: i=0 P — both target P's used → ABSENT
        val result = GameEngine.evaluateGuess("PPPXX", "APPLE")
        assertEquals(LetterState.ABSENT, result[0].state)   // P - no more P's available
        assertEquals(LetterState.CORRECT, result[1].state)   // P - exact match
        assertEquals(LetterState.CORRECT, result[2].state)   // P - exact match
        assertEquals(LetterState.ABSENT, result[3].state)    // X
        assertEquals(LetterState.ABSENT, result[4].state)    // X
    }

    @Test
    fun `case insensitive comparison`() {
        val result = GameEngine.evaluateGuess("apple", "APPLE")
        result.forEach { assertEquals(LetterState.CORRECT, it.state) }
    }

    @Test
    fun `mixed correct wrong_position and absent`() {
        // CRANE = C(0) R(1) A(2) N(3) E(4)
        // CHART = C(0) H(1) A(2) R(3) T(4)
        // C→CORRECT, H→ABSENT, A→CORRECT, R→WRONG_POSITION, T→ABSENT
        val result = GameEngine.evaluateGuess("CHART", "CRANE")
        assertEquals(LetterState.CORRECT, result[0].state)        // C
        assertEquals(LetterState.ABSENT, result[1].state)          // H
        assertEquals(LetterState.CORRECT, result[2].state)         // A
        assertEquals(LetterState.WRONG_POSITION, result[3].state)  // R
        assertEquals(LetterState.ABSENT, result[4].state)          // T
    }

    // ── scoreForGuess ────────────────────────────────────────────────

    @Test
    fun `scoreForGuess returns correct points for each guess number`() {
        assertEquals(100, GameEngine.scoreForGuess(1))
        assertEquals(80, GameEngine.scoreForGuess(2))
        assertEquals(60, GameEngine.scoreForGuess(3))
        assertEquals(40, GameEngine.scoreForGuess(4))
        assertEquals(20, GameEngine.scoreForGuess(5))
        assertEquals(10, GameEngine.scoreForGuess(6))
        assertEquals(5, GameEngine.scoreForGuess(7))
    }

    // ── roundBonus ──────────────────────────────────────────────────

    @Test
    fun `roundBonus increases with round number`() {
        assertEquals(50, GameEngine.roundBonus(1))
        assertEquals(100, GameEngine.roundBonus(2))
        assertEquals(150, GameEngine.roundBonus(3))
        assertEquals(200, GameEngine.roundBonus(4))
    }

    // ── streakBonus ─────────────────────────────────────────────────

    @Test
    fun `streakBonus is zero for streak of 1 or less`() {
        assertEquals(0, GameEngine.streakBonus(0))
        assertEquals(0, GameEngine.streakBonus(1))
    }

    @Test
    fun `streakBonus scales with streak`() {
        assertEquals(20, GameEngine.streakBonus(2))
        assertEquals(50, GameEngine.streakBonus(5))
        assertEquals(100, GameEngine.streakBonus(10))
    }
}
