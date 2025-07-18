package com.weblite.kgf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohit.kgfindia.ui.ContentScreen
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.ui.screens.MainScreen
import com.weblite.kgf.ui.screens.auth.AuthFlowScreen
import com.weblite.kgf.ui.screens.auth.ForgotPasswordScreen
//import com.weblite.kgf.ui.screens.auth.AuthScreen
import com.weblite.kgf.ui.theme.KGFTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefManager.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            KGFTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val userId = SharedPrefManager.getString("user_id", "0")
    val isLoggedIn = remember { mutableStateOf(userId != "0") }

    LaunchedEffect(key1 = isLoggedIn.value) {
        if (isLoggedIn.value) {
            viewModel.fetchDashboard(userId)
        }
    }

    Scaffold { paddingValues ->
        if (isLoggedIn.value) {
            MainScreen()
        } else {
            ContentScreen(modifier = Modifier.padding(paddingValues))
        }
    }

//    AuthFlowScreen(
//        startWithLogin = TODO(),
//        onLoginSuccess = TODO(),
//        onClose = TODO(),
//        viewModel = TODO()
//    )
//    ForgotPasswordScreen()

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KGFTheme {
        Greeting("Android")
    }
}