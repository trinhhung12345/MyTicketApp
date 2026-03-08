package com.example.myticketapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MyTickets : Screen("tickets")
    object Profile : Screen("profile")
    object Chatbot : Screen("chatbot")
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

    object EventList : Screen("event_list/{categoryId}/{categoryName}") {
        fun passData(categoryId: Int, categoryName: String): String {
            // Encode categoryName để tránh lỗi nếu tên có khoảng trắng hoặc ký tự đặc biệt
            val encodedName = java.net.URLEncoder.encode(categoryName, "UTF-8")
            return "event_list/$categoryId/$encodedName"
        }
    }
}
