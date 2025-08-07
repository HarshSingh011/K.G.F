package com.weblite.kgf.ui.screens.dashboard

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.navigation.COMMISS_DETAILS
import com.weblite.kgf.navigation.DIRECT_TEAM
import com.weblite.kgf.navigation.INVITAION_RULES
import com.weblite.kgf.ui.components.AttractiveText

import com.weblite.kgf.navigation.PromotionNavHost
import com.weblite.kgf.navigation.REBATE_RATIO
import com.weblite.kgf.ui.viewmodel.PromotionViewModel

// Navigation constants moved to PromotionNavHost.kt
@Composable
fun PromotionScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    PromotionNavHost(
        navController = navController,
        onShowTopBar = onShowTopBar,
        onShowBottomBar = onShowBottomBar
    )
}

@Composable
fun PromotionMainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    promotionViewModel: PromotionViewModel = hiltViewModel()
){
    val scrollState = rememberScrollState()
    val promotionViewModel = promotionViewModel
    val promotionViewState = promotionViewModel.promotionViewState.value
    var userId by remember { mutableStateOf("") }
    var rowCount by remember { mutableStateOf(0) }
    var referralCount by remember { mutableStateOf(0) }
    var directDepositNumber by remember { mutableStateOf(0) }
    var directDepositAmount by remember { mutableStateOf(0) }
    var directFirstDepositPeople by remember { mutableStateOf(0) }
    var indirectDepositNumber by remember { mutableStateOf(0) }
    var indirectDepositAmount by remember { mutableStateOf(0) }
    var indirectFirstDepositCount by remember { mutableStateOf(0) }
    var myTodayCommission by remember { mutableStateOf("0") }
    var totalCommission by remember { mutableStateOf("0.0000") }

    // Fetch promotion view data on launch
    LaunchedEffect(Unit) {
        val uid = SharedPrefManager.getString("user_id", "0") ?: "0"
        userId = uid
        promotionViewModel.fetchPromotionView(uid)
    }

    // Update state when API response changes
    LaunchedEffect(promotionViewState) {
        if (promotionViewState is Resource.Success) {
            promotionViewState.data?.result?.let { result ->
                userId = result.userId.trim()
                rowCount = result.rowCount
                referralCount = result.referralCount
                directDepositNumber = result.directDepositCount
                directDepositAmount = result.directDepositAmount
                directFirstDepositPeople = result.directFirstDepositCount
                indirectDepositNumber = result.indirectDepositCount
                indirectDepositAmount = result.indirectDepositAmount
                indirectFirstDepositCount = result.indirectFirstDepositCount
                myTodayCommission = result.myTodayCommission ?: "0"
                totalCommission = result.totalCommission
            }
        }
    }

    val invitationLnk = "https://newkgfindia.com/login/ragister?referid=$userId"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF002051))
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        AgencyCommissionUI(
            navController = navController,
            directTeamRegistered = rowCount,
            indirectTeamRegistered = referralCount,
            directDepositNumber = directDepositNumber,
            directDepositAmount = directDepositAmount,
            directFirstDepositPeople = directFirstDepositPeople,
            indirectDepositNumber = indirectDepositNumber,
            indirectDepositAmount = indirectDepositAmount,
            indirectFirstDepositPeople = indirectFirstDepositCount,
            myTodayCommission = myTodayCommission,
            totalCommission = totalCommission,
            invitationLnk = invitationLnk
        )
        InvitationDashboardUI(userId = userId, navController = navController)
    }
}


@Composable
fun AgencyCommissionUI(
    navController: NavController,
    directTeamRegistered: Int,
    indirectTeamRegistered: Int,
    directDepositNumber: Int,
    directDepositAmount: Int,
    directFirstDepositPeople: Int,
    indirectDepositNumber: Int,
    indirectDepositAmount: Int,
    indirectFirstDepositPeople: Int,
    myTodayCommission: String,
    totalCommission: String,
    invitationLnk: String,
    onCopyClick: () -> Unit = {}
) {
    val darkBlue = Color(0xFF061B43)
    val lightBlue = Color(0xFF028bed)
    val neonYellow = Color(0xFFE8FF00)
    val mediumBlue = Color(0xFFffffff)
    val whiteColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Agency Header
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color(0xFF00EBEF),
                        spotColor = Color(0xFF00EBEF)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .background(color = whiteColor, shape = RoundedCornerShape(30.dp))
                    .padding(horizontal = 36.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                AttractiveText(
                    text = "Agency",
                    primaryColor = Color.Black,
                    shadowColor = Color(0xFF606060),
                    fontSize = 24.sp,
                    strokeWidth = 1.5f
                )
            }

            // Total Commission
            AttractiveText(
                text = myTodayCommission,
                primaryColor = Color.White,
                shadowColor = Color(0xFF606060),
                fontSize = 50.sp,
                strokeWidth = 1.5f
            )

            // Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(6.dp),
                        ambientColor = Color(0xFF00EBEF),
                        spotColor = Color(0xFF00EBEF)
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(neonYellow, Color(0xFFB0B800))
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Today's total commission",
                    color = darkBlue,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AttractiveText(
                text = "Your commission balance is added wallet next day",
                primaryColor = Color.White,
                shadowColor = Color(0xFF606060),
                fontSize = 11.sp,
                strokeWidth = 1.5f,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9f)
            )

            // Team Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = whiteColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(IntrinsicSize.Min)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color(0xff00b8bb),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xff143250), Color(0xff00EBEF))
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Direct Team
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(" Direct Team ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = lightBlue,
                            modifier = Modifier.background(darkBlue.copy(0.1f)))
                        Divider(color = mediumBlue, thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.6f))

                        Text("$directTeamRegistered", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                        Text("number of register", fontSize = 12.sp, color = Color.Black.copy(0.9f))

                        Text(
                            directDepositNumber.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (directDepositNumber > 0) Color.Green else Color(0xFF00AA00)
                        )
                        Text("Deposit number", fontSize = 12.sp, color = Color.Black.copy(0.9f))

                        Text(
                            directDepositAmount.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (directDepositAmount > 0) Color.Green else Color(0xFF00AA00)
                        )
                        Text("Deposit amount", fontSize = 12.sp, color = Color.Black.copy(0.9f))

                        Text(
                            directFirstDepositPeople.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = darkBlue
                        )
                        Text(
                            text = "Number of people making\nfirst deposit",
                            fontSize = 12.sp,
                            color = Color.Black.copy(0.9f),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Indirect Team
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(" Indirect Team ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = lightBlue,
                            modifier = Modifier.background(darkBlue.copy(0.1f)))
                        Divider(color = mediumBlue, thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.6f))

                        Text("$indirectTeamRegistered", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                        Text("number of register", fontSize = 12.sp, color = Color.Black.copy(0.9f))

                        Text(
                            indirectDepositNumber.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (indirectDepositNumber > 0) Color.Green else Color(0xFF00AA00)
                        )
                        Text("Deposit number", fontSize = 12.sp, color = Color.Black.copy(0.9f))

                        Text(
                            indirectDepositAmount.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (indirectDepositAmount > 0) Color.Green else Color(0xFF00AA00)
                        )
                        Text("Deposit amount", fontSize = 12.sp, color = Color.Black.copy(0.9f))

                        Text(
                            indirectFirstDepositPeople.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = darkBlue
                        )
                        Text(
                            text = "Number of people making\nfirst deposit",
                            fontSize = 12.sp,
                            color = Color.Black.copy(0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Invitation Link & Copy Button
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF054fe3),
                        spotColor = Color(0xFF00b8bb)
                    )
                    .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xff043fb5), Color(0xff054fe3), Color(0xff043fb5))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 20.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    val clipboardManager = LocalClipboardManager.current
                    Text(
                        text = invitationLnk,
                        fontSize = 14.sp,
                        color = whiteColor,
                        modifier = Modifier.clickable {},
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(invitationLnk))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = neonYellow,
                            contentColor = darkBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.77f)
                            .height(38.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy invitation link")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "COPY INVITATION LINK", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
//////////////////


@Composable
fun InvitationDashboardUI(
    userId: String ="",
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val clipboardManager = LocalClipboardManager.current
        InfoCard(
            icon = Icons.Outlined.MailOutline,
            iconDescription = "Invitation code icon",
            title = "Invitation code: ",
            trailingContent = {
                Text(
                    text = userId,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(userId))
                        }
                )
            },
            backgroundColor = Color(0xFF2874A6)
        )

        ClickableCard(
            icon = Icons.Default.Group,
            iconDescription = "Direct Team Data icon",
            title = "Direct Team Data",
            onClick = {
                navController.navigate(DIRECT_TEAM)
            }
        )

        ClickableCard(
            icon = Icons.Default.Receipt,
            iconDescription = "Commission Details icon",
            title = "Commission Details *",
            onClick = {
                navController.navigate(COMMISS_DETAILS)
            }
        )

        ClickableCard(
            icon = Icons.Default.Info,
            iconDescription = "Invitation Rules icon",
            title = "Invitation Rules",
            onClick = {
                navController.navigate(INVITAION_RULES)
            }
        )

        ClickableCard(
            icon = Icons.Default.Money,
            iconDescription = "Rebate Ratio icon",
            title = "Rebate Ratio",
            onClick = {
                navController.navigate(REBATE_RATIO)
            }
        )

        PromotionDataCard(
            icon = Icons.Default.Percent,
            iconDescription = "Promotion data icon",
        )
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    iconDescription: String,
    title: String,
    trailingContent: @Composable () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF00b8bb),
                spotColor = Color(0xFF00b8bb)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription,
                    tint = Color(0xFFFF6500),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            trailingContent()
        }
    }
}

@Composable
fun ClickableCard(
    icon: ImageVector,
    iconDescription: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF00b8bb),
                spotColor = Color(0xFF00b8bb)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription,
                    tint = Color(0xFFFF6500),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Forward arrow",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}


@Composable
fun PromotionDataCard(
    icon: ImageVector,
    iconDescription: String,
    modifier: Modifier = Modifier,
    viewModel: PromotionViewModel = hiltViewModel()
) {
    val promotionViewState = viewModel.promotionViewState.value
    var title by remember { mutableStateOf("Promotion Data") }
    var weekValue by remember { mutableStateOf(0) } // If you have week data, map it here
    var totalDirectTeamValue by remember { mutableStateOf(0) }
    var totalIndirectTeamValue by remember { mutableStateOf(0) }
    var totalCommission by remember { mutableStateOf("0.0000") }

    LaunchedEffect(Unit) {
        val userId = com.weblite.kgf.Api2.SharedPrefManager.getString("user_id", "0") ?: "0"
        viewModel.fetchPromotionView(userId)
    }
    LaunchedEffect(promotionViewState) {
        if (promotionViewState is com.weblite.kgf.Api2.Resource.Success) {
            promotionViewState.data?.result?.let { result ->
                totalCommission = result.totalCommission
                totalDirectTeamValue = result.rowCount
                totalIndirectTeamValue = result.referralCount
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF00b8bb),
                spotColor = Color(0xFF00b8bb)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription,
                    tint = Color(0xFFFF6500),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PromotionDataColumn(value = weekValue.toString(), label = "This Week")
                PromotionDataColumn(value = totalCommission, label = "Total Commission")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PromotionDataColumn(value = totalDirectTeamValue.toString(), label = "Total Direct Team")
                PromotionDataColumn(value = totalIndirectTeamValue.toString(), label = "Total Indirect Team")
            }
        }
    }
}
@Composable
fun PromotionDataColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PromoPreview() {
    val localNavController = rememberNavController()
    //AgencyCommissionUI()
    InvitationDashboardUI(navController = localNavController)
}

