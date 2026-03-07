package com.example.myticketapp.presentation.checkout.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

private const val ORDER_TIMEOUT_MS = 60_000L
private val TailwindOrange400 = Color(0xFFFB923C)
private val TailwindOrange500 = Color(0xFFF97316)
private val TailwindOrange950 = Color(0xFF431407)
private val TailwindRed400 = Color(0xFFF87171)
private val TailwindRed500 = Color(0xFFEF4444)
private val TailwindRed950 = Color(0xFF450A0A)
private val TailwindSlate700 = Color(0xFF334155)

private fun parseServerDateToMs(dateString: String): Long {
    val formats = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    formats.forEach { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(dateString)?.time
        }.getOrNull()?.let { return it }
    }

    return System.currentTimeMillis()
}

private fun getRemainingMs(createdAt: String): Long {
    val createdMs = parseServerDateToMs(createdAt)
    val expiryTime = createdMs + ORDER_TIMEOUT_MS
    return maxOf(0L, expiryTime - System.currentTimeMillis())
}

private fun formatTime(ms: Long): String {
    if (ms <= 0L) return "00:00"

    val totalSeconds = kotlin.math.ceil(ms / 1000.0).toLong()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
fun OrderTimerBlock(
    createdAt: String,
    onExpired: () -> Unit
) {
    var remainingMs by remember(createdAt) { mutableLongStateOf(getRemainingMs(createdAt)) }
    var hasExpired by remember(createdAt) { mutableStateOf(remainingMs <= 0L) }

    LaunchedEffect(createdAt) {
        remainingMs = getRemainingMs(createdAt)
        if (remainingMs <= 0L) {
            if (!hasExpired) {
                hasExpired = true
                onExpired()
            }
            return@LaunchedEffect
        }

        hasExpired = false
        while (true) {
            val updatedRemaining = getRemainingMs(createdAt)
            remainingMs = updatedRemaining

            if (updatedRemaining <= 0L) {
                hasExpired = true
                onExpired()
                break
            }

            delay(1_000L)
        }
    }

    val isUrgent = remainingMs in 1..15_000
    val targetProgress = (remainingMs.toFloat() / ORDER_TIMEOUT_MS).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "order_timer_progress"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "order_timer_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "order_timer_alpha"
    )
    val animatedAlpha = if (isUrgent) pulseAlpha else 1f

    if (hasExpired) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TailwindRed950.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(1.dp, TailwindRed500.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = TailwindRed400,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đã hết thời gian thanh toán",
                    color = TailwindRed400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Đơn hàng đã bị hủy do quá thời gian thanh toán. Vui lòng đặt lại vé.",
                color = TailwindRed400.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
        return
    }

    val bgColor = if (isUrgent) TailwindRed950.copy(alpha = 0.5f) else TailwindOrange950.copy(alpha = 0.4f)
    val borderColor = if (isUrgent) TailwindRed500.copy(alpha = 0.6f) else TailwindOrange500.copy(alpha = 0.5f)
    val textColor = if (isUrgent) TailwindRed400 else TailwindOrange400
    val progressColor = if (isUrgent) TailwindRed500 else TailwindOrange500

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(animatedAlpha)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thời gian còn lại",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Text(
                text = formatTime(remainingMs),
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.alpha(animatedAlpha)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(TailwindSlate700)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vui lòng hoàn tất thanh toán trước khi hết thời gian",
            color = textColor.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}