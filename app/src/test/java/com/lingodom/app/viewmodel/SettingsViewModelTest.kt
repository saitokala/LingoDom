package com.lingodom.app.viewmodel

import app.cash.turbine.test
import com.lingodom.app.core.model.PlayerStats
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
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val timerFlow = MutableStateFlow(60)
    private val soundFlow = MutableStateFlow(true)

    private var lastTimerSet: Int? = null
    private var lastSoundSet: Boolean? = null

    private val fakePrefs = object : PreferencesManagerInterface {
        override val statsFlow: Flow<PlayerStats> = MutableStateFlow(PlayerStats())
        override val timerDurationFlow: Flow<Int> = timerFlow
        override val soundEnabledFlow: Flow<Boolean> = soundFlow
        override suspend fun recordWin(score: Int, guessNumber: Int, roundNumber: Int) {}
        override suspend fun recordLoss() {}
        override suspend fun setTimerDuration(seconds: Int) { lastTimerSet = seconds }
        override suspend fun setSoundEnabled(enabled: Boolean) { lastSoundSet = enabled }
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
    fun `initial state reflects preferences`() = runTest {
        val vm = SettingsViewModel(fakePrefs)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(60, state.timerDuration)
            assertEquals(true, state.soundEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setTimerDuration delegates to preferences`() = runTest {
        val vm = SettingsViewModel(fakePrefs)
        vm.setTimerDuration(30)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(30, lastTimerSet)
    }

    @Test
    fun `setSoundEnabled delegates to preferences`() = runTest {
        val vm = SettingsViewModel(fakePrefs)
        vm.setSoundEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(false, lastSoundSet)
    }
}
