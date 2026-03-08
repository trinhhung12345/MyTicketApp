package com.example.myticketapp.presentation.chatbot

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.ChatMessage
import com.example.myticketapp.ui.theme.PrimaryPink

val DarkBg = Color(0xFF0B0E14)
val DarkCard = Color(0xFF161B22)
val BotBubbleColor = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Tự động cuộn xuống cuối khi có tin nhắn mới
    LaunchedEffect(viewModel.messages.size, viewModel.isTyping.value) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PrimaryPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "B",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "TixCon Assistant",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Online",
                                color = Color.Green,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkCard)
            )
        },
        bottomBar = {
            // Thanh Chat Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Nhập tin nhắn...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkBg,
                        unfocusedContainerColor = DarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendTextMessage(inputText)
                        inputText = ""
                    },
                    modifier = Modifier.background(PrimaryPink, CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.messages) { message ->
                if (message.isFromUser) {
                    UserBubble(message.text)
                } else {
                    BotBubble(message, viewModel)
                }
            }
            if (viewModel.isTyping.value) {
                item { BotTypingIndicator() }
            }
        }
    }
}

@Composable
fun UserBubble(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Box(
            modifier = Modifier
                .background(
                    PrimaryPink,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 4.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(text, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun BotBubble(message: ChatMessage, viewModel: ChatViewModel) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Column(modifier = Modifier.fillMaxWidth(0.85f)) {
            // Text Reply
            Box(
                modifier = Modifier
                    .background(
                        BotBubbleColor,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(message.text, color = Color.White, fontSize = 14.sp)
            }

            // Options (Nếu có)
            val options = message.options
            if (!options.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    options.forEach { option ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, PrimaryPink, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.sendTextMessage(option.label) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                option.label,
                                color = PrimaryPink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Form Fields (Nếu có) -> AUTOFILL ÁP DỤNG Ở ĐÂY
            val formFields = message.formFields
            if (!formFields.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                BotForm(
                    initialName = viewModel.autofillName,
                    initialPhone = viewModel.autofillPhone,
                    initialEmail = viewModel.autofillEmail,
                    initialAddress = viewModel.autofillAddress,
                    onSubmit = { n, p, e, a -> viewModel.sendFormMessage(n, p, e, a) }
                )
            }
        }
    }
}

// Component Form nhúng trong Chat
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotForm(
    initialName: String,
    initialPhone: String,
    initialEmail: String,
    initialAddress: String,
    onSubmit: (String, String, String, String) -> Unit
) {
    // Lưu state nội bộ cho Form
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var email by remember { mutableStateOf(initialEmail) }
    var address by remember { mutableStateOf(initialAddress) }
    var isSubmitted by remember { mutableStateOf(false) } // Để khóa form sau khi bấm gửi

    Card(
        colors = CardDefaults.cardColors(containerColor = BotBubbleColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChatTextField("Họ tên *", name, { name = it }, isSubmitted)
            ChatTextField("Số điện thoại *", phone, { phone = it }, isSubmitted)
            ChatTextField("Email *", email, { email = it }, isSubmitted)
            ChatTextField("Địa chỉ", address, { address = it }, isSubmitted)

            Button(
                onClick = {
                    isSubmitted = true
                    onSubmit(name, phone, email, address)
                },
                enabled = !isSubmitted && name.isNotBlank() && phone.isNotBlank() && email.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSubmitted) "Đã gửi" else "Gửi thông tin")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp, color = Color.Gray) },
        readOnly = readOnly,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DarkBg,
            unfocusedContainerColor = DarkBg,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
fun BotTypingIndicator() {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Gray, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Gray, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Gray, CircleShape)
        )
    }
}
