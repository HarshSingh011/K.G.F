package com.weblite.kgf.utils

object DiceUtils {
    /**
     * Given a bidNum (3..18), returns a list of three dice values (each 1..6) that sum to bidNum.
     * The distribution is as even as possible, e.g. 4 -> [2,1,1], 5 -> [3,1,1], 6 -> [2,2,2], etc.
     */
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

    /**
     * Given a dice value (1..6), returns the drawable resource id for that dice face.
     * Usage: getDiceDrawable(3) -> R.drawable.dice_3
     */
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
