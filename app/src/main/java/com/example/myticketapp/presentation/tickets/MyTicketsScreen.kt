package com.example.myticketapp.presentation.tickets

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.Order
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.presentation.tickets.components.StatusBadge
import com.example.myticketapp.presentation.tickets.components.formatToVNDate
import com.example.myticketapp.ui.theme.DarkBg
import com.example.myticketapp.ui.theme.DarkCard
import com.example.myticketapp.ui.theme.PrimaryPink

@Composable
fun MyTicketsScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: MyTicketsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var filterStatus by remember { mutableStateOf("ALL") }

    val filteredOrders = if (filterStatus == "ALL") {
        state.orders
    } else {
        state.orders.filter { it.status == filterStatus }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PrimaryPink, Color(0xFFDB2777))))
                .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vé của tôi",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Quản lý tất cả đơn hàng và vé bạn đã đặt",
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

            state.error.isNotBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.error, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        val totalTickets = state.orders.sumOf { it.totalQuantity }
                        val paidCount = state.orders.count { it.status == "PAID" }
                        val unpaidCount = state.orders.count { it.status == "UNPAID" }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard("Đơn hàng", state.orders.size.toString(), Icons.Default.Receipt, PrimaryPink, Modifier.weight(1f))
                            StatCard("Tổng vé", totalTickets.toString(), Icons.Default.ConfirmationNumber, Color(0xFF3B82F6), Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard("Đã thanh toán", paidCount.toString(), Icons.Default.CheckCircle, PrimaryPink, Modifier.weight(1f))
                            StatCard("Chờ thanh toán", unpaidCount.toString(), Icons.Default.Schedule, Color(0xFFF59E0B), Modifier.weight(1f))
                        }
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { FilterChip("Tất cả", "ALL", filterStatus) { filterStatus = it } }
                            item { FilterChip("Đã thanh toán", "PAID", filterStatus) { filterStatus = it } }
                            item { FilterChip("Chờ thanh toán", "UNPAID", filterStatus) { filterStatus = it } }
                            item { FilterChip("Đã hủy", "CANCELLED", filterStatus) { filterStatus = it } }
                        }
                    }

                    if (filteredOrders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkCard, RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(16.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Chưa có đơn hàng phù hợp", color = Color.White)
                            }
                        }
                    } else {
                        items(filteredOrders) { order ->
                            OrderCard(order = order, onClick = { onNavigateToDetail(order.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        modifier = modifier.border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun FilterChip(label: String, type: String, currentFilter: String, onClick: (String) -> Unit) {
    val isSelected = type == currentFilter
    val bgColor = if (isSelected) PrimaryPink else DarkCard
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, if (isSelected) PrimaryPink else Color(0xFF1F2937), RoundedCornerShape(8.dp))
            .clickable { onClick(type) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.code, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${order.totalQuantity} vé • ${(order.paymentAt ?: order.createdAt).formatToVNDate()}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(order.status)
                    Text(
                        text = order.totalAmount.formatVND().replace("Từ ", ""),
                        color = PrimaryPink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (order.orderDetails.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    order.orderDetails.take(3).forEach { detail ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(detail.seatCode, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    if (order.orderDetails.size > 3) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+${order.orderDetails.size - 3} vé khác", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
