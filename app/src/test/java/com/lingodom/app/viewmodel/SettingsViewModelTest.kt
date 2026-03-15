package com.lingodom.app.viewmodel

import app.cash.turbine.test
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
class SettingsViewModelTest {

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
        assertEquals(30, fakePrefs.lastTimerSet)
    }

    @Test
    fun `setSoundEnabled delegates to preferences`() = runTest {
        val vm = SettingsViewModel(fakePrefs)
        vm.setSoundEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(false, fakePrefs.lastSoundSet)
    }
}
