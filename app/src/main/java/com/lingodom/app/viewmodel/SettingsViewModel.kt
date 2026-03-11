package com.lingodom.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingodom.app.data.PreferencesManagerInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val timerDuration: Int,
    val soundEnabled: Boolean
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PreferencesManagerInterface
) : ViewModel() {

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
