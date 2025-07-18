package com.weblite.kgf.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.ui.components.AttractiveText

@Composable
fun AuthFlowScreen(
    startWithLogin: Boolean,
    onLoginSuccess: () -> Unit,
    onClose: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isLoginScreen by remember { mutableStateOf(startWithLogin) }
    var showForgotPassword by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }

    val loginState = viewModel.loginState.value
    val registerState = viewModel.signupState.value
    LaunchedEffect(Unit) {
        val userId = SharedPrefManager.getString("user_id", "0")
        viewModel.fetchPromotionView(userId.toString())
    }

    //  Handle login success

    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                val userName = loginState.data?.result?.user?.name ?: "User"
                val userid = loginState.data?.result?.user?.userId ?: "null"


                SharedPrefManager.setString("user_id",userid)

                //MainScreen()// @Composable invocations can only happen from the context of a @Composable function
                Toast.makeText(context, "Login Successful : ${userName}", Toast.LENGTH_SHORT).show()
                viewModel.resetLoginState()
                onLoginSuccess()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Login Failed: ${loginState.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    //  Handle registration success
    LaunchedEffect(registerState) {
        when (registerState) {
            is Resource.Success<*> -> {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                viewModel.resetSignupState()
                isLoginScreen = true
            }
            is Resource.Error<*> -> {
                Toast.makeText(context, "Registration Failed: ${registerState.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    when {

        showForgotPassword -> {
            ForgotPasswordScreen(
                onBack = { showForgotPassword = false },
//                onSubmit = { /* validate */ },
//                onSendOtp = { /* send OTP */ }
            )
        }

        showSupportDialog -> {
            // Add support dialog if needed
        }

        isLoginScreen -> {
            LoginScreen(
                phone = phone,
                onPhoneChange = { phone = it },
                password = password,
                onPasswordChange = { password = it },
                onLoginClick = {
                    Log.d("AuthFlowScreen", "Login clicked with phone=$phone, password=$password")
                    viewModel.login(phone.trim(), password.trim())
                },
                onRegisterClick = {
                    phone = ""
                    password = ""
                    viewModel.resetLoginState()
                    isLoginScreen = false
                },
                onForgotPasswordClick = { showForgotPassword = true },
                onSupportClick = { showSupportDialog = true },
                errorMessage = when (loginState) {
                    is Resource.Error -> loginState.message
                    else -> null
                }
            )
        }

        else -> {
            RegistrationScreen(
                phone = phone,
                onPhoneChange = { phone = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                referralCode = referralCode,
                onReferralChange = { referralCode = it },
                agreeToTerms = agreeToTerms,
                onAgreeChange = { agreeToTerms = it },
                onRegisterClick = {
                    if (password != confirmPassword) return@RegistrationScreen
                    viewModel.signup(
                        phone.trim(),
                        password.trim(),
                        confirmPassword.trim(),
                        ""
                    )
                },
                onLoginClick = {
                    viewModel.signupState
                    isLoginScreen = true
                },
                errorMessage = when {
                    // registerState is Resource.Error -> registerState.message
                    password != confirmPassword -> "Passwords do not match"
                    !agreeToTerms -> "Please agree to terms"
                    else -> null
                }
            )
        }
    }
}



@Composable
fun LoginScreen(
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,     // NEW
    onSupportClick: () -> Unit,            // NEW
    errorMessage: String?
) {
    val darkRed = Color(0xFF460014)
    val yellow = Color(0xFFFFC107)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkRed)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(45.dp))

        AttractiveText(
            text = "KGF",
            primaryColor = Color.Yellow,
            shadowColor = Color(0xFF606060),
            fontSize = 28.sp,
            strokeWidth = 1.5f
        )

        Spacer(modifier = Modifier.height(28.dp))

        Icon(
            imageVector = Icons.Default.PhoneAndroid,
            contentDescription = "Phone Icon",
            tint = yellow,
            modifier = Modifier.size(55.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        AttractiveText(
            text = "Login Your Phone",
            primaryColor = Color.White,
            shadowColor = Color(0xFF606060),
            fontSize = 22.sp,
            strokeWidth = 1.5f
        )

        Divider(
            color = yellow,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        CustomInputField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone Number",
            placeholder = "Enter your phone number",
            leadingIcon = Icons.Default.Phone,
            yellow = yellow,
            errorMessage = if (errorMessage?.contains(
                    "credential",
                    true
                ) == true
            ) errorMessage else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = "Enter your password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(yellow),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRegisterClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = yellow),
            border = BorderStroke(1.dp, yellow),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                Text("Forgot Password", fontSize = 12.sp, color = Color.White)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onSupportClick() }
            ) {
                Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = Color.White)
                Text("Customer Support", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}


@Composable
fun RegistrationScreen(
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    referralCode: String,
    onReferralChange: (String) -> Unit,
    agreeToTerms: Boolean,
    onAgreeChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    errorMessage: String?
) {
    val darkRed = Color(0xFF460014)
    val yellow = Color(0xFFFFC107)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkRed)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(45.dp))

        AttractiveText(
            text = "KGF",
            primaryColor = Color.Yellow,
            shadowColor = Color(0xFF606060),
            fontSize = 28.sp,
            strokeWidth = 1.5f
        )

        Spacer(modifier = Modifier.height(28.dp))

        Icon(
            imageVector = Icons.Default.PhoneAndroid,
            contentDescription = "Phone Icon",
            tint = yellow,
            modifier = Modifier.size(55.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        AttractiveText(
            text = "Register Your Phone",
            primaryColor = Color.White,
            shadowColor = Color(0xFF606060),
            fontSize = 22.sp,
            strokeWidth = 1.5f
        )

        Divider(
            color = yellow,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))

        CustomInputField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone Number",
            placeholder = "Enter your phone number",
            leadingIcon = Icons.Default.Phone,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = "Enter your password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm Password",
            placeholder = "Confirm your password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            yellow = yellow,
            errorMessage = if (errorMessage?.contains("match", true) == true) errorMessage else null
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = referralCode,
            onValueChange = onReferralChange,
            label = "Referral Code",
            placeholder = "Enter referral code (optional)",
            leadingIcon = Icons.Default.CardGiftcard,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreeToTerms,
                onCheckedChange = onAgreeChange,
                colors = CheckboxDefaults.colors(checkedColor = yellow)
            )
            Text(text = "I agree to the ", color = Color.White)
            Text(text = "Terms & Conditions", color = yellow, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(Color(0xffecc00e)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = agreeToTerms
        ) {
            Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLoginClick,
            border = BorderStroke(1.dp, yellow),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = yellow),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontWeight = FontWeight.Bold)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    yellow: Color,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
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
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = 0.9f),
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