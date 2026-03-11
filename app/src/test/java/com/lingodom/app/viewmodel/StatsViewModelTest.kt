package com.lingodom.app.viewmodel

import app.cash.turbine.test
import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.core.model.Rank
import com.lingodom.app.data.PreferencesManagerInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val fakeStatsFlow = MutableStateFlow(PlayerStats())

    private val fakePrefs = object : PreferencesManagerInterface {
        override val statsFlow: Flow<PlayerStats> = fakeStatsFlow
        override val timerDurationFlow: Flow<Int> = MutableStateFlow(60)
        override val soundEnabledFlow: Flow<Boolean> = MutableStateFlow(true)
        override suspend fun recordWin(score: Int, guessNumber: Int, roundNumber: Int) {}
        override suspend fun recordLoss() {}
        override suspend fun setTimerDuration(seconds: Int) {}
        override suspend fun setSoundEnabled(enabled: Boolean) {}
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
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

            fakeStatsFlow.value = PlayerStats(
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
            fakeStatsFlow.value = PlayerStats(
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

            fakeStatsFlow.value = PlayerStats(totalScore = 5000)
            val state = awaitItem()
            assertEquals(Rank.LEXICON_MASTER, state.rank)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
