package com.weblite.kgf.ui.screens.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.graphicsLayer
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.weblite.kgf.ui.components.HOMESCREEN
import androidx.navigation.NavController

@Composable
fun TigerAndDragonGameScreen(
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
) {
    var showTigerOverlay by remember { mutableStateOf(false) }
    var lastTimer by remember { mutableStateOf(3) }
    var timerValue by remember { mutableStateOf(3) }
    var selectedChipIndex by remember { mutableStateOf(-1) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // Hide system status bar when this screen is shown
    DisposableEffect(Unit) {
        val activity = context as? Activity
        val window = activity?.window
        val decorView = window?.decorView
        val controller = window?.let {
            androidx.core.view.WindowInsetsControllerCompat(it, decorView!!)
        }
        controller?.hide(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        onDispose {
            controller?.show(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        }
    }
    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    

    // Simulate timer for preview/demo. Replace with your timer state in real app.
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timerValue = if (timerValue == 0) 3 else timerValue - 1
        }
    }

    // Show overlay for 1s when timer reaches 0 and resets to 30
    LaunchedEffect(timerValue) {
        if (timerValue == 0) {
            showTigerOverlay = true
            // Show overlay for 1 second, then hide and reset coin scale
            scope.launch {
                delay(1000)
                showTigerOverlay = false
                selectedChipIndex = -1
            }
        }
        if (timerValue == 3) {
            lastTimer = timerValue
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A), // dark blue top
                        Color(0xFF1B263B), // mid
                        Color(0xFF274472)  // dark blue bottom
                    )
                )
            )
    ) {
        // Main game area with lowest z-index
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f) // Lowest z-index
        ) {
            val overlayHeight = maxHeight * 0.75f
            val overlayWidth = maxWidth * 0.18f

            // Main game table area (increased size and better positioning)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.78f) // Increased width
                    .fillMaxHeight(0.75f) // Increased height
                    .align(Alignment.BottomCenter)
                    .padding(bottom = maxHeight * 0.16f) // Adjusted bottom padding
            ) {
                val tableWidth = maxWidth
                val tableHeight = maxHeight
                val borderThickness = tableWidth * 0.04f // Slightly reduced border
                val chipSize = tableWidth * 0.10f
                val avatarSize = chipSize * 0.8f
                val cornerRadius = tableHeight * 0.5f
                val borderCornerRadius = tableHeight * 0.38f

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(tableWidth, tableHeight)
                ) {
                    // Outer border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0A2342), // deep blue
                                        Color(0xFF274472)
                                    )
                                ),
                                shape = RoundedCornerShape(borderCornerRadius)
                            )
                    ) {}
                    // Inner border
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(tableWidth - borderThickness, tableHeight - borderThickness)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1B263B), // lighter blue
                                        Color(0xFF415A77)
                                    )
                                ),
                                shape = RoundedCornerShape(borderCornerRadius)
                            )
                    ) {}
                    // Main table area
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(tableWidth - borderThickness * 2, tableHeight - borderThickness * 2)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFE0E7FF), // soft blue white
                                        Color(0xFFB3C7F7), // pastel blue
                                        Color(0xFFB39DDB)  // soft purple
                                    )
                                ),
                                shape = RoundedCornerShape(cornerRadius)
                            )
                    ) {
                        // Removed the inner Box wrapper and padding to maximize space
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Dragon section - no padding, larger
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f) // Use weight instead of fixed width
                                    .clip(RoundedCornerShape(
                                        topStart = cornerRadius,
                                        bottomStart = cornerRadius,
                                        topEnd = 0.dp,
                                        bottomEnd = 0.dp
                                    ))
                                    .background(Color(0xFF2B3A4B))
                                    .clickable { }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painterResource(R.drawable.dragon),
                                        contentDescription = "Dragon",
                                        modifier = Modifier
                                            .size(avatarSize * 2.8f) // Increased size
                                            .alpha(0.65f) // Slightly more visible
                                    )
                                    Spacer(modifier = Modifier.height(tableHeight * 0.02f))
                                    androidx.compose.material3.Text(
                                        text = "Dragon\n1:2",
                                        color = Color.White, // Changed to white for better visibility
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.08f).value.sp, // Increased font size
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }

                            // Tie section - no padding, larger
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f) // Use weight instead of fixed width
                                    .background(Color(0xFF43A047))
                                    .clickable { }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    androidx.compose.material3.Text(
                                        text = "TIE\n1:8",
                                        color = Color.White, // Changed to white for better visibility
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.10f).value.sp, // Increased font size
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }

                            // Tiger section - no padding, larger
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f) // Use weight instead of fixed width
                                    .clip(RoundedCornerShape(
                                        topStart = 0.dp,
                                        bottomStart = 0.dp,
                                        topEnd = cornerRadius,
                                        bottomEnd = cornerRadius
                                    ))
                                    .background(Color(0xFFED6A5A))
                                    .clickable { }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painterResource(R.drawable.tiger),
                                        contentDescription = "Tiger",
                                        modifier = Modifier
                                            .size(avatarSize * 2.8f) // Increased size
                                            .alpha(0.65f) // Slightly more visible
                                    )
                                    Spacer(modifier = Modifier.height(tableHeight * 0.02f))
                                    androidx.compose.material3.Text(
                                        text = "Tiger\n1:2",
                                        color = Color.White, // Changed to white for better visibility
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.08f).value.sp, // Increased font size
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Left user chips overlay (move closer to table)
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .height(overlayHeight * 1.0f) // taller so all chips fit
                    .width(overlayWidth * 1.5f)
                    .padding(start = 1.dp, bottom = maxHeight * 0.13f) // Adjusted padding
                    .align(Alignment.BottomStart)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top: User 1
                    UserChip(
                        "User 1", 500.0, R.drawable.user1,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.TopEnd)
                            .padding(top = overlayHeight * 0.02f, end = overlayWidth * 0.38f)
                    )
                    // Center: User 2
                    UserChip(
                        "User 2", 200.0, R.drawable.user2,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.CenterEnd)
                            .padding(end = overlayWidth * 0.80f)
                    )
                    // Bottom: User 3
                    UserChip(
                        "User 3", 100.0, R.drawable.user3,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.BottomEnd)
                            .padding(bottom = overlayHeight * 0.02f, end = overlayWidth * 0.48f)
                    )
                }
            }

            // Right user chips overlay (move closer to table)
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(overlayHeight * 1.0f) // taller so all chips fit
                    .width(overlayWidth * 1.5f)
                    .padding(end = 1.dp, bottom = maxHeight * 0.16f) // Adjusted padding
                    .align(Alignment.BottomEnd)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top: User 4
                    UserChip(
                        "User 4", 500.0, R.drawable.user4,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.TopStart)
                            .padding(top = overlayHeight * 0.02f, start = overlayWidth * 0.38f)
                    )
                    // Center: User 5
                    UserChip(
                        "User 5", 200.0, R.drawable.user5,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.CenterStart)
                            .padding(start = overlayWidth * 0.80f)
                    )
                    // Bottom: User 6
                    UserChip(
                        "User 6", 100.0, R.drawable.user6,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.BottomStart)
                            .padding(bottom = overlayHeight * 0.02f, start = overlayWidth * 0.38f)
                    )
                }
            }

            // History coins box with medium z-index, align just above black border
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .absoluteOffset(y = (this@BoxWithConstraints.maxHeight * 0.19f)) // Adjusted position
                    .zIndex(2f),
                contentAlignment = Alignment.TopCenter
            ) {
                // Cylindrical background for all coins
                Box(
                    modifier = Modifier
                        .padding(horizontal = this@BoxWithConstraints.maxWidth * 0.10f)
                        .fillMaxWidth(0.80f)
                        .height(48.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1B263B),
                                    Color(0xFF415A77)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)
                    ) {
                        val historyRaw = listOf("T", "T", "Tie", "D", "D", "Tie", "T", "Tie")
                        val visibleHistory = buildList {
                            add("...")
                            val count = historyRaw.size
                            if (count >= 8) {
                                addAll(historyRaw.take(8))
                            } else {
                                addAll(historyRaw)
                                repeat(8 - count) { add("...") }
                            }
                        }
                        visibleHistory.forEachIndexed { idx, it ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when (it) {
                                            "T" -> Color(0xAAED6A5A)
                                            "D" -> Color(0xAA3CB371)
                                            "Tie" -> Color(0xAA4FC3F7)
                                            "..." -> Color(0xAA888888)
                                            else -> Color(0xAA888888)
                                        },
                                        shape = GenericShape { size, _ ->
                                            // Octagon shape
                                            val w = size.width
                                            val h = size.height
                                            val midX = w / 2f
                                            val midY = h / 2f
                                            val r = w / 2f
                                            for (i in 0..7) {
                                                val angle = Math.toRadians((45 * i - 22.5).toDouble())
                                                val x = midX + r * Math.cos(angle).toFloat()
                                                val y = midY + r * Math.sin(angle).toFloat()
                                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                                            }
                                            close()
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Text(
                                    text = if (idx == 0) "..." else it,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom betting chips with medium z-index (move a bit up)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 19.dp)
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .height(44.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFB3C7F7),
                                Color(0xFFB39DDB)
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val chipImages = listOf(
                        R.drawable.chip_10,
                        R.drawable.chip_50,
                        R.drawable.chip_500,
                        R.drawable.chip_1000,
                        R.drawable.chip_5000
                    )
                    val chipValues = listOf(10, 50, 500, 1000, 5000)

                    chipValues.forEachIndexed { idx, value ->
                        val isSelected = idx == selectedChipIndex
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.3f else 1f,
                            label = "chipScale"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(36.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .background(
                                    Brush.verticalGradient(
                                        colors = when (value) {
                                            10 -> listOf(Color(0xFFED6A5A), Color(0xFFF7B267))
                                            50 -> listOf(Color(0xFF3CB371), Color(0xFFB2F7EF))
                                            500 -> listOf(Color(0xFF4FC3F7), Color(0xFFB39DDB))
                                            1000 -> listOf(Color(0xFFD1B06B), Color(0xFFB39DDB))
                                            5000 -> listOf(Color(0xFF415A77), Color(0xFFB39DDB))
                                            else -> listOf(Color.LightGray, Color.Gray)
                                        }
                                    ),
                                    shape = CircleShape
                                )
                                .clickable { selectedChipIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(chipImages[idx % chipImages.size]),
                                contentDescription = "Chip $value",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Timer and animated images box at the very top of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .zIndex(10f),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Dragon image with vibration animation
                val dragonVibe = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(Unit) {
                    while (true) {
                        dragonVibe.animateTo(
                            targetValue = 18f,
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 300,
                                easing = androidx.compose.animation.core.LinearEasing
                            )
                        )
                        dragonVibe.animateTo(
                            targetValue = -18f,
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 300,
                                easing = androidx.compose.animation.core.LinearEasing
                            )
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.dragon),
                    contentDescription = "Dragon Side",
                    modifier = Modifier
                        .size(110.dp)
                        .offset(x = dragonVibe.value.dp)
                )
                Spacer(modifier = Modifier.width(40.dp))

                // Center timer (smaller)
                Box(
                    modifier = Modifier
                        .background(Color(0xFFD1B06B), RoundedCornerShape(16.dp))
                        .padding(horizontal = 32.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = timerValue.toString(),
                        color = Color.Black,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))

                // Tiger image with vibration animation
                val tigerVibe = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(Unit) {
                    while (true) {
                        tigerVibe.animateTo(
                            targetValue = -18f,
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 300,
                                easing = androidx.compose.animation.core.LinearEasing
                            )
                        )
                        tigerVibe.animateTo(
                            targetValue = 18f,
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 300,
                                easing = androidx.compose.animation.core.LinearEasing
                            )
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.tiger),
                    contentDescription = "Tiger Side",
                    modifier = Modifier
                        .size(110.dp)
                        .offset(x = tigerVibe.value.dp)
                )
            }
        }

        // Tiger overlay when timer resets
        if (showTigerOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
                    .zIndex(10f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.tiger),
                    contentDescription = "Tiger Overlay",
                    modifier = Modifier.size(220.dp)
                )
            }
        }
    }
}

@Composable
fun UserChip(userName: String, amount: Double, avatarRes: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = userName,
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, shape = CircleShape)
                .padding(1.dp)
        )
        Spacer(modifier = Modifier.height(1.dp))
        androidx.compose.material3.Text(
            text = userName,
            color = Color(0xFF39FF14), // more opaque green
            fontSize = 10.sp,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x5539FF14), // subtle glassy effect
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 3.dp, vertical = 1.dp)
        )
        androidx.compose.material3.Text(
            text = String.format("%.2f", amount),
            color = Color(0xFF39FF14), // more opaque green
            fontSize = 10.sp,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x5539FF14),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 3.dp, vertical = 1.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 480)
@Composable
fun TigerAndDragonGameScreenPreview() {
    TigerAndDragonGameScreen(
        onShowTopBar = {},
        onShowBottomBar = {}
    )
}