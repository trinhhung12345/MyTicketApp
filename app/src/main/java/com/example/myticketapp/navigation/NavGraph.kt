package com.example.myticketapp.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.myticketapp.domain.utils.SessionManager
import com.example.myticketapp.presentation.checkout.CheckoutScreen
import com.example.myticketapp.presentation.auth.LoginScreen
import com.example.myticketapp.presentation.auth.RegisterScreen
import com.example.myticketapp.presentation.booking.BookingScreen
import com.example.myticketapp.presentation.chatbot.ChatbotScreen
import com.example.myticketapp.presentation.detail.EventDetailScreen
import com.example.myticketapp.presentation.event_list.EventListScreen
import com.example.myticketapp.presentation.home.HomeScreen
import com.example.myticketapp.presentation.tickets.MyTicketsScreen
import com.example.myticketapp.presentation.tickets.OrderDetailScreen
import com.example.myticketapp.presentation.profile.ProfileScreen
import com.example.myticketapp.presentation.splash.SplashScreen
import com.google.gson.Gson

private const val PaymentRedirectHost = "tixcon.netlify.app"
private val NavDarkCard = Color(0xFF161B22)
private val NavPrimaryColor = Color(0xFFFF2D78)

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager,
    appLinkIntent: Intent? = null
) {
    // State cho dialog phiên hết hạn toàn cục
    var showSessionExpiredDialog by remember { mutableStateOf(false) }

    // Lắng nghe sự kiện session expired từ interceptor
    LaunchedEffect(Unit) {
        sessionManager.sessionExpiredEvent.collect {
            showSessionExpiredDialog = true
        }
    }

    Box {
    Scaffold(
        bottomBar = { MainBottomBar(navController = navController) }
    ) { paddingValues ->
        LaunchedEffect(appLinkIntent) {
            val data = appLinkIntent?.data ?: return@LaunchedEffect
            val isPaymentRedirect = data.host == PaymentRedirectHost &&
                (data.scheme == "https" || data.scheme == "http")

            if (isPaymentRedirect) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

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
            composable(
                route = Screen.Home.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://tixcon.netlify.app" },
                    navDeepLink { uriPattern = "https://tixcon.netlify.app/" },
                    navDeepLink { uriPattern = "https://tixcon.netlify.app/{redirectPath}" },
                    navDeepLink { uriPattern = "http://tixcon.netlify.app" },
                    navDeepLink { uriPattern = "http://tixcon.netlify.app/" },
                    navDeepLink { uriPattern = "http://tixcon.netlify.app/{redirectPath}" }
                )
            ) {
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
                    },
                    onCategoryViewAllClick = { categoryId, categoryName ->
                        navController.navigate(Screen.EventList.passData(categoryId, categoryName))
                    },
                    onChatClick = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }

            composable(route = Screen.MyTickets.route) {
                MyTicketsScreen(
                    onNavigateToDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.passId(orderId))
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
            ) { backStackEntry ->
                val showingId = backStackEntry.arguments?.getInt("showingId") ?: 0
                val bookingStateHandle = navController.previousBackStackEntry?.savedStateHandle
                val eventName = bookingStateHandle?.get<String>("bookingEventName").orEmpty()
                val showingTime = bookingStateHandle?.get<String>("bookingShowingTime").orEmpty()

                BookingScreen(
                    showingId = showingId,
                    eventName = eventName,
                    showingTime = showingTime,
                    onBackClick = { navController.popBackStack() },
                    onCheckout = { cart ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "checkoutCartJson",
                            Gson().toJson(cart)
                        )
                        navController.navigate(Screen.Checkout.route)
                    }
                )
            }

            composable(route = Screen.Checkout.route) {
                val cartJson = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("checkoutCartJson")
                    .orEmpty()

                CheckoutScreen(
                    cartJson = cartJson,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) {
                OrderDetailScreen(
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

            // 8. Màn hình Chatbot
            composable(route = Screen.Chatbot.route) {
                ChatbotScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EventList.route,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.IntType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) {
                EventListScreen(
                    onBackClick = { navController.popBackStack() },
                    onEventClick = { eventId ->
                        navController.navigate(Screen.EventDetail.passId(eventId))
                    }
                )
            }
        }
    }

    // Dialog phiên đăng nhập hết hạn — hiển thị trên mọi màn hình
    if (showSessionExpiredDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NavPrimaryColor
                )
            },
            title = {
                Text(
                    text = "Phiên đăng nhập đã hết hạn",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Vui lòng đăng nhập lại để tiếp tục sử dụng ứng dụng",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSessionExpiredDialog = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavPrimaryColor
                    )
                ) {
                    Text("Đăng nhập lại")
                }
            },
            containerColor = NavDarkCard
        )
    }
    } // Box
}
