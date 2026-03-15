package com.lingodom.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lingodom.app.core.model.GameRound
import com.lingodom.app.ui.components.ConfettiEffect
import com.lingodom.app.ui.components.LetterGrid
import com.lingodom.app.ui.components.TimerBar
import com.lingodom.app.ui.theme.CorrectGreen
import com.lingodom.app.ui.theme.WrongPositionYellow
import com.lingodom.app.viewmodel.GameStatus
import com.lingodom.app.viewmodel.GameViewModel

@Composable
fun GameScreen(
    round: GameRound,
    onNavigateBack: () -> Unit,
    onPlayAgain: (GameRound) -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Start the game when the composable enters the composition
    LaunchedEffect(round) {
        viewModel.startGame(round)
    }

    // When the game state becomes PLAYING, request focus and show the keyboard.
    LaunchedEffect(state.gameStatus) {
        if (state.gameStatus == GameStatus.PLAYING) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding() // Automatically handle keyboard padding
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Top bar ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.round.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${state.wordLength} letters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${state.totalScore} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorrectGreen
                )
                if (state.streak > 0) {
                    Text(
                        text = "🔥 ${state.streak}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WrongPositionYellow
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Timer ───────────────────────────────────────────
        if (state.timerRunning || state.gameStatus == GameStatus.PLAYING) {
            TimerBar(
                remainingSeconds = state.timerSeconds,
                totalSeconds = state.timerTotal
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Letter grid & Transparent Input ───────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (state.gameStatus == GameStatus.PLAYING) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            LetterGrid(
                guesses = state.guesses,
                currentInput = state.currentInput.text,
                currentRow = state.guesses.size,
                wordLength = state.wordLength,
                maxAttempts = state.maxAttempts
            )

            // This transparent text field overlays the grid. It captures all
            // keyboard input, but its text is invisible.
            if (state.gameStatus == GameStatus.PLAYING) {
                BasicTextField(
                    value = state.currentInput,
                    onValueChange = viewModel::onInputChange,
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester),
                    textStyle = LocalTextStyle.current.copy(color = Color.Transparent),
                    cursorBrush = SolidColor(Color.Transparent)
                )
            }
        }

        // ── Message ─────────────────────────────────────────
        AnimatedVisibility(
            visible = state.message != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = state.message ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = when (state.gameStatus) {
                    GameStatus.WON -> CorrectGreen
                    GameStatus.LOST -> Color.Red
                    else -> WrongPositionYellow
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }

        // ── Result buttons (shown after game ends) ──────────
        if (state.gameStatus != GameStatus.PLAYING) {
            Spacer(Modifier.height(4.dp))

            // Score breakdown for wins
            if (state.gameStatus == GameStatus.WON) {
                Text(
                    text = "+${state.earnedScore} points earned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorrectGreen,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Unlock notification
            state.newRoundUnlocked?.let { newRound ->
                Text(
                    text = "🎉 ${newRound.displayName} Round Unlocked!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = WrongPositionYellow,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Home")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onPlayAgain(state.round) },
                    colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Next Word", color = Color.White)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

    }

    // ── Confetti overlay (in Box, above Column for proper z-ordering) ──
    ConfettiEffect(
        trigger = state.showConfetti,
        onComplete = viewModel::dismissConfetti,
        modifier = Modifier.fillMaxSize()
    )
    }
}
