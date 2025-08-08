package com.weblite.kgf.ui.screens.internalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.presentation.support.SupportViewModel
import com.weblite.kgf.domain.model.SupportMessage
import com.weblite.kgf.Api.SharedPrefManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.awaitCancellation

@Composable
fun Service24O7Screen(viewModel: SupportViewModel = hiltViewModel()) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val messages = viewModel.messages.collectAsState().value
    val scrollState = rememberScrollState()
    val userId = SharedPrefManager.getString("user_id", "") ?: ""

    // Start/stop polling for messages
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.startFetchingMessages(userId)
            try {
                awaitCancellation()
            } finally {
                viewModel.stopFetchingMessages()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            text = "Customer Support",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4B3F2F),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    messages.forEach { msg ->
                        ChatBubble(msg)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                BasicTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.Transparent),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (message.isNotBlank()) {
                                viewModel.sendMessage(userId, message)
                                message = ""
                                keyboardController?.hide()
                            }
                        }
                    ),
                    decorationBox = { innerTextField ->
                        if (message.isEmpty()) {
                            Text(
                                text = "Type your message...",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (message.isNotBlank()) {
                            viewModel.sendMessage(userId, message)
                            message = ""
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.4f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F6F))
                ) {
                    Text("Send", color = Color.White, fontSize = 18.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Bottom navigation placeholder
    }
}

// Use SupportMessage from domain.model

@Composable
fun ChatBubble(message: SupportMessage) {
    val bubbleColor = if (message.isUser) Color(0xFFE3F2FD) else Color(0xFFC8E6C9)
    val align = if (message.isUser) Alignment.End else Alignment.Start
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 220.dp)
                .background(bubbleColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = Color(0xFF00695C),
                fontSize = 16.sp
            )
        }
        Text(
            text = message.time,
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 2.dp, end = 8.dp, start = 8.dp)
        )
    }
}

