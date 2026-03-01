package com.example.myticketapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myticketapp.presentation.auth.LoginScreen
import com.example.myticketapp.presentation.auth.RegisterScreen
import com.example.myticketapp.presentation.splash.SplashScreen
import com.example.myticketapp.ui.theme.MyTicketAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Cài đặt thư viện core-splashscreen TRƯỚC onCreate
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            MyTicketAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToHome = {
                                    // Sau khi splash xong, chuyển đến màn hình Đăng nhập
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = {
                                    // TODO: Chuyển sang HomeScreen sau này
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = { navController.navigateUp() } // Trở về Login
                            )
                        }
                    }
                }
            }
        }
    }
}
