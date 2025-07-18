package com.weblite.kgf.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weblite.InvitationRecordsScreen
import com.example.weblite.InviteFriendsScreen
import com.example.weblite.SelfTradeRewardScreen
import com.weblite.kgf.R
import com.weblite.kgf.data.CustomFont.Companion.AptosFontNormal
import com.weblite.kgf.data.GiftCardData
import com.weblite.kgf.ui.components.HOMESCREEN
import com.weblite.kgf.utils.AssetIcon


@Composable
fun ActivityScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val localNavController = rememberNavController()
    val scrollState = rememberScrollState()
    val scrollStates = rememberLazyListState()
    var currentScreen by remember { mutableStateOf("invite_friends") }


    NavHost(localNavController, startDestination = "home") {
        composable("home") {
            //  Restore bars when returning to profile
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF002051))
                    .verticalScroll(scrollState)
                    .padding(bottom = 16.dp)
            ){
                ScreenControl(navController = localNavController)
            }

        }

        composable("self_trade_screen") {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Wingo 30 opened", Toast.LENGTH_SHORT).show()
            }
            SelfTradeRewardScreen()


        }

        composable("new_member_screen") {

        }

        composable("invitation_bonus_screen") {
            when (currentScreen) {
                "invite_friends" -> {
                    InviteFriendsScreen(
                        onBackClick = {
                            localNavController.popBackStack()
                        },
                        onNavigationClick = { destination ->
                            // Handle bottom nav (optional switch here)
                            println("Navigate to $destination")
                        },
                        onInvitationRulesClick = {
                            // Navigate or show rules
                            println("Show invitation rules")
                        },
                        onInvitationRecordClick = {
                            currentScreen = "invitation_records"
                        },
                        onShowTopBar = { onShowTopBar(it) },
                        onShowBottomBar = { onShowBottomBar(it) }
                    )
                }

                "invitation_records" -> {
                    InvitationRecordsScreen(
                        onBackClick = {
                            localNavController.popBackStack()
                        },
                        onNavigationClick = { destination ->
                            println("Navigate to $destination")
                        },
                        onInvitationRulesClick = {
                            currentScreen = "invite_friends"
                        },
                        onViewUserIdsClick = { srNo ->
                            println("View user IDs for record: $srNo")
                        },
                        onShowTopBar = { onShowTopBar(it) },
                        onShowBottomBar = { onShowBottomBar(it) }
                    )
                }
            }

        }
    }


}

@Composable
fun ScreenControl(navController: NavController){
    Column {
        ActivitySection(navController = navController)
        RewardsSection()
        GiftCardsResponsiveGrid()
    }
}


@Composable
fun ActivitySection(navController: NavController) {
    // Responsive Layout based on screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val isMobile = screenWidth in 320..767
    val isDesktop = screenWidth in 768..1439
    val isLargeDesktop = screenWidth >= 1440


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF001E3C), Color(0xFF00152F)) // dark blue gradient
                )
            )
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth(if (isMobile) 1f else 0.6f)
                    .padding(bottom = 30.dp).padding(2.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(31.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xff00ebef), Color(0xff00EBEF))
                        )
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.border(4.dp, Color(0xFF000000), RoundedCornerShape(32.dp))
                        .padding(2.dp)
                ){
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "Activity",
                            color = Color(0xFF000000),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Please remember to follow the event page\nWe will launch user feedback activities from time to time",
                            // fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp,
                            fontFamily = AptosFontNormal,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }


            val rowModifier = if (isMobile) Modifier.fillMaxWidth() else Modifier.fillMaxWidth(0.8f)

            Row(
                modifier = rowModifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconLabel(
                    icon = "icons/selftrade.png",
                    label = "Self trade \nrewards",
                    contentDescription = "Icon of a red gift box representing self trade rewards",
                    onClick = {
                        // Navigate or open SelfTradeScreen
                        navController.navigate("self_trade_screen")
                    }
                )
                IconLabel(
                    icon = "icons/newmenb.png",
                    label = "New member \ngift package",
                    contentDescription = "Icon of a blue gift box representing new member gift package",
                    onClick = {
                      /*  navController.navigate("new_member_screen")*/
                    }
                )
                IconLabel(
                    icon = "icons/invitation.png",
                    label = "Invitation \nbonus",
                    contentDescription = "Icon of an envelope representing invitation bonus",
                    onClick = {
                        navController.navigate("invitation_bonus_screen")
                    }
                )
            }

        }
    }
}

@Composable
fun IconLabel(
    icon: String,
    label: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 80.dp, max = 120.dp)
            .clickable { onClick() } // Make clickable
    ) {
        AssetIcon(
            assetPath = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(58.dp)
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )
    }
}

// section 2
@Composable
fun RewardsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x00001F3F))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RewardCard(
            imageRes = R.drawable.gifts,
            title = "Gifts",
            description = "Enter the redemption code to receive gift rewards. The rewards enter in",
            modifier = Modifier.weight(1f)
        )

        RewardCard(
            imageRes = R.drawable.attbonus,
            title = "Attendance bonus",
            description = "The more consecutive days you sign in, the higher the reward will be.",
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun RewardCard(
    modifier: Modifier = Modifier,
    imageRes: Int,
    title: String,
    description: String
) {
    Card(
        modifier = modifier
            .width(222.dp)
            .height(218.dp)
            .padding(4.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xff00EBEF),
                spotColor = Color(0xff00EBEF)
            )
            .border(
                width = 2.dp,
                color = Color(0xFF29659A),
                shape = RoundedCornerShape(13.dp)
            ),
        shape = RoundedCornerShape(13.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF29659A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(14.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 0.dp).padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.FillBounds
                )
//            AssetImage(
//                assetPath = imageRes,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .clip(RoundedCornerShape(12.dp)),
//                contentScale = ContentScale.Crop
//            )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    fontFamily = AptosFontNormal,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

    }
}

//section 3
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GiftCardsResponsiveGrid() {
    val cards = listOf(
        GiftCardData(R.drawable.gifts, "Member Winning Streak", "Gift box with stars"),
        GiftCardData(R.drawable.attbonus, "Aviator High Betting Award", "Wrapped gift boxes"),
        GiftCardData(R.drawable.gifts, "Lucky 10 Days of Interest", "Celebration gift"),
        GiftCardData(R.drawable.attbonus, "Mysterious Gift", "Mysterious ribbon gift")
    )

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val columns = when {
        screenWidthDp < 768 -> 1
        screenWidthDp < 1440 -> 2
        else -> 3
    }

    val cardWidth = when (columns) {
        1 -> 0.9f
        2 -> 0.45f
        else -> 0.3f
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                cards.forEach { card ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(cardWidth)
                    ) {
                        GiftCard(cardData = card)
                    }
                }
            }
        }
    }

}

@Composable
fun GiftCard(cardData: GiftCardData) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(13.dp),
                ambientColor = Color(0xff00EBEF),
                spotColor = Color(0xff00EBEF)
            )
            .border(
                width = 2.dp,
                color = Color(0xff29659a),
                shape = RoundedCornerShape(13.dp)
            ).padding(1.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 0.dp)
                .background(Color.White)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = 2.dp,
                    color = Color(0xff000000),
                    shape = RoundedCornerShape(14.dp)
                ),
        ) {
//            AssetImage(
//                assetPath = cardData.assetPath,
//                contentDescription = cardData.altText,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp)
//                    .clip(RoundedCornerShape(12.dp)),
//                contentScale = ContentScale.Crop
//            )
            Image(
                painter = painterResource(id = cardData.assetPath),
                contentDescription = null,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cardData.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Preview
@Composable
fun ActiPre(){
    //ActivitySection()
    RewardsSection()
    //GiftCardsResponsiveGrid()
}