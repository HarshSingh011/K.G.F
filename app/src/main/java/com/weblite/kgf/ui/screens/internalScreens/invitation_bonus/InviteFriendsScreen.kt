package com.weblite.kgf.ui.screens.internalScreens.invitation_bonus

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.Api.MainViewModel
import com.weblite.kgf.Api.SharedPrefManager
import com.weblite.kgf.R
import com.weblite.kgf.data.models.auth.ReferralBonus
import com.weblite.kgf.data.models.auth.ReferralBonusResponse

//data class BonusLevel(
//    val level: Int,
//    val reward: String,
//    val requiredInvites: Int,
//    val rechargePerPerson: String,
//    val currentInvites: Int,
//    val currentDeposits: Int,
//    val isCompleted: Boolean
//)

@Composable
fun InviteFriendsScreen(
    onBackClick: () -> Unit = {},
    onNavigationClick: (String) -> Unit = {},
    onInvitationRulesClick: () -> Unit = {},
    onInvitationRecordClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
//    val bonusLevels = remember {
//        listOf(
//            BonusLevel(1, "₹55.00", 1, "₹500", 1, 1, true),
//            BonusLevel(2, "₹155.00", 3, "₹500", 3, 3, true),
//            BonusLevel(3, "₹555.00", 10, "₹500", 0, 0, false),
//            BonusLevel(4, "₹1,555.00", 30, "₹500", 0, 0, false),
//            BonusLevel(5, "₹2,555.00", 50, "₹500", 0, 0, false)
//        )
//    }
    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
    }

    val bonusLevels by viewModel.invitationResponse.collectAsState()

    LaunchedEffect(Unit) {
        val userId = SharedPrefManager.getString("user_id", "0")
        viewModel.invitationData(userId!!)
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
                        // Invite friends Header
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
                                        text = "Invite friends and deposit",
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
                                    text = "Both parties can receive rewards.",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Invite friends to register and recharge to receive rewards.",
                                    color = Color.White,
                                    fontSize = 14.sp,
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
                                    // Invitations reward rules - HIGHLIGHTED when active
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable { onInvitationRulesClick() }
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
                                            color = Color(0xFF0B63CE), // Blue text to show it's selected
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Invitations record
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onInvitationRecordClick() } // This will trigger navigation
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
                                            color = Color.Gray,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Bonus level cards
//                items(bonusLevels?.result?.levels!!) { bonus ->
//                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//                        BonusLevelCard(bonus = bonus, )
//                    }
//                }

                if (bonusLevels != null){
                    itemsIndexed(bonusLevels?.result?.levels!!) { index, bonus ->
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            BonusLevelCard(
                                bonus = bonus,
                                index = index, // pass index to your composable
                                bonusLevels
                            )
                        }
                    }
                }else{
                    Log.d("TAG_DATA", "InviteFriendsScreen: ${bonusLevels?.result?.levels}")
                }
            }
        }
    }
}

@Composable
fun BonusLevelCard(bonus: ReferralBonus.Level, index: Int, bonusLevels: ReferralBonusResponse?) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main Card Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(25.dp))
                .border(2.dp, Color(0xFF0B63CE), shape = RoundedCornerShape(25.dp))
                .background(Color.White)
                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                // Number of Invites Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), RectangleShape)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Number Of Invites",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = bonus.required.toString(),
                        color = Color(0xFF0B63CE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Recharge per Person Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), RectangleShape)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Recharge per Person",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = bonus.bonus,
                        color = Color(0xFF0B63CE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${bonusLevels?.result?.level_counts[index]}/${bonus.required}",
                            color = Color(0xFF0B63CE),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Number of Invites",
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color.Gray)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${bonusLevels?.result?.level_counts[index]}/${bonus.required}",
                            color = Color(0xFF0B63CE),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Number of Deposite",
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (bonus.required == bonusLevels?.result?.level_counts[index]) Color(
                                0xFF4CAF50
                            ) else Color(0xFFBDBDBD)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (bonus.required == bonusLevels?.result?.level_counts[index]) "Completed" else "Unfinished",
                        color = if (bonus.required == bonusLevels?.result?.level_counts[index]) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Top Row: Bonus level tab + Reward amount with light gray lines
        Column(
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bonus level tab
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF0B63CE),
                            shape = RoundedCornerShape(topStart = 25.dp, bottomEnd = 25.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 25.dp,
                                topEnd = 0.dp,
                                bottomEnd = 25.dp
                            )
                        )
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F8188), Color(0xFF2F4C5E))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    var ind = index + 1
                    Text(
                        text = "Bonus $ind",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Reward amount
                Text(
                    text = bonus.bonus,
                    color = Color(0xFF0B63CE),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            // Light gray line in front of bonus box
            Box(
                modifier = Modifier
                    .padding(start = 120.dp, end = 8.dp, top = 2.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2196F3) else Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) Color(0xFF2196F3) else Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
