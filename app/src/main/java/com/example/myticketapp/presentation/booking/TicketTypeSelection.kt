package com.example.myticketapp.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myticketapp.domain.model.TicketType
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.ui.theme.PrimaryPink
import androidx.compose.foundation.layout.navigationBarsPadding
@Composable
fun TicketTypeSelectionScreen(
    ticketTypes: List<TicketType>,
    quantities: Map<Int, Int>,
    onQuantityChange: (TicketType, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(ticketTypes) { ticketType ->
            val qty = quantities[ticketType.id] ?: 0
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0E14)), // Nền tiệp với app
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2C3440), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cột thông tin vé
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ticketType.name.uppercase(),
                            color = PrimaryPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ticketType.description,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = ticketType.price.formatVND().replace("Từ ", ""),
                            color = PrimaryPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // Cột Stepper[ -  0  + ]
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF161B22), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút Trừ
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Minus",
                            tint = if (qty > 0) Color.White else Color.DarkGray,
                            modifier = Modifier.size(20.dp).clickable(enabled = qty > 0) { 
                                onQuantityChange(ticketType, -1) 
                            }
                        )
                        
                        // Số lượng
                        Text(
                            text = qty.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        // Nút Cộng
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = PrimaryPink,
                            modifier = Modifier.size(20.dp).clickable { 
                                onQuantityChange(ticketType, 1) 
                            }
                        )
                    }
                }
            }
        }
    }
}

// Giao diện Bottom Bar tĩnh cho chế độ này
@Composable
fun TicketListBottomBar(
    totalTickets: Int,
    totalPrice: Long,
    onCheckout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // <--- THÊM DÒNG NÀY ĐỂ TRÁNH THANH ĐIỀU HƯỚNG
            .background(Color(0xFF161B22))
            .border(1.dp, Color(0xFF1F2937))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("$totalTickets vé", color = Color.Gray, fontSize = 12.sp)
            Text(totalPrice.formatVND().replace("Từ ", ""), color = PrimaryPink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onCheckout,
            enabled = totalTickets > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF903A62),
                contentColor = Color.LightGray,
                disabledContainerColor = Color(0xFF3B2034),
                disabledContentColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Text("Tiếp tục →", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}