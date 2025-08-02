package com.weblite.kgf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PaginationNavButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isPrimary: Boolean
) {
    val backgroundColor = if (isPrimary) Color(0xFFFF8000) else Color(0xFFE0E0E0)
    val textColor = if (isPrimary) Color.White else Color(0xFF333333)

    Box(
        modifier = Modifier
            .height(40.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Generic pagination state and logic for paginated lists.
 */
data class PaginationState(
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val itemsPerPage: Int = 8
) {
    fun <T> pagedItems(items: List<T>): List<T> {
        val start = (currentPage - 1) * itemsPerPage
        val end = (start + itemsPerPage).coerceAtMost(items.size)
        return if (items.isNotEmpty() && start < items.size) items.subList(start, end) else emptyList()
    }
}
