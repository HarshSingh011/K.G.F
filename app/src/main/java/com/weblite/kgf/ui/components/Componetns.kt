package com.weblite.kgf.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.weblite.kgf.R
import com.weblite.kgf.data.CustomFont.Companion.AptosFontNormal
import com.weblite.kgf.data.CustomFont.Companion.aptosFontBold
import com.weblite.kgf.data.EarningUser
import com.weblite.kgf.data.Winner
import com.weblite.kgf.utils.AssetImage
import kotlinx.coroutines.delay

@Composable
fun AttractiveText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    primaryColor: Color = Color(0xFFFFD700), // Gold
    shadowColor: Color = Color(0x80FFA000), // Orange shadow
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 2f,
    shadowRadius: Float = 8f,
    letterSpacing: TextUnit = 1.5.sp,
    fontWeight: FontWeight = FontWeight.Black
) {
    Box(modifier = modifier) {
        // Shadow layer (behind everything)
        Text(
            text = text,
            style = TextStyle(
                color = shadowColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                shadow = Shadow(
                    color = shadowColor,
                    offset = Offset(4f, 4f),
                    blurRadius = shadowRadius
                ),
                letterSpacing = letterSpacing
            )
        )

        // Stroke layer
        Text(
            text = text,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = Color.Transparent,
                drawStyle = Stroke(width = strokeWidth),
                letterSpacing = letterSpacing
            )
        )

        // Main text (on top)
        Text(
            text = text,
            style = TextStyle(
                color = primaryColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                shadow = Shadow(
                    color = primaryColor.copy(alpha = 0.5f),
                    blurRadius = shadowRadius * 1.5f
                ),
                letterSpacing = letterSpacing
            )
        )
    }
}

@Composable
fun UserWalletCards(
    mobileNumber: String,
    userId: String,
    walletBalance: String,
    onUSRCardClick: () -> Unit = {},
    onWLTCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val cardHeight = 82.dp // Set a fixed height for both cards

        // User Info Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(cardHeight).padding(2.dp)
                .clickable { onUSRCardClick() }
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color(0xff00b8bb),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, Color(0xff27629b), RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xff182548), Color(0xff2293E3), Color(0xff02F9FE))
                    )
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Make default background transparent
        ) {
            Column(
                modifier = Modifier.padding(2.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .fillMaxSize()
            ){
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.userpro),
                        contentDescription = "User",
                        modifier = Modifier.size(58.dp)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(modifier = Modifier.align(alignment = Alignment.CenterVertically)) {

                        Text(
                            text = "$mobileNumber \n$userId",
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Wallet Balance Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onWLTCardClick() }
                .height(cardHeight).padding(2.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color(0xff00b8bb),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, Color(0xff27629b), RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xff182548), Color(0xff2293E3), Color(0xff02F9FE))
                    )
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Make default background transparent
        ) {
            Column(
                modifier = Modifier.padding(2.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .fillMaxSize()
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 1.dp),
                    horizontalArrangement = Arrangement.Center // Centers the cards horizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.wallet),
                        contentDescription = "wallet",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.width(1.dp))
                    Text(
                        text = walletBalance,
                        fontFamily = AptosFontNormal,
                        color = Color.White,
                        fontSize = 15.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                }
            }

        }
    }
}

// items cards and sections
@Composable
fun CardSection(
    items: List<String>,
    onItemClick: (String) -> Unit = {} // Default empty click handler
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.take(2).forEach { imagePath ->
                ItemCard(
                    item = imagePath,
                    modifier = Modifier,
                    onClick = { onItemClick(imagePath) } // Pass click to parent
                )
            }
        }
    }
}

@Composable
fun ItemCard(
    item: String,
    modifier: Modifier,
    onClick: () -> Unit = {} // Click handler parameter
) {
    Column(
        modifier = modifier
            .width(182.dp)
            .padding(4.dp)
            .clickable { onClick() } // Handle click on entire card
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xff143250),
                spotColor = Color(0xff00EBEF)
            )
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xff2d6aa6), RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(width = 180.dp, height = 168.dp)
                .padding(2.dp)
                .border(2.dp, Color(0xff043463), RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xff143250), Color(0xff00EBEF))
                    )
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            AssetImage(
                assetPath = item,
                contentDescription = "Special promotion",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
////     Winning information

@Composable
fun WinningInfoSection(winners: List<Winner>) {
    val listState = rememberLazyListState()
    var autoScrollEnabled by remember { mutableStateOf(true) }

    // Detect user scroll and disable auto-scroll while user is scrolling
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            autoScrollEnabled = false
        } else {
            delay(1) // Wait a bit before resuming
            autoScrollEnabled = true
        }
    }

    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            if (autoScrollEnabled) {
                listState.scrollBy(0.5f)
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                if (lastVisible != null && lastVisible.index == winners.lastIndex) {
                    listState.scrollToItem(0)
                }
            }
            delay(1)
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x000D1B3D))
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            AttractiveText(
                text = "Winning Information",
                primaryColor = Color.White,
                shadowColor = Color(0xFF606060),
                fontSize = 22.sp,
                strokeWidth = 1.5f
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color(0xff00b8bb),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(12.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xff2c6daf), Color(0xff27629b))
                    )
                )
                .padding(2.dp)
                .height(330.dp)
        ) {
            items(winners) { winner ->
                WinnerCard(winner)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}


@Composable
fun WinnerCard(winner: Winner) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Image(
                painter = painterResource(id = winner.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // User ID and amount info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = winner.userId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                )
            }

            // Icons
            Row {
                Image(
                    painter = painterResource(id = winner.winIcon),
                    contentDescription = "Reward",
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            ) {
                Text(
                    text = "Receive ₹${winner.amount} \nWinning Amount",
                    fontSize = 14.sp,
                    color = Color(0xff040039)
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewWinningList() {
//    val sampleWinners = listOf(
//        Winner(R.drawable.ic_launcher_background, "MEM***AWG", "702.20", R.drawable.ic_win1),
//        Winner(R.drawable.ic_launcher_background, "MEM***KEY", "122.28", R.drawable.ic_win2),
//        Winner(R.drawable.ic_launcher_background, "MEM***RXG", "122.90", R.drawable.ic_win2),
//        Winner(R.drawable.ic_launcher_background, "MEM***AWG", "903.44", R.drawable.ic_win1),
//        Winner(R.drawable.ic_launcher_background, "MEM***MKW", "620.50", R.drawable.ic_win2),
//        Winner(R.drawable.ic_launcher_background, "MEM***RAG", "620.50", R.drawable.ic_win2)
//    )
//
//    WinningInfoSection(sampleWinners)
//}
// earning chart

@Composable
fun EarningsChart(users: List<EarningUser>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x000D1230))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AttractiveText(
            text = "Today's Earning Chart",
            primaryColor = Color(0xFFfefefe),
            shadowColor = Color(0xFF606060),
            fontSize = 22.sp,
            strokeWidth = 1.5f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            users.forEach { user ->
                EarningCard(
                    position = user.position,
                    name = user.name,
                    amount = user.amount,
                    crownColor = user.crownColor,
                    backgroundColor = user.backgroundColor,
                    isTop = user.isTop
                )
            }
        }
    }
}


//  earning chart

@Composable
fun EarningCard(
    position: String,
    name: String,
    amount: String,
    crownColor: Color,
    backgroundColor: Color,
    isTop: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(if (isTop) 220.dp else 180.dp)
            .width(110.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Crown",
            tint = crownColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = position,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Avatar Placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Text(
            text = amount,
            fontSize = 11.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EarningsChartPreview() {
    MaterialTheme {
        EarningsChart(
            users = listOf(
                EarningUser(
                    position = "NO 2",
                    name = "MEM**AXY",
                    amount = "₹5,599,610.20",
                    crownColor = Color.Gray,
                    backgroundColor = Color(0xFFD1B2FF),
                    isTop = false
                ),
                EarningUser(
                    position = "NO 1",
                    name = "MEM**KLK",
                    amount = "₹10,703,323.50",
                    crownColor = Color(0xFFFFD700),
                    backgroundColor = Color(0xFFFFA726),
                    isTop = true
                ),
                EarningUser(
                    position = "NO 3",
                    name = "MEM**AQU",
                    amount = "₹4,435,323.27",
                    crownColor = Color(0xFFCD7F32),
                    backgroundColor = Color(0xFFFF8A80),
                    isTop = false
                )
            )
        )
    }
}


//
@Composable
fun EarningInfoSection(earnings: List<Winner>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x000D1B3D))
            .padding(8.dp)
    ) {
        // Header
        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            AttractiveText(
                text = "Earning Information",
                primaryColor = Color.White,
                shadowColor = Color(0xFF606060),
                fontSize = 22.sp,
                strokeWidth = 1.5f
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Earning cards list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(225.dp)
        ) {
            items(earnings) { winner ->
                WinEarningCard(winner)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun WinEarningCard(winner: Winner) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xff00b8bb),
                spotColor = Color(0xff00EBEF)
            )
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xff00EBEF), RoundedCornerShape(14.dp))
            .padding(horizontal = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Image(
                painter = painterResource(id = winner.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // User ID and amount info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = winner.userId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            // Icons
            Row {
                Image(
                    painter = painterResource(id = winner.winIcon),
                    contentDescription = "Reward",
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            ) {
                Text(
                    text = " ₹${winner.amount} ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xff000000)
                )
            }
        }
    }
}




//@Preview
//@Composable
//fun CompPre(){
//    UserWalletCards(
//        mobileNumber = "+1 234 567 890",
//        userId = "USR12345",
//        walletBalance = "Rs. 1,250.50"
//    )
//}