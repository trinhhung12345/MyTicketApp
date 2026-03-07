package com.example.myticketapp.presentation.tickets.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.Locale

private val ViLocale = Locale.forLanguageTag("vi-VN")

fun String?.formatToVNDate(): String {
    if (this.isNullOrBlank()) return "---"

    val normalized = this.replace(" ", "T")
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd"
    )

    patterns.forEach { pattern ->
        runCatching {
            val parser = SimpleDateFormat(pattern, ViLocale)
            val formatter = if (pattern == "yyyy-MM-dd") {
                SimpleDateFormat("dd/MM/yyyy", ViLocale)
            } else {
                SimpleDateFormat("dd/MM/yyyy HH:mm", ViLocale)
            }
            parser.parse(normalized)?.let(formatter::format)
        }.getOrNull()?.let { return it }
    }

    return this
}

@Composable
fun StatusBadge(status: String) {
    val badge = when (status) {
        "PAID" -> StatusBadgeData(
            background = Color(0xFFFCE7F3),
            text = Color(0xFFBE185D),
            icon = Icons.Default.CheckCircle,
            label = "Đã thanh toán"
        )

        "UNPAID" -> StatusBadgeData(
            background = Color(0xFFFFEDD5),
            text = Color(0xFFC2410C),
            icon = Icons.Default.Schedule,
            label = "Chờ thanh toán"
        )

        else -> StatusBadgeData(
            background = Color(0xFFFEE2E2),
            text = Color(0xFFB91C1C),
            icon = Icons.Default.Cancel,
            label = "Đã hủy"
        )
    }

    Row(
        modifier = Modifier
            .background(badge.background, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(badge.icon, contentDescription = null, tint = badge.text)
        Text(
            text = badge.label,
            color = badge.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun rememberQrBitmap(content: String, size: Int = 512): Bitmap? {
    return remember(content, size) {
        runCatching {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
            bitmap
        }.getOrNull()
    }
}

private data class StatusBadgeData(
    val background: Color,
    val text: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)