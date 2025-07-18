import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.weblite.kgf.R
import com.weblite.kgf.ui.components.AttractiveText

data class GiftHistoryItem(
    val couponCode: String,
    val amount: String,
    val date: String,
    val status: String
)

@Composable
fun GiftScreen(
    onBackClick: () -> Unit = {},
    historyList: List<GiftHistoryItem> = emptyList(), // Pass this dynamically
    onApplyCoupon: (String) -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    var couponCode by remember { mutableStateOf("") }
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFF001B40), Color(0xFF002D60), Color(0xFF001B40))
    )

    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
    }

    Column {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_gift_bann),
                contentDescription = null,
                contentScale = ContentScale.Crop, // or FillBounds, Fit, etc.
                modifier = Modifier.matchParentSize()
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Your content here
                Text(
                    "Coupon Award",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Enter your coupon code below to see if you win!",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            // Coupon input
            BasicTextField(
                value = couponCode,
                onValueChange = { couponCode = it },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, lineHeight = 16.sp, fontSize = 17.sp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(42.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(31.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                decorationBox = { innerTextField ->
                    if (couponCode.isEmpty()) {
                        Text(
                            text = "Enter coupon Code",
                            color = Color.DarkGray,
                            lineHeight = 16.sp,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Apply Now Button
            Button(
                onClick = { onApplyCoupon(couponCode) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Apply Now",
                    color = Color.White,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Redemption History
            AttractiveText(
                text = "Redemption History",
                primaryColor = Color.Black,
                shadowColor = Color(0xFFFFFFFF),
                fontSize = 24.sp,
                strokeWidth = 1.5f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Table Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .border(2.dp, Color(0xFF1E88E5), RoundedCornerShape(10.dp))
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                        TableCellHead("Coupon Code")
                        TableCellHead("Amount")
                        TableCellHead("Date")
                        TableCellHead("Status")
                    }
                    //.verticalScroll(rememberScrollState()),

                    Divider(color = Color.Black.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    ){
                        if (historyList.isEmpty()) {
                            Text(
                                "No redemption records found.",
                                color = Color.Black,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            historyList.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFF1E88E5), RoundedCornerShape(8.dp))
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Coupon Code
                                    TableCell(text = item.couponCode, weight = 1f)

                                    // Amount
                                    TableCell(text = "₹${item.amount}", weight = 1f)

                                    // Date
                                    TableCell(text = item.date, weight = 1f)

                                    // Status with pill shape
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF4CAF50))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.status,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }


                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float = 1f) {
    Text(
        text = text,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = Color.Black,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
            .weight(weight)
            .padding(4.dp),
        textAlign = TextAlign.Center
    )
}
@Composable
fun RowScope.TableCellHead(text: String, weight: Float = 1f) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .weight(weight)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        textAlign = TextAlign.Center
    )
}