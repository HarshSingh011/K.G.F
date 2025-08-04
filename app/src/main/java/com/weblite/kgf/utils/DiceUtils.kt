package com.weblite.kgf.utils

object DiceUtils {
    fun getDiceValues(bidNum: Int): List<Int> {
        if (bidNum < 3 || bidNum > 18) return listOf(1, 1, 1)
        val base = bidNum / 3
        val rem = bidNum % 3
        return when (rem) {
            0 -> listOf(base, base, base)
            1 -> listOf(base + 1, base, base)
            2 -> listOf(base + 1, base + 1, base)
            else -> listOf(1, 1, 1) // Should never happen
        }.sortedDescending()
    }

    fun getDiceDrawable(diceValue: Int): Int {
        return when (diceValue) {
            1 -> com.weblite.kgf.R.drawable.dice_1
            2 -> com.weblite.kgf.R.drawable.dice_2
            3 -> com.weblite.kgf.R.drawable.dice_3
            4 -> com.weblite.kgf.R.drawable.dice_4
            5 -> com.weblite.kgf.R.drawable.dice_5
            6 -> com.weblite.kgf.R.drawable.dice_6
            else -> com.weblite.kgf.R.drawable.dice_1
        }
    }
}
