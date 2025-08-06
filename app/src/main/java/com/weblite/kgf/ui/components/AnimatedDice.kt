package com.weblite.kgf.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

@Composable
fun AnimatedDice(
    diceValue: Int,
    isRolling: Boolean,
    modifier: Modifier = Modifier,
    diceSize: Dp = 55.dp,
    getDiceDrawable: (Int) -> Int
) {
    // Animate rotation when rolling, reset to 0 when not rolling, always restart on isRolling change
    val rotation = if (isRolling) {
        // Restart animation every time isRolling changes to true
        val infiniteTransition = rememberInfiniteTransition(label = "dice-rotation")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "dice-rotation"
        ).value
    } else 0f

    val painter = painterResource(id = getDiceDrawable(diceValue))

    Box(
        modifier = modifier.size(diceSize),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Dice $diceValue",
            modifier = Modifier.rotate(rotation).size(diceSize),
            contentScale = ContentScale.Fit
        )
    }
}
