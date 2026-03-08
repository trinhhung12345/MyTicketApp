package com.example.myticketapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.myticketapp.domain.utils.SessionManager
import com.example.myticketapp.domain.repository.SocketService
import com.example.myticketapp.navigation.SetupNavGraph
import com.example.myticketapp.ui.theme.MyTicketAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var appLinkIntent by mutableStateOf<Intent?>(null)

    @Inject
    lateinit var socketService: SocketService

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        appLinkIntent = intent

        // Mở Socket khi App mở
        socketService.connectAndSubscribe()

        setContent {
            MyTicketAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    SetupNavGraph(
                        navController = navController,
                        sessionManager = sessionManager,
                        appLinkIntent = appLinkIntent
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ngắt Socket khi App bị tắt
        socketService.disconnect()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        appLinkIntent = intent
    }
}
