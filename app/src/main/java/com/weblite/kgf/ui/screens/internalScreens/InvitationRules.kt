package com.weblite.kgf.ui.screens.internalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R
import com.weblite.kgf.data.CustomFont.Companion.AptosFontFit
import com.weblite.kgf.data.CustomFont.Companion.AptosFontNormal
import com.weblite.kgf.data.CustomFont.Companion.aptosFontBold
import com.weblite.kgf.ui.components.AttractiveText
import java.nio.file.WatchEvent


@Composable
fun InvitationRules(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )
    // All Rules
    val rules1 = listOf(
        "There are 6 subordinate levels in inviting friends, if A invites B, then B is a level 1 subordinate of A. If B invites C, then C is a level 1 subordinate of B and also a level 2 subordinate of A. If C invites D, then D is a level 1 subordinate of C, at the same time a level 2 subordinate of B and also a level 3 subordinate of A.",
        "When inviting friends to register, you must send the invitation link provided or the invitation code manually so your friends become your level 1 subordinates.",
        "The invitee registers via the inviter’s invitation code and completes the deposit, shortly after that the commission will be received immediately.",
        "The calculation of yesterday’s commission starts every morning at 01:00. After the commission calculation is completed, the commission is rewarded to the wallet and can be viewed through the commission collection record.",
        "Commission rates vary depending on your agency level on that day.\nNumber of Teams: How many downline deposits you have to date.\nTeam Deposits: The total number of deposits made by your downline in one day.",
        )
    val rules2 = listOf(
        "The commission percentage depends on the membership level. The higher the membership level, the higher the bonus percentage. Different game types also have different payout percentages. The commission rate is specifically explained as follows: View rebate ratio.",
        "TOP20 commission rankings will be randomly awarded with a separate bonus.",
        "The final interpretation of this activity belongs to Welcome to KGF India Game."
    )

    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
           //.verticalScroll(scrollState)
    ) {
        item {
            // AppBar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth().height(50.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007BFF))
                }
                Text("Invitation Rules",
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            // Rules Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rules",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF000000)
                )

            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ){

                AttractiveText(
                    text = "[Promotion partner] program",
                    primaryColor = Color.White,
                    shadowColor = Color(0xFF606060),
                    fontSize = 16.sp,
                    strokeWidth = 1.5f
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "This activity is valid for a long time",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            rules1.forEachIndexed { index, text ->
                RuleCard(index + 1, text)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            RebateLevelTable()
            Spacer(modifier = Modifier.height(12.dp))

        }

        item {
            rules2.forEachIndexed { index, text ->
                RuleCard(index + 6, text)
                Spacer(modifier = Modifier.height(12.dp))
            }

        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }



    }
}

@Composable
fun RuleCard(index: Int, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xff00EBEF),
                spotColor = Color(0xff00EBEF)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(2.dp, Color(0xFF007BFF), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "0$index",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFF4CAF50), RoundedCornerShape(54))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                color = Color.Black,
                fontFamily = AptosFontNormal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}


@Composable
fun RebateLevelTable() {
    val tableData = listOf(
        TableRowData("L0", "0", "0K", "0K"),
        TableRowData("L1", "5", "500K", "100K"),
        TableRowData("L2", "10", "1,000K", "200K"),
        TableRowData("L3", "15", "2,500K", "500K"),
        TableRowData("L4", "20", "3,500K", "700K"),
        TableRowData("L5", "25", "5,000K", "1,000K"),
        TableRowData("L6", "30", "10,000K", "2,000K"),
        TableRowData("L7", "100", "100,000K", "20,000K"),
        TableRowData("L8", "500", "500,000K", "100,000K"),
        TableRowData("L9", "1000", "1,000,000K", "200,000K"),
        TableRowData("L10", "5000", "1,500,000K", "300,000K"),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(2.dp, Color(0xFF00A8FF), RoundedCornerShape(16.dp))
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF008000), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            TableHeaderCell("Rebate\nLevel",modifier = Modifier.weight(0.8f))
            TableHeaderCell("Team\nNumber", modifier = Modifier.weight(0.7f))
            TableHeaderCell("Team Betting", modifier = Modifier.weight(1f))
            TableHeaderCell("Team Deposit", modifier = Modifier.weight(1f))
        }


        Spacer(modifier = Modifier.height(4.dp))

        // Data rows
        tableData.forEach { row ->
            TableDataRow(row)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color.White,
        lineHeight = 14.sp,
        modifier = modifier
            .padding(horizontal = 1.dp),
        maxLines = 2
    )
}

@Composable
fun TableDataRow(row: TableRowData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8F8F8))
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rebate Level with crown and Lx badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.2f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fire), // Replace with your crown drawable
                contentDescription = "Crown",
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFC107), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = row.level,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        TableCell(row.teamNumber, modifier = Modifier.weight(0.68f))
        TableCell(row.teamBetting, modifier = Modifier.weight(1f))
        TableCell(row.teamDeposit, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = modifier
            .padding(horizontal = 4.dp)
    )
}

data class TableRowData(
    val level: String,
    val teamNumber: String,
    val teamBetting: String,
    val teamDeposit: String
)



@Preview
@Composable
fun InvRulesView(){
    //InvitationRules()
    RebateLevelTable()
}