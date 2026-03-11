package com.lingodom.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RankTest {

    @Test
    fun `fromScore returns NOVICE for score 0`() {
        assertEquals(Rank.NOVICE, Rank.fromScore(0))
    }

    @Test
    fun `fromScore returns WORDSMITH at 500`() {
        assertEquals(Rank.WORDSMITH, Rank.fromScore(500))
    }

    @Test
    fun `fromScore returns LINGUIST at 2000`() {
        assertEquals(Rank.LINGUIST, Rank.fromScore(2000))
    }

    @Test
    fun `fromScore returns LEXICON_MASTER at 5000`() {
        assertEquals(Rank.LEXICON_MASTER, Rank.fromScore(5000))
    }

    @Test
    fun `fromScore returns LINGODOM_CHAMPION at 10000`() {
        assertEquals(Rank.LINGODOM_CHAMPION, Rank.fromScore(10000))
    }

    @Test
    fun `fromScore returns correct rank for in-between scores`() {
        assertEquals(Rank.NOVICE, Rank.fromScore(499))
        assertEquals(Rank.WORDSMITH, Rank.fromScore(1999))
        assertEquals(Rank.LINGUIST, Rank.fromScore(4999))
        assertEquals(Rank.LEXICON_MASTER, Rank.fromScore(9999))
        assertEquals(Rank.LINGODOM_CHAMPION, Rank.fromScore(50000))
    }
}
