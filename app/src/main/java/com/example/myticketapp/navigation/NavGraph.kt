package com.example.myticketapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myticketapp.presentation.auth.LoginScreen
import com.example.myticketapp.presentation.auth.RegisterScreen
import com.example.myticketapp.presentation.booking.BookingScreen
import com.example.myticketapp.presentation.detail.EventDetailScreen
import com.example.myticketapp.presentation.home.HomeScreen
import com.example.myticketapp.presentation.profile.ProfileScreen
import com.example.myticketapp.presentation.splash.SplashScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { MainBottomBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 1. Màn hình Splash
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Màn hình Login
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Màn hình Register
            composable(route = Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 4. Màn hình Home
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onEventClick = { eventId ->
                        navController.navigate(Screen.EventDetail.passId(eventId))
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onSessionExpired = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 5. Màn hình Chi tiết Sự kiện
            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) {
                EventDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onTokenExpired = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    navController = navController
                )
            }

            // 6. Màn hình Đặt vé
            composable(
                route = Screen.Booking.route,
                arguments = listOf(navArgument("showingId") { type = NavType.IntType })
            ) {
                BookingScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 7. Màn hình Profile
            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
