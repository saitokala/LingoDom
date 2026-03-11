package com.lingodom.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lingodom.app.core.model.PlayerStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lingodom_prefs")

/**
 * Manages all persisted preferences: player stats, settings, theme.
 * Uses Jetpack DataStore (Preferences) — no account or network required.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesManagerInterface {

    // ── Keys ────────────────────────────────────────────────────────────
    private companion object {
        val TOTAL_SCORE = intPreferencesKey("total_score")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val TOTAL_WORDS_PLAYED = intPreferencesKey("total_words_played")
        val TOTAL_WORDS_WON = intPreferencesKey("total_words_won")
        val WORDS_WON_R1 = intPreferencesKey("words_won_r1")
        val WORDS_WON_R2 = intPreferencesKey("words_won_r2")
        val WORDS_WON_R3 = intPreferencesKey("words_won_r3")
        val WORDS_WON_R4 = intPreferencesKey("words_won_r4")
        val GUESS_1 = intPreferencesKey("guess_1")
        val GUESS_2 = intPreferencesKey("guess_2")
        val GUESS_3 = intPreferencesKey("guess_3")
        val GUESS_4 = intPreferencesKey("guess_4")
        val GUESS_5 = intPreferencesKey("guess_5")
        val GUESS_6 = intPreferencesKey("guess_6")
        val TIMER_DURATION = intPreferencesKey("timer_duration")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")

        fun guessKey(n: Int) = when (n) {
            1 -> GUESS_1; 2 -> GUESS_2; 3 -> GUESS_3
            4 -> GUESS_4; 5 -> GUESS_5; else -> GUESS_6
        }

        fun roundKey(n: Int) = when (n) {
            1 -> WORDS_WON_R1; 2 -> WORDS_WON_R2
            3 -> WORDS_WON_R3; else -> WORDS_WON_R4
        }
    }

    // ── Flows ───────────────────────────────────────────────────────────

    override val statsFlow: Flow<PlayerStats> = context.dataStore.data.map { p ->
        PlayerStats(
            totalScore = p[TOTAL_SCORE] ?: 0,
            currentStreak = p[CURRENT_STREAK] ?: 0,
            bestStreak = p[BEST_STREAK] ?: 0,
            totalWordsPlayed = p[TOTAL_WORDS_PLAYED] ?: 0,
            totalWordsWon = p[TOTAL_WORDS_WON] ?: 0,
            wordsWonPerRound = mapOf(
                1 to (p[WORDS_WON_R1] ?: 0),
                2 to (p[WORDS_WON_R2] ?: 0),
                3 to (p[WORDS_WON_R3] ?: 0),
                4 to (p[WORDS_WON_R4] ?: 0)
            ),
            guessDistribution = listOf(
                p[GUESS_1] ?: 0, p[GUESS_2] ?: 0, p[GUESS_3] ?: 0,
                p[GUESS_4] ?: 0, p[GUESS_5] ?: 0, p[GUESS_6] ?: 0
            )
        )
    }

    override val timerDurationFlow: Flow<Int> = context.dataStore.data.map { it[TIMER_DURATION] ?: 60 }
    override val soundEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: true }

    // ── Mutations ───────────────────────────────────────────────────────

    override suspend fun recordWin(score: Int, guessNumber: Int, roundNumber: Int) {
        context.dataStore.edit { p ->
            val newStreak = (p[CURRENT_STREAK] ?: 0) + 1
            p[TOTAL_SCORE] = (p[TOTAL_SCORE] ?: 0) + score
            p[CURRENT_STREAK] = newStreak
            p[BEST_STREAK] = maxOf(p[BEST_STREAK] ?: 0, newStreak)
            p[TOTAL_WORDS_PLAYED] = (p[TOTAL_WORDS_PLAYED] ?: 0) + 1
            p[TOTAL_WORDS_WON] = (p[TOTAL_WORDS_WON] ?: 0) + 1
            val rk = roundKey(roundNumber)
            p[rk] = (p[rk] ?: 0) + 1
            val gk = guessKey(guessNumber)
            p[gk] = (p[gk] ?: 0) + 1
        }
    }

    override suspend fun recordLoss() {
        context.dataStore.edit { p ->
            p[CURRENT_STREAK] = 0
            p[TOTAL_WORDS_PLAYED] = (p[TOTAL_WORDS_PLAYED] ?: 0) + 1
        }
    }

    override suspend fun setTimerDuration(seconds: Int) {
        context.dataStore.edit { it[TIMER_DURATION] = seconds }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_ENABLED] = enabled }
    }
}
