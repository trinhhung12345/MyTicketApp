package com.example.myticketapp.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

// Colors mapped from your HTML
val DarkBg = Color(0xFF0F172A)
val BrandPink = Color(0xFFFF2D78)

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Animation States
    var isVisible by remember { mutableStateOf(false) }

    // Animate opacity for logo (Slow fade-in)
    val alphaAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = EaseInOut),
        label = "fade_in"
    )

    // Xử lý navigation dựa trên token
    LaunchedEffect(state.isLoading, state.hasToken) {
        if (!state.isLoading) {
            isVisible = true
            delay(1500) // Hiển thị logo đẹp 1.5s

            if (state.hasToken) {
                // Có token -> Về Home (auto login thành công)
                onNavigateToHome()
            } else {
                // Không có token -> Về Login
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Background Decoration for Elegance (Glowing Orbs)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Top Left Blur
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(BrandPink.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = 400f
                ),
                radius = 400f,
                center = Offset(0f, 0f)
            )

            // Bottom Right Blur
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(BrandPink.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(canvasWidth, canvasHeight),
                    radius = 500f
                ),
                radius = 500f,
                center = Offset(canvasWidth, canvasHeight)
            )
        }

        // Logo Section (Center)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alphaAnim), // Áp dụng fade-in
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TixCon",
                color = BrandPink,
                style = TextStyle(
                    fontSize = 60.sp, // ~ text-6xl
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp, // tracking-tighter
                    shadow = Shadow(
                        color = BrandPink.copy(alpha = 0.3f),
                        blurRadius = 15f
                    )
                ),
                modifier = Modifier.padding(bottom = 32.dp) // mb-8
            )

            // Logo Divider / Aesthetic line
            Box(
                modifier = Modifier
                    .width(48.dp) // w-12
                    .height(4.dp) // h-1
                    .background(color = BrandPink.copy(alpha = 0.8f), shape = CircleShape)
            )
        }

        // Loading Section (Bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp), // bottom-20
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // space-y-4
        ) {
            // Subtle Text with infinite pulse
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val textAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "text_alpha"
            )

            Text(
                text = "ONLINE BOOKING",
                color = Color.White.copy(alpha = textAlpha),
                style = TextStyle(
                    fontSize = 14.sp, // text-sm
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.8.sp // tracking-[0.2em]
                )
            )

            // Minimalist Loading Indicator (Bouncing dots)
            BouncingDots()
        }
    }
}

@Composable
fun BouncingDots() {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 150L) // Stagger effect like[animation-delay:-0.3s]
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        0.0f at 0
                        (-10f) at 300 // Bounce up
                        0.0f at 600
                        0.0f at 1000
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { // space-x-2
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .offset(y = animatable.value.dp)
                    .size(6.dp) // w-1.5 h-1.5
                    .background(BrandPink, CircleShape)
            )
        }
    }
}