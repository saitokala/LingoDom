package com.lingodom.app.viewmodel

import app.cash.turbine.test
import com.lingodom.app.core.model.GameRound
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `initial state has default values`() = runTest {
        val vm = HomeViewModel(fakePrefs)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(Rank.NOVICE, state.rank)
            assertEquals(listOf(GameRound.STARTER), state.unlockedRounds)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stats update unlocks rounds`() = runTest {
        val vm = HomeViewModel(fakePrefs)

        vm.uiState.test {
            awaitItem() // initial

            fakePrefs.statsState.value = PlayerStats(totalScore = 2500)
            val state = awaitItem()
            assertEquals(Rank.LINGUIST, state.rank)
            assertTrue(state.unlockedRounds.contains(GameRound.CLASSIC))
            assertTrue(state.unlockedRounds.contains(GameRound.CHALLENGE))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `progress bar computes correctly`() = runTest {
        val vm = HomeViewModel(fakePrefs)

        vm.uiState.test {
            awaitItem()

            fakePrefs.statsState.value = PlayerStats(totalScore = 250)
            val state = awaitItem()
            // Next unlock is CLASSIC at 500, progress = 250/500 = 0.5
            assertEquals(0.5f, state.nextUnlockProgress, 0.01f)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
