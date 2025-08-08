package com.weblite.kgf.utils

import androidx.compose.ui.graphics.Color
import com.weblite.kgf.R

data class K3Ball(val number: Int, val multiplier: String, val backgroundColor: Color)
data class K3BetOption(val name: String, val multiplier: String, val backgroundColor: Color)

object K3Utils {
    fun getK3BallDrawable(number: Int): Int {
        return when (number) {
            3 -> R.drawable.ball5
            4 -> R.drawable.ball6
            5 -> R.drawable.ball7
            6 -> R.drawable.ball8
            7 -> R.drawable.ball9
            8 -> R.drawable.ball10
            9 -> R.drawable.ball11
            10 -> R.drawable.ball12
            11 -> R.drawable.ball13
            12 -> R.drawable.ball14
            13 -> R.drawable.ball15
            14 -> R.drawable.ball16
            15 -> R.drawable.ball15
            16 -> R.drawable.ball16
            17 -> R.drawable.ball17
            18 -> R.drawable.ball18
            else -> R.drawable.ball5
        }
    }
}
