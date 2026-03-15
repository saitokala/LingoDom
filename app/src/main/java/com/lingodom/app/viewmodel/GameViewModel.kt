package com.lingodom.app.viewmodel

import android.media.AudioManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingodom.app.core.engine.GameEngine
import com.lingodom.app.core.model.GameRound
import com.lingodom.app.core.model.LetterResult
import com.lingodom.app.core.model.LetterState
import com.lingodom.app.data.PreferencesManager
import com.lingodom.app.data.SoundManager
import com.lingodom.app.data.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ────────────────────────────────────────────────────────

enum class GameStatus { PLAYING, WON, LOST }

data class GameUiState(
    val round: GameRound = GameRound.STARTER,
    val guesses: List<List<LetterResult>> = emptyList(),
    val currentInput: TextFieldValue = TextFieldValue(),
    val firstLetter: Char = ' ',
    val maxAttempts: Int = 5,
    val wordLength: Int = 4,
    val timerSeconds: Int = 10,
    val timerTotal: Int = 10,
    val timerRunning: Boolean = false,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val earnedScore: Int = 0,
    val totalScore: Int = 0,
    val streak: Int = 0,
    val wordsInRound: Int = 0,
    val keyStates: Map<Char, LetterState> = emptyMap(),
    val message: String? = null,
    val targetWord: String = "",
    val newRoundUnlocked: GameRound? = null,
    val showConfetti: Boolean = false
)

// ── ViewModel ───────────────────────────────────────────────────────

@HiltViewModel
class GameViewModel @Inject constructor(
    private val wordRepo: WordRepository,
    private val prefs: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _ui = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _ui.asStateFlow()

    private var targetWord = ""
    private var timerJob: Job? = null
    private var submitting = false

    init {
        viewModelScope.launch {
            val stats = prefs.statsFlow.first()
            val timer = prefs.timerDurationFlow.first()
            _ui.update {
                it.copy(
                    totalScore = stats.totalScore,
                    streak = stats.currentStreak,
                    timerTotal = timer,
                    timerSeconds = timer
                )
            }
        }
    }

    // ── Public API ──────────────────────────────────────────────────

    fun startGame(round: GameRound) {
        targetWord = wordRepo.getRandomWord(round)
        val firstChar = targetWord.first()
        _ui.update {
            GameUiState(
                round = round,
                firstLetter = firstChar,
                maxAttempts = round.maxAttempts,
                wordLength = round.wordLength,
                timerSeconds = it.timerTotal,
                timerTotal = it.timerTotal,
                timerRunning = true,
                totalScore = it.totalScore,
                streak = it.streak,
                currentInput = TextFieldValue(
                    text = firstChar.toString(),
                    selection = TextRange(1)
                )
            )
        }
        startTimer()
    }

    fun onInputChange(newValue: TextFieldValue) {
        val s = _ui.value
        if (s.gameStatus != GameStatus.PLAYING) return

        val oldValue = s.currentInput
        val hint = s.firstLetter.toString()
        val sanitizedText = newValue.text.uppercase().filter { it.isLetter() }

        val textWithHint = if (sanitizedText.isEmpty() || !sanitizedText.startsWith(hint)) {
            hint
        } else {
            sanitizedText
        }

        val cappedText = textWithHint.take(s.wordLength)

        val selectionStart = newValue.selection.start.coerceIn(hint.length, cappedText.length)
        val selectionEnd = newValue.selection.end.coerceIn(hint.length, cappedText.length)

        val finalTfv = TextFieldValue(
            text = cappedText,
            selection = TextRange(selectionStart, selectionEnd)
        )

        if (finalTfv != oldValue) {
            _ui.update { it.copy(currentInput = finalTfv, message = null) }
            if (finalTfv.text.length > oldValue.text.length) {
                playSound(AudioManager.FX_KEY_CLICK)
            }
        }

        if (finalTfv.text.length == s.wordLength) {
            submitGuess()
        }
    }

    private fun submitGuess() {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                val s = _ui.value
                if (s.gameStatus != GameStatus.PLAYING) return@launch

                val guess = s.currentInput.text.uppercase()

                if (guess.length != s.wordLength) {
                    return@launch
                }
                if (!wordRepo.isValidWord(guess)) {
                    _ui.update { it.copy(message = "Not a valid word") }
                    delay(1000L)
                    _ui.update {
                        it.copy(
                            message = null,
                            currentInput = TextFieldValue(
                                text = s.firstLetter.toString(),
                                selection = TextRange(1)
                            )
                        )
                    }
                    return@launch
                }

                val result = GameEngine.evaluateGuess(guess, targetWord)
                val newGuesses = s.guesses + listOf(result)
                val newKeys = mergeKeyStates(s.keyStates, result)
                val guessNum = newGuesses.size

                if (result.all { it.state == LetterState.CORRECT }) {
                    handleWin(newGuesses, newKeys, guessNum)
                } else if (guessNum >= s.maxAttempts) {
                    handleLoss(newGuesses, newKeys)
                } else {
                    handleIncorrectGuess(newGuesses, newKeys)
                }
            } finally {
                submitting = false
            }
        }
    }

    private suspend fun handleWin(newGuesses: List<List<LetterResult>>, newKeys: Map<Char, LetterState>, guessNum: Int) {
        timerJob?.cancel()
        val s = _ui.value
        val wordPts = GameEngine.scoreForGuess(guessNum)
        val roundPts = GameEngine.roundBonus(s.round.roundNumber)
        val streakPts = GameEngine.streakBonus(s.streak + 1)
        val earned = wordPts + roundPts + streakPts

        prefs.recordWin(earned, guessNum, s.round.roundNumber)
        val stats = prefs.statsFlow.first()
        val unlocked = detectNewUnlock(stats.totalScore, stats.totalScore - earned)

        // Play victory jingle
        playSound(AudioManager.FX_KEYPRESS_RETURN)

        _ui.update {
            it.copy(
                guesses = newGuesses,
                currentInput = TextFieldValue(),
                keyStates = newKeys,
                gameStatus = GameStatus.WON,
                earnedScore = earned,
                totalScore = stats.totalScore,
                streak = stats.currentStreak,
                wordsInRound = it.wordsInRound + 1,
                timerRunning = false,
                targetWord = targetWord,
                message = "Excellent! +$earned pts",
                newRoundUnlocked = unlocked,
                showConfetti = true
            )
        }
    }

    private suspend fun handleLoss(newGuesses: List<List<LetterResult>>, newKeys: Map<Char, LetterState>) {
        timerJob?.cancel()
        prefs.recordLoss()
        val stats = prefs.statsFlow.first()

        // Play failure tone
        playSound(AudioManager.FX_KEYPRESS_DELETE)

        _ui.update {
            it.copy(
                guesses = newGuesses,
                currentInput = TextFieldValue(),
                keyStates = newKeys,
                gameStatus = GameStatus.LOST,
                totalScore = stats.totalScore,
                streak = 0,
                timerRunning = false,
                targetWord = targetWord,
                message = "The word was: $targetWord"
            )
        }
    }

    private fun handleIncorrectGuess(newGuesses: List<List<LetterResult>>, newKeys: Map<Char, LetterState>) {
        resetTimer()
        _ui.update {
            it.copy(
                guesses = newGuesses,
                currentInput = TextFieldValue(
                    text = it.firstLetter.toString(),
                    selection = TextRange(1)
                ),
                keyStates = newKeys,
                message = null
            )
        }
    }

    fun dismissConfetti() {
        _ui.update { it.copy(showConfetti = false) }
    }

    // ── Timer ───────────────────────────────────────────────────

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_ui.value.timerSeconds > 0 && _ui.value.gameStatus == GameStatus.PLAYING) {
                delay(1000L)
                _ui.update { it.copy(timerSeconds = (it.timerSeconds - 1).coerceAtLeast(0)) }
            }
            if (_ui.value.gameStatus == GameStatus.PLAYING && _ui.value.timerSeconds <= 0) {
                handleTimerExpiry()
            }
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _ui.update { it.copy(timerSeconds = it.timerTotal) }
        startTimer()
    }

    private fun handleTimerExpiry() {
        val s = _ui.value
        // Count timer expiry as a consumed attempt by adding an empty guess row
        val emptyGuess = List(s.wordLength) { LetterResult(' ', LetterState.ABSENT) }
        val newGuesses = s.guesses + listOf(emptyGuess)

        if (newGuesses.size >= s.maxAttempts) {
            viewModelScope.launch { handleLoss(newGuesses, s.keyStates) }
        } else {
            _ui.update {
                it.copy(
                    guesses = newGuesses,
                    timerSeconds = it.timerTotal,
                    currentInput = TextFieldValue(
                        text = targetWord.first().toString(),
                        selection = TextRange(1)
                    ),
                    message = "Time's up! Try again…"
                )
            }
            startTimer()
        }
    }

    // ── Helpers ─────────────────────────────────────────────────

    private fun playSound(effectType: Int) {
        viewModelScope.launch {
            if (prefs.soundEnabledFlow.first()) {
                soundManager.playSound(effectType)
            }
        }
    }

    private fun mergeKeyStates(
        current: Map<Char, LetterState>,
        result: List<LetterResult>
    ): Map<Char, LetterState> {
        val m = current.toMutableMap()
        result.forEach { lr ->
            val prev = m[lr.letter]
            if (prev == null || lr.state.ordinal < prev.ordinal) {
                m[lr.letter] = lr.state
            }
        }
        return m
    }

    private fun detectNewUnlock(newTotal: Int, oldTotal: Int): GameRound? =
        GameRound.entries.firstOrNull { round: GameRound ->
            round.unlockScore > 0 &&
                    newTotal >= round.unlockScore &&
                    oldTotal < round.unlockScore
        }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
