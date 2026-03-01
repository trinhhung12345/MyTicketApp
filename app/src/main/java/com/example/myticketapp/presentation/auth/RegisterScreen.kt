package com.example.myticketapp.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myticketapp.ui.theme.PrimaryPink

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AuthBackground {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Thêm scroll vì form đăng ký dài
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TixConLogo()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tham gia cùng chúng tôi", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Đăng ký", color = PrimaryPink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                value = name,
                onValueChange = { name = it },
                label = "Họ và tên",
                placeholder = "Nguyễn Văn A",
                leadingIcon = Icons.Outlined.Person
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Số điện thoại",
                placeholder = "0901 234 567",
                leadingIcon = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "vi-du@email.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu",
                placeholder = "••••••••",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Xác nhận mật khẩu",
                placeholder = "••••••••",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            AuthButton(text = "Đăng ký", onClick = { /* Xử lý đăng ký */ })

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Đã có tài khoản? ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "Đăng nhập tại đây",
                    color = PrimaryPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onNavigateToLogin = {})
}
