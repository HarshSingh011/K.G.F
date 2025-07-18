package com.weblite.kgf.ui.screens.auth
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SendOtpResponse
import com.weblite.kgf.data.CustomFont.Companion.AptosFontNormal
import com.weblite.kgf.ui.components.AttractiveText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
    onSuccessReset: () -> Unit = {}
) {
    val yellow = Color(0xFFff5c00)

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    var isOtpVerified by remember { mutableStateOf(false) }
    var isOtpSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(60) }
    var timerRunning by remember { mutableStateOf(false) }
    var actualOtp by remember { mutableStateOf<String?>(null) }



    val sendOtpState = viewModel.sendOtpState.value
    val updatePasswordState = viewModel.updatePasswordState.value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val passwordFieldsEnabled = isOtpVerified
    val allFieldsValid = isOtpVerified && phone.isNotBlank() &&
            otp.isNotBlank() &&
            newPassword.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            newPassword == confirmPassword &&
            agreedToTerms

    fun startCountdown() {
        timerRunning = true
        coroutineScope.launch {
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            timerRunning = false
        }
    }
    LaunchedEffect(sendOtpState) {
        when (sendOtpState) {
            is Resource.Success -> {
                actualOtp = sendOtpState.data?.otp?.toString()
                isOtpVerified = false
                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                isOtpSent = true
                countdown = 60
                startCountdown()
                viewModel.resetSendOtpState()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Failed to send OTP: ${sendOtpState.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetSendOtpState()
            }
            else -> {}
        }
    }



    LaunchedEffect(updatePasswordState) {
        when (updatePasswordState) {
            is Resource.Success -> {
                Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdatePasswordState()
                onSuccessReset()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Update failed: ${updatePasswordState.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdatePasswordState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("KGF India Game", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = yellow)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(yellow)
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Text("Forgot your password", fontWeight = FontWeight.SemiBold, fontSize = 21.sp, color = Color.White)
                Spacer(modifier = Modifier.height(5.dp))
                Text("Please change your password through your mobile phone number.", fontSize = 17.sp, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                CustomInputField2(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    placeholder = "Enter your phone number",
                    leadingIcon = Icons.Default.Phone,
                    yellow = yellow
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (phone.trim().isNotBlank()) {
                            viewModel.sendOtp(phone.trim())
                        } else {
                            Toast.makeText(context, "Enter phone number", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.Start),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellow.copy(alpha = if (timerRunning) 0.7f else 1f),
                        contentColor = Color.White
                    ),
                    // Don't disable the button, just fade it visually
                    enabled = true,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when {
                            timerRunning -> "Resend in ${countdown}s"
                            isOtpSent -> "Resend OTP"
                            else -> "Send OTP"
                        },
                        fontSize = 14.sp
                    )
                }


                if (sendOtpState is Resource.Loading) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(14.dp))

                CustomInputField2(
                    value = otp,
                    onValueChange = { otp = it },
                    label = "Verification Code",
                    placeholder = "Enter OTP",
                    leadingIcon = Icons.Default.Lock,
                    yellow = yellow
                )
                val otpFromServer = actualOtp
                if (isOtpSent && otp.isNotBlank() && !isOtpVerified) {
                    Button(
                        onClick = {
                            if (otpFromServer == otp.trim()) {
                                isOtpVerified = true
                                Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                            } else {
                                isOtpVerified = false
                                Toast.makeText(context, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = yellow)
                    ) {
                        Text("Verify OTP", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField2(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    placeholder = "Enter new password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    yellow = yellow,
                    enabled = passwordFieldsEnabled
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField2(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Re-enter new password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    yellow = yellow,
                    enabled = passwordFieldsEnabled
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = yellow)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("I agree to the Terms & Conditions", fontSize = 14.sp, color = yellow)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.updatePassword(phone.trim(), newPassword.trim())
                    },
                    enabled = allFieldsValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellow,
                        contentColor = Color.White,
                        disabledContainerColor = yellow.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Submit", lineHeight = 16.sp, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}


////////
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit,
    yellow: Color = Color(0xFF007BFF)
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val allFieldsValid = newPassword.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            newPassword == confirmPassword

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 2.dp,
            color = Color.White,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp) // Reduced from 16dp
                    .background(Color.White)
            ) {
                // Compact Header Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Change Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Reduced from 12dp

                CustomInputField2(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    placeholder = "Enter new password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    yellow = yellow
                )

                Spacer(modifier = Modifier.height(8.dp)) // Reduced from 12dp

                CustomInputField2(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm New Password",
                    placeholder = "Re-enter new password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    yellow = yellow
                )

                Spacer(modifier = Modifier.height(16.dp)) // Reduced from 20dp

                Button(
                    onClick = { onSubmit(newPassword, confirmPassword) },
                    enabled = allFieldsValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allFieldsValid) yellow else yellow.copy(alpha = 0.6f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.7f),
                        disabledContainerColor = yellow.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(30.dp), // Fully rounded button
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField2(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    yellow: Color,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Animated label that moves up when focused
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isFocused) listOf(
                            yellow.copy(alpha = 0.1f),
                            yellow.copy(alpha = 0.05f)
                        ) else listOf(
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (errorMessage != null) Color.Red else if (isFocused) yellow else yellow.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                leadingIcon = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = yellow,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    if (isPassword) {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = yellow.copy(alpha = 0.8f)
                            )
                        }
                    } else if (value.isNotEmpty()) {
                        IconButton(
                            onClick = { onValueChange("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear input",
                                tint = yellow.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Color.Gray.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                visualTransformation = when {
                    isPassword && !passwordVisible -> PasswordVisualTransformation()
                    else -> VisualTransformation.None
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black.copy(alpha = 0.8f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = yellow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 0.1.sp
                ),
                singleLine = true,
                interactionSource = interactionSource,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Error message with animation
        AnimatedVisibility(
            visible = !errorMessage.isNullOrEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage ?: "",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}