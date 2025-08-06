package com.weblite.kgf.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatUtil {
    // Converts yyyy-MM-dd to dd-MMM-yyyy (e.g., 2025-05-02 -> 02-May-2025)
    fun formatApiDateToDisplay(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
            val parsed = inputFormat.parse(date)
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            date
        }
    }

    // Converts dd-MMM-yyyy to yyyy-MM-dd (e.g., 02-May-2025 -> 2025-05-02)
    fun formatDisplayDateToApi(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsed = inputFormat.parse(date)
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            date
        }
    }
}
