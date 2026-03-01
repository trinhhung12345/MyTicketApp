package com.example.myticketapp.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Divider
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

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit, // Dùng để chuyển sang màn Home sau này
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = state) {
        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }
        if (state.isSuccess) {
            Toast.makeText(context, state.successMessage, Toast.LENGTH_SHORT).show()
            onLoginSuccess() // Chuyển màn hình
        }
    }


    AuthBackground {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo & Header
            TixConLogo()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Chào mừng bạn trở lại", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Text("Đăng nhập", color = PrimaryPink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // Inputs
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Nhập email của bạn",
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu",
                placeholder = "Nhập mật khẩu",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                rightLabelText = "Quên mật khẩu?",
                onRightLabelClick = { /* Xử lý quên mật khẩu */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Button
            if (state.isLoading) {
                CircularProgressIndicator(color = PrimaryPink)
            } else {
                AuthButton(
                    text = "Đăng nhập",
                    onClick = { viewModel.login(email, password) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.LightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Chưa có tài khoản? ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "Tạo tài khoản",
                    color = PrimaryPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToRegister = {},
        onLoginSuccess = {}
    )
}
