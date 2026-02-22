package com.lingodom.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lingodom.app.LingoDomApp
import com.lingodom.app.core.model.GameRound
import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.core.model.Rank
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val stats: PlayerStats = PlayerStats(),
    val rank: Rank = Rank.NOVICE,
    val unlockedRounds: List<GameRound> = listOf(GameRound.STARTER),
    val nextUnlockRound: GameRound? = GameRound.CLASSIC,
    val nextUnlockProgress: Float = 0f
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = (application as LingoDomApp).preferencesManager

    val uiState: StateFlow<HomeUiState> = prefs.statsFlow
        .map { stats ->
            val rank = Rank.fromScore(stats.totalScore)
            val unlocked = GameRound.entries.filter { stats.totalScore >= it.unlockScore }
            val nextLocked = GameRound.entries.firstOrNull { it !in unlocked }
            val progress = if (nextLocked != null)
                (stats.totalScore.toFloat() / nextLocked.unlockScore).coerceIn(0f, 1f)
            else 1f

            HomeUiState(
                stats = stats,
                rank = rank,
                unlockedRounds = unlocked,
                nextUnlockRound = nextLocked,
                nextUnlockProgress = progress
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
