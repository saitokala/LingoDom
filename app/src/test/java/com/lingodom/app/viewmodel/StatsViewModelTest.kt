package com.lingodom.app.viewmodel

import app.cash.turbine.test
import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.core.model.Rank
import com.lingodom.app.fake.FakePreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefs: FakePreferencesManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakePreferencesManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has zero stats`() = runTest {
        val vm = StatsViewModel(fakePrefs)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(Rank.NOVICE, state.rank)
            assertEquals(0f, state.winPercentage, 0.01f)
            assertEquals(0f, state.averageGuesses, 0.01f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `win percentage calculated correctly`() = runTest {
        val vm = StatsViewModel(fakePrefs)

        vm.uiState.test {
            awaitItem()

            fakePrefs.statsState.value = PlayerStats(
                totalWordsPlayed = 10,
                totalWordsWon = 7
            )
            val state = awaitItem()
            assertEquals(70f, state.winPercentage, 0.01f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `average guesses calculated correctly`() = runTest {
        val vm = StatsViewModel(fakePrefs)

        vm.uiState.test {
            awaitItem()

            // 2 wins on guess 1, 3 wins on guess 3 = (2*1 + 3*3) / 5 = 11/5 = 2.2
            fakePrefs.statsState.value = PlayerStats(
                totalWordsWon = 5,
                guessDistribution = listOf(2, 0, 3, 0, 0, 0)
            )
            val state = awaitItem()
            assertEquals(2.2f, state.averageGuesses, 0.01f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `rank updates with score`() = runTest {
        val vm = StatsViewModel(fakePrefs)

        vm.uiState.test {
            awaitItem()

            fakePrefs.statsState.value = PlayerStats(totalScore = 5000)
            val state = awaitItem()
            assertEquals(Rank.LEXICON_MASTER, state.rank)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
