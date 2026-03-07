package com.example.myticketapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MyTickets : Screen("tickets")
    object Profile : Screen("profile")
    object Checkout : Screen("checkout")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun passId(orderId: Int): String {
            return "order_detail/$orderId"
        }
    }
    object EventDetail : Screen("event_detail/{eventId}") {
        fun passId(eventId: Int): String {
            return "event_detail/$eventId"
        }
    }
    object Booking : Screen("booking/{showingId}") {
        fun passShowingId(showingId: Int): String {
            return "booking/$showingId"
        }
    }
}
