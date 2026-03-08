package com.example.myticketapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myticketapp.ui.theme.PrimaryPink

val DarkCard = Color(0xFF161B22)

/**
 * Bottom Navigation Bar dùng chung cho Home và Profile screens
 */
@Composable
fun MainBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (
        currentRoute == Screen.Home.route ||
        currentRoute == Screen.MyTickets.route ||
        currentRoute == Screen.Profile.route
    ) {
        NavigationBar(
            containerColor = DarkCard.copy(alpha = 0.95f),
            contentColor = Color.Gray
        ) {
            NavigationBarItem(
                selected = currentRoute == Screen.Home.route,
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Trang chủ") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPink,
                    selectedTextColor = PrimaryPink,
                    indicatorColor = Color.Transparent
                )
            )
//            NavigationBarItem(
//                selected = currentRoute == "search",
//                onClick = { /* TODO: Navigate to Search */ },
//                icon = { Icon(Icons.Default.Search, null) },
//                label = { Text("Tìm kiếm") },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = PrimaryPink,
//                    selectedTextColor = PrimaryPink,
//                    indicatorColor = Color.Transparent
//                )
//            )
            NavigationBarItem(
                selected = currentRoute == Screen.MyTickets.route,
                onClick = {
                    navController.navigate(Screen.MyTickets.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(Icons.Default.ConfirmationNumber, null) },
                label = { Text("Vé của tôi") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPink,
                    selectedTextColor = PrimaryPink,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Profile.route,
                onClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Cá nhân") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPink,
                    selectedTextColor = PrimaryPink,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
