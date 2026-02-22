package com.lingodom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingodom.app.core.model.GameRound
import com.lingodom.app.ui.theme.CorrectGreen
import com.lingodom.app.ui.theme.LingoAccent
import com.lingodom.app.ui.theme.WrongPositionYellow
import com.lingodom.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onPlayRound: (GameRound) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Top bar ─────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToStats) {
                Icon(Icons.Default.BarChart, "Stats", tint = MaterialTheme.colorScheme.onBackground)
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, "Settings", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Title ───────────────────────────────────────────────
        Text(
            text = "LingoDom",
            style = MaterialTheme.typography.headlineLarge,
            color = CorrectGreen,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Master the words",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(20.dp))

        // ── Rank badge ──────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CorrectGreen.copy(alpha = 0.15f))
        ) {
            Text(text = state.rank.emoji, fontSize = 32.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.rank.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${state.stats.totalScore} pts",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // ── Progress to next unlock ─────────────────────────────
        if (state.nextUnlockRound != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Next unlock: ${state.nextUnlockRound!!.displayName} (${state.nextUnlockRound!!.unlockScore} pts)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { state.nextUnlockProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = WrongPositionYellow,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Round cards ─────────────────────────────────────────
        Text(
            text = "Choose a Round",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))

        GameRound.entries.forEach { round ->
            val unlocked = round in state.unlockedRounds
            RoundCard(
                round = round,
                unlocked = unlocked,
                wordsWon = state.stats.wordsWonPerRound[round.roundNumber] ?: 0,
                onClick = { if (unlocked) onPlayRound(round) }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(16.dp))

        // ── Quick stats ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MiniStat("Streak", state.stats.currentStreak.toString())
            MiniStat("Best", state.stats.bestStreak.toString())
            MiniStat("Won", state.stats.totalWordsWon.toString())
        }
    }
}

@Composable
private fun RoundCard(
    round: GameRound,
    unlocked: Boolean,
    wordsWon: Int,
    onClick: () -> Unit
) {
    val colors = listOf(CorrectGreen, WrongPositionYellow, Color(0xFF3498DB), LingoAccent)
    val accent = colors.getOrElse(round.roundNumber - 1) { CorrectGreen }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked)
                accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = unlocked, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (unlocked) accent else Color.Gray.copy(alpha = 0.3f))
            ) {
                if (unlocked) {
                    Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(28.dp))
                } else {
                    Icon(Icons.Default.Lock, "Locked", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = round.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (unlocked) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "${round.wordLength} letters · ${round.maxAttempts} attempts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (unlocked && wordsWon > 0) {
                    Text(
                        text = "$wordsWon words solved",
                        style = MaterialTheme.typography.bodySmall,
                        color = accent
                    )
                }
                if (!unlocked) {
                    Text(
                        text = "Unlocks at ${round.unlockScore} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
