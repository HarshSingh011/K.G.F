package com.weblite.kgf.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.weblite.kgf.R

class Model {
}
data class Items(val imageResId: Int, val name: String)
data class Winner(
    val avatar: Int,      // e.g. R.drawable.avatar1
    val userId: String,   // e.g. "MEM***AWG"
    val amount: String,    // e.g. "702.20"
    val winIcon: Int,
    val isTop: Boolean
)

data class EarningUser(
    val position: String,
    val name: String,
    val amount: String,
    val crownColor: Color,
    val backgroundColor: Color,
    val isTop: Boolean
)
val dummyUsers = listOf(
    User(phone = "9639919215", password = "123456"),
    User(phone = "8888888888", password = "password"),
    User(phone = "9988776655", password = "kgfrocks")
)
data class User(val phone: String, val password: String)
data class GiftCardData(
    val assetPath: Int,
    val description: String,
    val altText: String
)

class CustomFont{
    companion object{
        val aptosFontBold = FontFamily(Font(R.font.aptos_font_family))
        val AptosFontNormal = FontFamily(
            Font(R.font.aptos_font_family_regular, weight = FontWeight.Medium)
        )
        val AptosFontFit = FontFamily(
            Font(R.font.aptos_font_family_normal, weight = FontWeight.Normal)
        )

    }
}

