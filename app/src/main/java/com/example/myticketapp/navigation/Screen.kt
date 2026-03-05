package com.example.myticketapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
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
