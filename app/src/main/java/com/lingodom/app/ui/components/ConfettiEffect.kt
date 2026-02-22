package com.lingodom.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val angle: Float,
    val speed: Float,
    val radius: Float,
    val color: Color
)

private val confettiColors = listOf(
    Color(0xFF6AAA64), // green
    Color(0xFFC9B458), // yellow
    Color(0xFFE74C3C), // red
    Color(0xFF3498DB), // blue
    Color(0xFF9B59B6), // purple
    Color(0xFFF39C12), // orange
)

/**
 * A lightweight confetti burst that plays once and calls [onComplete].
 */
@Composable
fun ConfettiEffect(
    trigger: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        List(60) {
            Particle(
                x = Random.nextFloat(),
                angle = Random.nextFloat() * 360f,
                speed = 300f + Random.nextFloat() * 500f,
                radius = 4f + Random.nextFloat() * 6f,
                color = confettiColors.random()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 1500, easing = LinearEasing))
        onComplete()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        particles.forEach { p ->
            val rad = Math.toRadians(p.angle.toDouble())
            val dx = cos(rad).toFloat() * p.speed * t
            val dy = sin(rad).toFloat() * p.speed * t + 400f * t * t // gravity
            val alpha = (1f - t).coerceIn(0f, 1f)
            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius * (1f - t * 0.5f),
                center = Offset(
                    x = size.width * p.x + dx,
                    y = -20f + dy
                )
            )
        }
    }
}
