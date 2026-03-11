package com.lingodom.app.data

import com.lingodom.app.core.model.PlayerStats
import kotlinx.coroutines.flow.Flow

interface PreferencesManagerInterface {
    val statsFlow: Flow<PlayerStats>
    val timerDurationFlow: Flow<Int>
    val soundEnabledFlow: Flow<Boolean>

    suspend fun recordWin(score: Int, guessNumber: Int, roundNumber: Int)
    suspend fun recordLoss()
    suspend fun setTimerDuration(seconds: Int)
    suspend fun setSoundEnabled(enabled: Boolean)
}
