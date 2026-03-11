package com.lingodom.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.core.model.Rank
import com.lingodom.app.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatsUiState(
    val stats: PlayerStats = PlayerStats(),
    val rank: Rank = Rank.NOVICE,
    val winPercentage: Float = 0f,
    val averageGuesses: Float = 0f
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    prefs: PreferencesManager
) : ViewModel() {

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
