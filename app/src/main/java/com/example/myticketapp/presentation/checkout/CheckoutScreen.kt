package com.example.myticketapp.presentation.checkout

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.CheckoutCart
import com.example.myticketapp.presentation.checkout.components.OrderTimerBlock
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.ui.theme.PrimaryPink
import com.google.gson.Gson

private val CheckoutBg = Color(0xFF0B0E14)
private val CheckoutCard = Color(0xFF161B22)
private val CheckoutBorder = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartJson: String,
    onBackClick: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val gson = remember { Gson() }

    LaunchedEffect(cartJson) {
        if (cartJson.isBlank()) {
            viewModel.setCartError("Không tìm thấy dữ liệu thanh toán")
            return@LaunchedEffect
        }

        runCatching { gson.fromJson(cartJson, CheckoutCart::class.java) }
            .onSuccess(viewModel::initCart)
            .onFailure {
                viewModel.setCartError("Không thể đọc dữ liệu thanh toán")
            }
    }

    LaunchedEffect(state.uiEvent) {
        if (state.uiEvent.isNotBlank()) {
            Toast.makeText(context, state.uiEvent, Toast.LENGTH_SHORT).show()
            viewModel.clearUiEvent()
        }
    }

    LaunchedEffect(state.payosUrl) {
        val paymentUrl = state.payosUrl ?: return@LaunchedEffect
        if (paymentUrl.isBlank()) {
            viewModel.onPayosUrlOpened()
            return@LaunchedEffect
        }

        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "Không tìm thấy ứng dụng để mở link thanh toán", Toast.LENGTH_SHORT).show()
        } finally {
            viewModel.onPayosUrlOpened()
        }
    }

    Scaffold(
        containerColor = CheckoutBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CheckoutBg),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Xác nhận đặt vé",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.cart?.eventName.orEmpty(),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPink)
                }
            }

            state.screenError.isNotBlank() || state.cart == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.screenError.ifBlank { "Không có dữ liệu thanh toán" },
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    item {
                        SelectedTicketsCard(cart = state.cart, totalTickets = state.totalTickets)
                    }
                    item {
                        ReceiverInfoCard(
                            name = state.name,
                            onNameChange = viewModel::onNameChange,
                            phone = state.phone,
                            onPhoneChange = viewModel::onPhoneChange,
                            email = state.email,
                            onEmailChange = viewModel::onEmailChange,
                            address = state.address,
                            onAddressChange = viewModel::onAddressChange,
                            readOnly = state.createdOrder != null
                        )
                    }
                    item {
                        PaymentSummaryCard(
                            state = state,
                            viewModel = viewModel,
                            onBackClick = onBackClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedTicketsCard(cart: CheckoutCart, totalTickets: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CheckoutCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CheckoutBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = PrimaryPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vé đã chọn ($totalTickets)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cart.hasSeatMap) {
                cart.selectedSeats.forEach { seat ->
                    TicketRowItem(
                        title = seat.code,
                        subtitle = seat.sectionName,
                        price = seat.price
                    )
                }
            } else {
                cart.selectedTickets.forEach { item ->
                    TicketRowItem(
                        title = item.ticketTypeName,
                        subtitle = "x${item.quantity}",
                        price = item.price * item.quantity
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketRowItem(title: String, subtitle: String, price: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(CheckoutBg, RoundedCornerShape(8.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = price.formatVND().replace("Từ ", ""),
            color = PrimaryPink,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ReceiverInfoCard(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    readOnly: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CheckoutCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CheckoutBorder, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thông tin người nhận vé",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            CheckoutTextField(label = "Họ và tên", value = name, onValueChange = onNameChange, readOnly = readOnly)
            CheckoutTextField(
                label = "Số điện thoại",
                value = phone,
                onValueChange = onPhoneChange,
                keyboardType = KeyboardType.Phone,
                readOnly = readOnly
            )
            CheckoutTextField(
                label = "Email",
                value = email,
                onValueChange = onEmailChange,
                keyboardType = KeyboardType.Email,
                readOnly = readOnly
            )
            CheckoutTextField(label = "Địa chỉ", value = address, onValueChange = onAddressChange, readOnly = readOnly)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false
) {
    Column {
        Text(
            text = "$label *",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, CheckoutBorder, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CheckoutBg,
                unfocusedContainerColor = CheckoutBg,
                disabledContainerColor = CheckoutBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = PrimaryPink,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
private fun PaymentSummaryCard(
    state: CheckoutState,
    viewModel: CheckoutViewModel,
    onBackClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CheckoutCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CheckoutBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tổng thanh toán",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (state.cart?.showingTime?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(state.cart.showingTime, color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Số lượng vé", color = Color.Gray, fontSize = 14.sp)
                Text(state.totalTickets.toString(), color = Color.White, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tạm tính", color = Color.Gray, fontSize = 14.sp)
                Text(state.totalPrice.formatVND().replace("Từ ", ""), color = Color.White, fontSize = 14.sp)
            }

            HorizontalDivider(color = CheckoutBorder, modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng cộng", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = state.totalPrice.formatVND().replace("Từ ", ""),
                    color = PrimaryPink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.errorMessage.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF450A0A), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(state.errorMessage, color = Color(0xFFF87171), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val createdOrder = state.createdOrder

            when {
                createdOrder == null -> {
                    Button(
                        onClick = viewModel::createOrder,
                        enabled = !state.isCreatingOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (state.isCreatingOrder) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Xác nhận đặt vé", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                createdOrder.status == "UNPAID" -> {
                    OrderTimerBlock(
                        createdAt = createdOrder.createdAt,
                        onExpired = viewModel::markOrderAsExpired
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::processPayment,
                        enabled = !state.isCheckingOut,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB2777)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (state.isCheckingOut) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thanh toán ngay", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                createdOrder.status == "CANCELLED" -> {
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Quay lại đặt vé", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                else -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Đơn hàng ${createdOrder.status}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Bằng việc đặt vé, bạn đồng ý với Điều khoản sử dụng của chúng tôi",
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(top = 12.dp)
            )
        }
    }
}