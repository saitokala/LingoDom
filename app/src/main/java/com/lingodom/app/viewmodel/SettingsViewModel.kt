package com.lingodom.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lingodom.app.LingoDomApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val timerDuration: Int,
    val soundEnabled: Boolean
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = (application as LingoDomApp).preferencesManager

    val uiState: StateFlow<SettingsUiState> = combine(
        prefs.timerDurationFlow,
        prefs.soundEnabledFlow
    ) { timer, sound ->
        SettingsUiState(timerDuration = timer, soundEnabled = sound)
    }.stateIn(
        viewModelScope, 
        SharingStarted.WhileSubscribed(5_000), 
        SettingsUiState(timerDuration = 60, soundEnabled = true)
    )

    fun setTimerDuration(seconds: Int) {
        viewModelScope.launch { prefs.setTimerDuration(seconds) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setSoundEnabled(enabled) }
    }
}
