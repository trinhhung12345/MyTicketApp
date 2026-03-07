package com.example.myticketapp.presentation.tickets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.BuildConfig
import com.example.myticketapp.domain.model.OrderDetail
import com.example.myticketapp.presentation.checkout.components.OrderTimerBlock
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.presentation.tickets.components.StatusBadge
import com.example.myticketapp.presentation.tickets.components.formatToVNDate
import com.example.myticketapp.presentation.tickets.components.rememberQrBitmap
import com.example.myticketapp.ui.theme.DarkBg
import com.example.myticketapp.ui.theme.DarkCard
import com.example.myticketapp.ui.theme.PrimaryPink

@Composable
fun OrderDetailScreen(
    onBackClick: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var selectedTicketForQr by remember { mutableStateOf<OrderDetail?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PrimaryPink, Color(0xFFDB2777))))
                .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onBackClick)
                        .padding(bottom = 16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    Text(" Quay lại", color = Color.White)
                }
                Text("Chi tiết đơn hàng", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Mã đơn: ${state.order?.code ?: "..."}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPink)
                }
            }

            state.error.isNotBlank() || state.order == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error.ifBlank { "Không tìm thấy đơn hàng" }, color = Color.White)
                }
            }

            else -> {
                val order = state.order
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(order.code, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            if (order.status == "PAID") {
                                                "Thanh toán: ${order.paymentAt.formatToVNDate()}"
                                            } else {
                                                "Tạo lúc: ${order.createdAt.formatToVNDate()}"
                                            },
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    StatusBadge(order.status)
                                }

                                if (order.status == "UNPAID") {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OrderTimerBlock(createdAt = order.createdAt, onExpired = viewModel::markAsCancelled)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                InfoRow(Icons.Default.Person, "Người nhận", order.recipientName.ifBlank { "---" })
                                InfoRow(Icons.Default.Phone, "Số điện thoại", order.recipientPhone.ifBlank { "---" })
                                InfoRow(Icons.Default.Email, "Email", order.recipientEmail.ifBlank { "---" })
                                InfoRow(Icons.Default.LocationOn, "Địa chỉ", order.recipientAddress.ifBlank { "---" })
                            }
                        }
                    }

                    item {
                        Text(
                            "Danh sách vé (${order.totalQuantity})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(order.orderDetails) { ticket ->
                        TicketDetailItem(
                            ticket = ticket,
                            status = order.status,
                            onShowQr = { selectedTicketForQr = ticket }
                        )
                    }
                }
            }
        }
    }

    selectedTicketForQr?.let { ticket ->
        val checkInBase = BuildConfig.API_BASE_URL.trimEnd('/')
        val checkInUrl = "$checkInBase/orders/check-in?token=${ticket.qr.orEmpty()}"
        QrCodeDialog(
            ticket = ticket,
            orderCode = state.order?.code.orEmpty(),
            checkInUrl = checkInUrl,
            onDismiss = { selectedTicketForQr = null }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = Color.Gray, fontSize = 10.sp)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TicketDetailItem(ticket: OrderDetail, status: String, onShowQr: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(if (status == "PAID") PrimaryPink else Color.Gray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = Color.White)
                        Text(ticket.seatCode, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    val originalPrice = ticket.originalPrice
                    Text(ticket.seatCode, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Mã ghế: ${ticket.seatId}", color = Color.Gray, fontSize = 12.sp)
                    if (originalPrice != null && originalPrice > ticket.price) {
                        Text(
                            "Giá gốc: ${originalPrice.formatVND()}",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    ticket.price.formatVND().replace("Từ ", ""),
                    color = PrimaryPink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (status == "PAID" && !ticket.qr.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onShowQr,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("QR Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QrCodeDialog(ticket: OrderDetail, orderCode: String, checkInUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Vé điện tử", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                val qrBitmap = rememberQrBitmap(content = checkInUrl, size = 600)
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(220.dp)
                            .border(2.dp, Color.LightGray, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Lỗi tải QR")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mã đơn", color = Color.Gray)
                        Text(orderCode, color = PrimaryPink, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mã ghế", color = Color.Gray)
                        Text(ticket.seatCode, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
                ) {
                    Text("Đóng", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Vui lòng xuất trình mã QR này khi check-in", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}