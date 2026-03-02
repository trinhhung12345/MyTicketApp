package com.example.myticketapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myticketapp.ui.theme.PrimaryPink

@Composable
fun HomeScreen() {
    // Tạm thời chỉ là một Box căn giữa chữ hiển thị
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to Home Screen!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryPink
        )
    }
}