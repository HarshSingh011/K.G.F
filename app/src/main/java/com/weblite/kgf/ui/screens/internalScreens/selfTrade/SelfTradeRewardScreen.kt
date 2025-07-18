package com.example.weblite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weblite.kgf.R
import androidx.compose.ui.unit.sp

data class DailyMission(
    val title: String,
    val progress: String,
    val target: String,
    val reward: String,
    val isCompleted: Boolean = false
)

@Composable
fun SelfTradeRewardScreen(
    onBackClick: () -> Unit = {},
    onNavigationClick: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Daily Missions") }

    val dailyMissions = remember {
        listOf(
            DailyMission("Daily betting bonus", "0", "500", "₹5.00"),
            DailyMission("Daily betting bonus", "0", "5000", "₹40.00"),
            DailyMission("Daily betting bonus", "0", "50000", "₹500.00"),
            DailyMission("Daily betting bonus", "0", "100000", "₹1,000.00")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E3A8A)),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header section
                item {
                    Column {
                        // Self Trade Reward Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0B1331))
                                .size(120.dp)
                                .padding(start = 10.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Self trade Reward",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .size(24.dp)
                                            .clickable { onBackClick() }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Complete daily tasks to receive rich rewards.",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                        }

                        // Tab Bar
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 20.dp)
                                    .wrapContentWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedTab == "Daily Missions") Color.White else Color.Transparent,
                                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                        )
                                        .clickable { selectedTab = "Daily Missions" }
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Daily Missions",
                                        color = if (selectedTab == "Daily Missions") Color.Black else Color.Yellow,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedTab == "History") Color.White else Color.Transparent,
                                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                        )
                                        .clickable { selectedTab = "History" }
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "History",
                                        color = if (selectedTab == "History") Color.Black else Color.Yellow,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            HorizontalDivider(
                                thickness = 2.dp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 10.dp, end = 20.dp)
                            )
                        }
                    }
                }

                // Conditional content based on selected tab
                if (selectedTab == "Daily Missions") {
                    items(dailyMissions) { mission ->
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            DailyMissionCard(mission = mission)
                        }
                    }
                } else {
                    item {
                        HistorySTScreen()
                    }
                }
            }
        }

    }
}

@Composable
fun DailyMissionCard(mission: DailyMission) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main Card Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(25.dp))
                .border(2.dp, Color.Black, shape = RoundedCornerShape(25.dp))
                .background(Color.White)
                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                // Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp, top = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.casino),
                        contentDescription = "Reward Icon",
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = mission.title,
                        color = Color.LightGray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.padding(start = 20.dp))
                    Text(
                        text = "${mission.progress}/${mission.target}",
                        color = Color(0xFF0B63CE),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Reward Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Award Amount",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.casino),
                            contentDescription = "Reward Icon",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = mission.reward,
                            color = Color(0xFF0B63CE),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 30.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFBDBDBD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "To Completed",
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Top Row: "Daily Mission" tab + "Unfinished"
        Column(
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(topStart = 25.dp, bottomEnd = 25.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 0.dp, bottomEnd = 25.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F8188), Color(0xFF2F4C5E))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Daily Mission",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Unfinished",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 90.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 140.dp, end = 8.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )
        }
    }
}

