package com.weblite.kgf.ui.screens.payScreens.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R

@Composable
fun BankAccountApiCard(
    name: String,
    accountNumber: String,
    bankName: String,
    ifsc: String,
    onEditClick: () -> Unit,
    isSelected: Boolean = false,
    onSelect: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFF00EBEF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF001B40))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.wallet_filled_money_tool),
                    contentDescription = "Bank Icon",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "User Bank Account Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Divider(color = Color(0xFF00EBEF), thickness = 1.dp)
            if (onSelect != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSelect() },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00EBEF))
                    )
                    Text("Select this bank account", color = Color.White, fontSize = 15.sp)
                }
            }
            Text(text = "Name: $name", color = Color.White, fontSize = 15.sp)
            Text(text = "Bank: $bankName", color = Color.White, fontSize = 15.sp)
            Text(text = "A/C No: $accountNumber", color = Color.White, fontSize = 15.sp)
            Text(text = "IFSC: $ifsc", color = Color.White, fontSize = 15.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00EBEF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(80.dp)
                ) {
                    Text("Edit", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
