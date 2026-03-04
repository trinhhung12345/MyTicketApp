package com.example.myticketapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myticketapp.presentation.auth.LoginScreen
import com.example.myticketapp.presentation.auth.RegisterScreen
import com.example.myticketapp.presentation.detail.EventDetailScreen
import com.example.myticketapp.presentation.home.HomeScreen
import com.example.myticketapp.presentation.splash.SplashScreen
import com.example.myticketapp.ui.theme.MyTicketAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MyTicketAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "splash") {
                        // 1. Màn hình Splash
                        composable("splash") {
                            SplashScreen(
                                onNavigateToHome = {
                                    // Có token -> Về Home
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    // Không có token -> Về Login
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. Màn hình Login
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 3. Màn hình Register
                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = { navController.navigateUp() }
                            )
                        }

                        // 4. Màn hình Home
                        composable("home") {
                            HomeScreen(
                                onEventClick = { eventId ->
                                    navController.navigate("event_detail/$eventId")
                                }
                            )
                        }

                        // 5. Màn hình Chi tiết Sự kiện
                        composable(
                            route = "event_detail/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
                        ) {
                            EventDetailScreen(
                                onBackClick = { navController.popBackStack() },
                                onTokenExpired = {
                                    // Xóa toàn bộ lịch sử backstack và đá về Login
                                    navController.navigate("login") {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}