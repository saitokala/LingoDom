package com.lingodom.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lingodom.app.LingoDomApp
import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.core.model.Rank
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val stats: PlayerStats = PlayerStats(),
    val rank: Rank = Rank.NOVICE,
    val winPercentage: Float = 0f,
    val averageGuesses: Float = 0f
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = (application as LingoDomApp).preferencesManager

    val uiState: StateFlow<StatsUiState> = prefs.statsFlow
        .map { s ->
            val winPct = if (s.totalWordsPlayed > 0)
                s.totalWordsWon.toFloat() / s.totalWordsPlayed * 100f else 0f

            val totalGuesses = s.guessDistribution
                .mapIndexed { idx, count -> (idx + 1) * count }.sum()
            val avg = if (s.totalWordsWon > 0)
                totalGuesses.toFloat() / s.totalWordsWon else 0f

            StatsUiState(
                stats = s,
                rank = Rank.fromScore(s.totalScore),
                winPercentage = winPct,
                averageGuesses = avg
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}
