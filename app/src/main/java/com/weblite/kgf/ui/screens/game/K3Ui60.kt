package com.weblite.kgf.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.weblite.components.CompactExcelTableforK3
import com.example.weblite.components.HistoryTabButton
import com.example.weblite.components.MyHistoryTableforK3
import com.weblite.kgf.R
import com.weblite.kgf.ui.screens.KGFLogoText
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun K3Ui60(
    variant: String,
    onBack: () -> Unit,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    var selectedHistoryTab by remember { mutableStateOf("Game History") }
    var showBettingPopup by remember { mutableStateOf(false) }
    var selectedNumberForBetting by remember { mutableStateOf<Int?>(null) }
    var selectedBetTypeForBetting by remember { mutableStateOf<String?>(null) }
    var totalBalance by remember { mutableStateOf(17436677.65) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(28) }
    var currentPeriod by remember { mutableStateOf("2507031461") }
    var timer by remember { mutableStateOf(60) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            timer--
            if (timer < 0) {
                timer = 30 // Restart after reaching 0
            }
        }
    }

    val k3Balls = listOf(
        K3Ball(3, "207.36X", Color(0xFFE53935)),
        K3Ball(4, "69.12X", Color(0xFF4CAF50)),
        K3Ball(5, "34.56X", Color(0xFFE53935)),
        K3Ball(6, "20.74X", Color(0xFF4CAF50)),
        K3Ball(7, "13.83X", Color(0xFFE53935)),
        K3Ball(8, "9.88X", Color(0xFF4CAF50)),
        K3Ball(9, "8.3X", Color(0xFFE53935)),
        K3Ball(10, "7.64X", Color(0xFF4CAF50)),
        K3Ball(11, "7.64X", Color(0xFFE53935)),
        K3Ball(12, "8.3X", Color(0xFF4CAF50)),
        K3Ball(13, "9.88X", Color(0xFFE53935)),
        K3Ball(14, "13.83X", Color(0xFF4CAF50)),
        K3Ball(15, "20.74X", Color(0xFFE53935)),
        K3Ball(16, "34.56X", Color(0xFF4CAF50)),
        K3Ball(17, "69.12X", Color(0xFFE53935)),
        K3Ball(18, "207.36X", Color(0xFF4CAF50))
    )

    // Bet options (Big, Small, Odd, Even)
    val betOptions = listOf(
        K3BetOption("Big", "1.92X", Color(0xFFE53935)),
        K3BetOption("Small", "1.92X", Color(0xFF4CAF50)),
        K3BetOption("Odd", "1.92X", Color(0xFFFFC107)),
        K3BetOption("Even", "1.92X", Color(0xFF2196F3))
    )

    // Game History Data for K3
    val k3GameHistoryData = remember {
        listOf(
            listOf("2507031461", "Loading...", "Loading...", "Loading..."),
            listOf("2507031460", "3", "Odd", "Small"),
            listOf("2507031459", "8", "Even", "Small"),
            listOf("2507031458", "13", "Odd", "Big"),
            listOf("2507031457", "8", "Even", "Small"),
            listOf("2507031456", "13", "Odd", "Big"),
            listOf("2507031455", "5", "Odd", "Small"),
            listOf("2507031454", "6", "Even", "Small"),
            listOf("2507031453", "8", "Even", "Small"),
            listOf("2507031452", "10", "Even", "Big")
        )
    }

    // My History Data for K3
    val k3MyHistoryData = remember {
        listOf(
            listOf("2506190211", "Small", "10000", "Loss", "0.00"),
            listOf("2505261338", "Big", "10", "Loss", "0.00"),
            listOf("2505220715", "Big", "1", "Loss", "0.00"),
            listOf("2505220715", "Small", "1", "Win", "1.92"),
            listOf("2505220715", "Odd", "1", "Win", "1.92"),
            listOf("2505220715", "Even", "1", "Loss", "0.00"),
            listOf("2505220714", "4", "1", "Loss", "0.00"),
            listOf("2505220714", "3", "1", "Loss", "0.00"),
            listOf("2505220714", "7", "1", "Loss", "0.00"),
            listOf("2505220714", "8", "1", "Loss", "0.00")
        )
    }


    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(52.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(58.dp)
                                    .padding(end = 8.dp)
                            )
                            KGFLogoText()
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black 
                        )
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // screen content here
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
            ) {
                // Wallet Balance Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RectangleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "₹ ${String.format("%,.2f", totalBalance)}",
                                fontSize = 24.sp,
                                fontWeight = Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Wallet Balance",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFE53935
                                        )
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Text("Withdraw", color = Color.White, fontSize = 14.sp)
                                }
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF4CAF50
                                        )
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Text("Deposit", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // K3 Lotre 30s Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Card(
                                modifier = Modifier,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B35)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "K3 Lotre 60sec",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Game Info Card with timer and dice
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Period and Time Remaining Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Period",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = currentPeriod,
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        fontWeight = Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Time Remaining",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = String.format("00:%02d", timeRemaining),
                                        color = Color(0xFFFF6B35),
                                        fontSize = 18.sp,
                                        fontWeight = Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(
                                        color = Color(0xFF39C780), // Outer bright green border
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(8.dp) // Thickness of green border
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = Color(0xFF2E7D32), // Dark green background inside
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(3) { index ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .background(
                                                        Color.LightGray,
                                                        RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.dice),
                                                    contentDescription = "Dice ${index + 1}",
                                                    modifier = Modifier.size(55.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                // COMBINED GAME SECTION - Balls and Bet Options
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // K3 Balls Grid (3-18) - UPDATED TO USE BALL IMAGES
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(330.dp),
                                userScrollEnabled = false
                            ) {
                                items(k3Balls) { ball ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable {
                                            selectedNumberForBetting = ball.number
                                            selectedBetTypeForBetting = null
                                            showBettingPopup = true
                                        }
                                    ) {
                                        // Use actual ball image instead of colored circle
                                        Image(
                                            painter = painterResource(id = getK3BallDrawable(ball.number)),
                                            contentDescription = "Ball ${ball.number}",
                                            modifier = Modifier
                                                .size(45.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ball.multiplier,
                                            fontSize = 15.sp,
                                            fontWeight = Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            // Big/Small/Odd/Even Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                betOptions.forEach { option ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedNumberForBetting = null
                                                selectedBetTypeForBetting = option.name
                                                showBettingPopup = true
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
//                                            .height(60.dp)
                                                .background(
                                                    option.backgroundColor,
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = option.name,
                                                    color = Color.White,
                                                    fontWeight = Bold,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    text = option.multiplier,
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // History Tabs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HistoryTabButton(
                            text = "Game History",
                            isSelected = selectedHistoryTab == "Game History",
                            onClick = { selectedHistoryTab = "Game History" },
                            modifier = Modifier.weight(1f)
                        )
                        HistoryTabButton(
                            text = "My History",
                            isSelected = selectedHistoryTab == "My History",
                            onClick = { selectedHistoryTab = "My History" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Results Table
                item {
                    when (selectedHistoryTab) {
                        "Game History" -> {
                            CompactExcelTableforK3(data = k3GameHistoryData)
                        }

                        "My History" -> {
                            MyHistoryTableforK3(data = k3MyHistoryData)
                        }
                    }
                }

                // Pagination
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Previous", color = Color.White, fontSize = 14.sp)
                        }

                        Text(
                            text = "Page 1 / 27",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ResultTableUI() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp)
    ) {
        // Header Row
        Row(modifier = Modifier.fillMaxWidth()) {
            TableHeaderCell("Period", Modifier.weight(1f))
            TableHeaderCell("Number", Modifier.weight(1f))
            TableHeaderCell("Odd/Even", Modifier.weight(1f))
            TableHeaderCell("Big/Small", Modifier.weight(1f))
        }

        val data = listOf(
            listOf("2506221458", "Loading", "Loading", "Loading"),
            listOf("2506221457", "6", "Even", "Small"),
            listOf("2506221456", "9", "Odd", "Small"),
            listOf("2506221455", "12", "Even", "Big"),
            listOf("2506221454", "3", "Odd", "Small"),
            listOf("2506221453", "8", "Even", "Small"),
            listOf("2506221452", "15", "Odd", "Big"),
            listOf("2506221451", "2", "Even", "Small"),
            listOf("2506221450", "7", "Odd", "Small")
        )

        // Data Rows
        data.forEach { (period, number, oddEven, bigSmall) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Period cell (default color)
                val periodColor = Color(0xfffb630c)
                TableDataCell(period, Modifier.weight(1f),textColor = periodColor)

                // Number cell - color based on odd/even
                val numberColor = when {
                    number == "Loading" -> Color(0xfffb630c)
                    number.toIntOrNull()?.let { it % 2 == 0 } == true -> Color(0xFF1ec825) // Even
                    else -> Color(0xfffe0000)
                }
                TableDataCell(number, Modifier.weight(1f), textColor = numberColor)

                // Odd/Even cell
                val oddEvenColor = when (oddEven) {
                    "Even" -> Color(0xff0195ff)
                    "Odd" -> Color(0xFFffc300)
                    else -> Color(0xfffb630c)
                }
                TableDataCell(oddEven, Modifier.weight(1f), textColor = oddEvenColor)

                // Big/Small cell
                val bigSmallColor = when (bigSmall) {
                    "Big" -> Color(0xfffe0000)
                    "Small" -> Color(0xFF1ec825) // Green
                    else -> Color(0xfffb630c)
                }
                TableDataCell(bigSmall, Modifier.weight(1f), textColor = bigSmallColor)
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, Color.LightGray)
            .background(Color(0xFF4CAF50))
            .padding(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TableDataCell(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black // Default color
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.LightGray)
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Preview(showSystemUi = true)
@Composable
fun GameUIPreview() {
//    val navController = rememberNavController()
//    K3Ui60(
//        variant = "",
//        onBack = { navController.popBackStack() },
//        onShowTopBar = {}, // or { show -> /* handle preview logic */ }
//        onShowBottomBar = {} // same here
//    )
    ResultTableUI()
}