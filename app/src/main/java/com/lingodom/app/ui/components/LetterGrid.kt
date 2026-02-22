package com.lingodom.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lingodom.app.core.model.LetterResult
import com.lingodom.app.core.model.LetterState

/**
 * The main word grid showing past guesses and the current input row.
 */
@Composable
fun LetterGrid(
    guesses: List<List<LetterResult>>,
    currentInput: String,
    currentRow: Int,
    wordLength: Int,
    maxAttempts: Int,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val spacing = 4.dp
    val totalSpacing = spacing * (wordLength + 1)
    val available = screenWidth - 32.dp
    val tileSize = minOf(56.dp, (available - totalSpacing) / wordLength)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        for (row in 0 until maxAttempts) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                for (col in 0 until wordLength) {
                    val (letter, state) = when {
                        // Already-evaluated row
                        row < guesses.size -> {
                            val r = guesses[row]
                            if (col < r.size) r[col].letter to r[col].state
                            else null to LetterState.EMPTY
                        }
                        // Active input row
                        row == currentRow -> currentInput.getOrNull(col) to LetterState.EMPTY
                        // Future empty row
                        else -> null to LetterState.EMPTY
                    }
                    LetterTile(letter = letter, state = state, size = tileSize)
                }
            }
        }
    }
}
