package com.lingodom.app.fake

import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.data.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory fake used across ViewModel unit tests.
 *
 * Exposes mutable flows so tests can push new values, and records
 * the last mutation arguments for assertion.
 */
class FakePreferencesManager(
    initialStats: PlayerStats = PlayerStats(),
    initialTimer: Int = 60,
    initialSoundEnabled: Boolean = true
) : PreferencesManager {

    val statsState = MutableStateFlow(initialStats)
    val timerState = MutableStateFlow(initialTimer)
    val soundState = MutableStateFlow(initialSoundEnabled)

    override val statsFlow: Flow<PlayerStats> = statsState
    override val timerDurationFlow: Flow<Int> = timerState
    override val soundEnabledFlow: Flow<Boolean> = soundState

    var lastRecordedWin: Triple<Int, Int, Int>? = null
        private set
    var recordLossCalled = false
        private set
    var lastTimerSet: Int? = null
        private set
    var lastSoundSet: Boolean? = null
        private set

    override suspend fun recordWin(score: Int, guessNumber: Int, roundNumber: Int) {
        lastRecordedWin = Triple(score, guessNumber, roundNumber)
    }

    override suspend fun recordLoss() {
        recordLossCalled = true
    }

    override suspend fun setTimerDuration(seconds: Int) {
        lastTimerSet = seconds
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        lastSoundSet = enabled
    }
}
