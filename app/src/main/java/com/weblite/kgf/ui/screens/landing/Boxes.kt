package com.weblite.kgf.ui.screens.landing

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.User
import com.weblite.kgf.data.dummyUsers
import com.weblite.kgf.ui.screens.MainScreen
import com.weblite.kgf.ui.screens.auth.CustomInputField


@Composable
fun AuthDialog(
    index: Int,
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(index) }

    // Shared State
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val loginState = viewModel.loginState.value
    val registerState = viewModel.signupState.value

    //Handle login result
    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                val userid = loginState.data?.result?.user?.userId ?: "null"


                SharedPrefManager.setString("user_id",userid)
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                viewModel.resetLoginState()
                onDismiss()
                onLoginSuccess()
            }
            is Resource.Error -> {
                errorMessage = loginState.message ?: "Login failed"
            }
            else -> {}
        }
    }

    // Handle register result
    LaunchedEffect(registerState) {
        when (registerState) {
            is Resource.Success -> {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                viewModel.resetSignupState()
                selectedTabIndex = 0 // move to login tab
            }
            is Resource.Error -> {
                errorMessage = registerState.message ?: "Registration failed"
            }
            else -> {}
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.wrapContentWidth().padding(2.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF3f0110)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("K.G.F", color = Color.White)
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close",
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .clickable { onDismiss() },
                        tint = Color.White
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF3f0110),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFFDEAF56)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            errorMessage = null
                        },
                        text = {
                            Text("Login", color = if (selectedTabIndex == 0) Color(0xFFDEAF56) else Color.White)
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            errorMessage = null
                        },
                        text = {
                            Text("Register", color = if (selectedTabIndex == 1) Color(0xFFDEAF56) else Color.White)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTabIndex == 0) {
                    LoginSection(
                        phone = phone,
                        password = password,
                        onPhoneChange = { phone = it },
                        onPasswordChange = { password = it },
                        onLoginClick = {
                            if (phone.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(phone.trim(), password.trim())
                            } else {
                                errorMessage = "Enter phone and password"
                            }
                        },
                        errorMessage = errorMessage
                    )
                } else {
                    RegisterSection(
                        phone = phone,
                        password = password,
                        confirmPassword = confirmPassword,
                        referralCode = referralCode,
                        agreeToTerms = agreeToTerms,
                        onPhoneChange = { phone = it },
                        onPasswordChange = { password = it },
                        onConfirmPasswordChange = { confirmPassword = it },
                        onReferralChange = { referralCode = it },
                        onAgreeChange = { agreeToTerms = it },
                        onRegisterClick = {
                            if (phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                errorMessage = "All fields are required"
                            } else if (password != confirmPassword) {
                                errorMessage = "Passwords do not match"
                            } else if (!agreeToTerms) {
                                errorMessage = "Please agree to terms"
                            } else {
                                viewModel.signup(phone.trim(), password.trim(), confirmPassword.trim(), referralCode.trim())
                            }
                        },
                        errorMessage = errorMessage
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun LoginSection(
    phone: String,
    password: String,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    errorMessage: String?
) {
    val yellow = Color(0xFFDEAF56)
    var checkedState by remember { mutableStateOf(false) }

    Column {
        CustomInputField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone",
            placeholder = "Enter your phone number",
            leadingIcon = Icons.Default.Phone,
            yellow = yellow,
            errorMessage = errorMessage
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = "Enter your password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checkedState, onCheckedChange = { checkedState = it })
            Text("I'm over 18 years old and agree to the terms", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            enabled = checkedState && phone.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login", color = Color.White)
        }
    }
}

@Composable
fun RegisterSection(
    phone: String,
    password: String,
    confirmPassword: String,
    referralCode: String,
    agreeToTerms: Boolean,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onReferralChange: (String) -> Unit,
    onAgreeChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String?
) {
    val yellow = Color(0xFFDEAF56)

    Column {
        CustomInputField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone",
            placeholder = "Enter your phone number",
            leadingIcon = Icons.Default.Phone,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = "Enter your password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomInputField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm Password",
            placeholder = "Re-enter your password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomInputField(
            value = referralCode,
            onValueChange = onReferralChange,
            label = "Referral Code (Optional)",
            placeholder = "Enter referral code",
            leadingIcon = Icons.Default.CardGiftcard,
            yellow = yellow
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreeToTerms, onCheckedChange = onAgreeChange)
            Text("I'm over 18 years old and agree to the terms", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            enabled = agreeToTerms &&
                    phone.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    password == confirmPassword
        ) {
            Text("Register", color = Color.White)
        }
    }
}
@Composable
@Preview
fun PreviewAuthDialog() {
    //AuthDialog(0, onDismiss = {})
    AuthDialog(index = 1, onDismiss = { true},onLoginSuccess = {
    })
}