package com.lingodom.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingodom.app.ui.theme.CorrectGreen
import com.lingodom.app.ui.theme.WrongPositionYellow

/**
 * Animated countdown bar that changes colour as time runs out.
 */
@Composable
fun TimerBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    val fraction = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 1f

    val animFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(300, easing = LinearEasing),
        label = "timerFrac"
    )

    val barColor by animateColorAsState(
        targetValue = when {
            fraction > 0.5f -> CorrectGreen
            fraction > 0.25f -> WrongPositionYellow
            else -> Color.Red
        },
        animationSpec = tween(300),
        label = "timerCol"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animFraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = "${remainingSeconds}s",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = barColor
        )
    }
}
