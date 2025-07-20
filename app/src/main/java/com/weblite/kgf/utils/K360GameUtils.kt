package com.weblite.kgf.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimestamp(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timestamp)
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        timestamp // Return original if parsing fails
    }
}
