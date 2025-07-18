package com.example.weblite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weblite.kgf.R
import androidx.compose.ui.unit.sp

data class InvitationRecord(
    val srNo: Int,
    val levelRequired: Int,
    val achievementBonus: String,
    val numberOfInvites: Int,
    val isCompleted: Boolean
)

@Composable
fun InvitationRecordsScreen(
    onBackClick: () -> Unit = {},
    onNavigationClick: (String) -> Unit = {},
    onInvitationRulesClick: () -> Unit = {},
    onInvitationRecordClick: () -> Unit = {},
    onViewUserIdsClick: (Int) -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val invitationRecords = remember {
        listOf(
            InvitationRecord(1, 1, "₹55.00", 1, true),
            InvitationRecord(2, 3, "₹155.00", 3, true),
            InvitationRecord(3, 10, "₹555.00", 0, false),
            InvitationRecord(4, 30, "₹1,555.00", 0, false),
            InvitationRecord(5, 50, "₹2,555.00", 0, false),
            InvitationRecord(6, 70, "₹3,555.00", 0, false),
            InvitationRecord(7, 100, "₹5,555.00", 0, false),
            InvitationRecord(8, 200, "₹10,955.00", 0, false),
            InvitationRecord(9, 500, "₹25,555.00", 0, false)
        )
    }
    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
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
                    .background(Color(0xFF05133F)),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header section
                item {
                    Column {
                        // Invitation Records Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0B1331))
//                                .padding(16.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Invitation Records",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.leftarrow),
                                        contentDescription = "Back",
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .size(24.dp)
                                            .clickable { onBackClick() },
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Invite friends to register and recharge to receive rewards.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "activity date",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = "2000-01-01 - 2099-01-01",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Card with two orange icon sections
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(330.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .border(2.dp, Color(0xFF0B63CE), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Invitations reward rules
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
//                                            .weight(1f)
                                            .clickable { onInvitationRulesClick() } // This will navigate back to InviteFriendsScreen
                                            .padding(6.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.checklist),
                                            contentDescription = "Reward Icon",
                                            modifier = Modifier.size(40.dp)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Invitations reward rules",
                                            color = Color.Gray,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Invitations record - HIGHLIGHTED when active
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onInvitationRecordClick() }
                                            .padding(6.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.book),
                                            contentDescription = "Reward Icon",
                                            modifier = Modifier.size(40.dp)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Invitations record",
                                            color = Color(0xFF0B63CE), // Blue text to show it's selected
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // "Invitations record" subtitle
                        Text(
                            text = "Invitations record",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Horizontally Scrollable Table (as a single unit)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Column {
                                    // Table Header
                                    Row(
                                        modifier = Modifier
                                            .background(Color.White, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Sr. No Column
                                        Box(
                                            modifier = Modifier
                                                .width(80.dp)
                                                .padding(end = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Sr.\nNo",
                                                color = Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        // Level Required Column
                                        Box(
                                            modifier = Modifier
                                                .width(100.dp)
                                                .padding(horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Level\n(Required)",
                                                color = Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        // Achievement Bonus Column
                                        Box(
                                            modifier = Modifier
                                                .width(120.dp)
                                                .padding(horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Achievement\n(Bonus)",
                                                color = Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        // Number of Invites Column
                                        Box(
                                            modifier = Modifier
                                                .width(100.dp)
                                                .padding(horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Number of\nInvites",
                                                color = Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        // States Column
                                        Box(
                                            modifier = Modifier
                                                .width(120.dp)
                                                .padding(horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "States",
                                                color = Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        // Actions Column
                                        Box(
                                            modifier = Modifier
                                                .width(100.dp)
                                                .padding(start = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Actions",
                                                color = Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    // Horizontal divider after header
                                    Box(
                                        modifier = Modifier
                                            .width(628.dp) // Total width of all columns
                                            .height(1.dp)
                                            .background(Color(0xFFE0E0E0))
                                    )

                                    // Table Rows
                                    invitationRecords.forEach { record ->
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Sr. No
                                            Box(
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .padding(end = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = record.srNo.toString(),
                                                    color = Color.Black,
                                                    fontSize = 14.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            // Level Required
                                            Box(
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .padding(horizontal = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = record.levelRequired.toString(),
                                                    color = Color.Black,
                                                    fontSize = 14.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            // Achievement Bonus
                                            Box(
                                                modifier = Modifier
                                                    .width(120.dp)
                                                    .padding(horizontal = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = record.achievementBonus,
                                                    color = Color.Black,
                                                    fontSize = 14.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            // Number of Invites
                                            Box(
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .padding(horizontal = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = record.numberOfInvites.toString(),
                                                    color = Color.Black,
                                                    fontSize = 14.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            // Status Button
                                            Box(
                                                modifier = Modifier
                                                    .width(120.dp)
                                                    .padding(horizontal = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(100.dp)
                                                        .height(32.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(
                                                            if (record.isCompleted) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (record.isCompleted) "Completed" else "Unfinished",
                                                        color = if (record.isCompleted) Color.White else Color.Black,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            // Action Button
                                            Box(
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .padding(start = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(90.dp)
                                                        .height(32.dp)
                                                        .clip(RectangleShape)
                                                        .background(Color(0xFF00BCD4))
                                                        .clickable { onViewUserIdsClick(record.srNo) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "View user\nIds",
                                                        color = Color.Black,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        textAlign = TextAlign.Center,
                                                        lineHeight = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

