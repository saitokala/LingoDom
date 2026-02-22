package com.lingodom.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingodom.app.core.model.LetterState
import com.lingodom.app.ui.theme.AbsentGray
import com.lingodom.app.ui.theme.CorrectGreen
import com.lingodom.app.ui.theme.EmptyTileBorder
import com.lingodom.app.ui.theme.WrongPositionYellow

@Composable
fun LetterTile(
    letter: Char?,
    state: LetterState,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = when (state) {
            LetterState.CORRECT -> CorrectGreen
            LetterState.WRONG_POSITION -> WrongPositionYellow
            LetterState.ABSENT -> AbsentGray
            LetterState.EMPTY -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300),
        label = "tileColor"
    )

    val borderColor = when (state) {
        LetterState.EMPTY -> if (letter != null)
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else EmptyTileBorder
        else -> Color.Transparent
    }

    val textColor = when (state) {
        LetterState.EMPTY -> MaterialTheme.colorScheme.onBackground
        else -> Color.White
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(4.dp))
    ) {
        if (letter != null) {
            Text(
                text = letter.uppercase(),
                fontSize = (size.value * 0.45f).sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
