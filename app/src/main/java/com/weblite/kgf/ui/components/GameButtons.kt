package com.example.weblite.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.weblite.kgf.ui.screens.game.getNumberDrawable
import kotlinx.coroutines.delay



@Composable
fun ColorButton(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(
        topStart = 25.dp,
        bottomStart = 0.dp,
        topEnd = 0.dp,
        bottomEnd = 25.dp
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp)
            .defaultMinSize(minWidth = 80.dp)
            .border(
                width = 2.dp,
                color = Color.White,
                shape = shape
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color.copy(alpha = 0.9f) else color
        ),
        shape = shape,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// THIS IS THE KEY COMPONENT - ImageNumberButton with click handler
@Composable
fun ImageNumberButton(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit  // This onClick will trigger the popup
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() }  // THIS TRIGGERS THE POPUP
            .then(
                if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = getNumberDrawable(number)),
            contentDescription = "Number $number",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MultiplierButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFF6B35) else Color.White,
            contentColor = if (isSelected) Color.White else Color(0xFFFF6B35)
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFFF6B35)) else null,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BigSmallButton(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color.copy(alpha = 0.8f) else color
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HistoryTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFF6B35) else Color.White,
            contentColor = if (isSelected) Color.White else Color(0xFFFF6B35)
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFFF6B35)) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun SuccessMessage(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(2000)
            onDismiss()
        }
    }
}