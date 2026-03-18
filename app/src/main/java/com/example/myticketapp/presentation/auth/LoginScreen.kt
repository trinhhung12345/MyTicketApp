package com.example.myticketapp.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    var forgotEmail by remember { mutableStateOf("") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.isSuccess, state.successMessage) {
        if (state.isSuccess) {
            Toast.makeText(context, state.successMessage, Toast.LENGTH_SHORT).show()
            onLoginSuccess() // Chuyển màn hình
        }
    }

    LaunchedEffect(state.forgotPasswordSuccessMessage) {
        if (state.forgotPasswordSuccessMessage.isNotBlank()) {
            Toast.makeText(
                context,
                "${state.forgotPasswordSuccessMessage}. Vui lòng đăng nhập bằng mật khẩu mới từ email.",
                Toast.LENGTH_LONG
            ).show()
            showForgotPasswordDialog = false
            forgotEmail = ""
            viewModel.clearForgotPasswordFeedback()
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
                onRightLabelClick = {
                    showForgotPasswordDialog = true
                    forgotEmail = email
                    viewModel.clearForgotPasswordFeedback()
                }
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

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!state.isForgotPasswordLoading) {
                    showForgotPasswordDialog = false
                    viewModel.clearForgotPasswordFeedback()
                }
            },
            title = {
                Text(
                    text = "Quên mật khẩu",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Nhập email để nhận mật khẩu mới",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = "Email",
                        placeholder = "wearingarmor12345@gmail.com",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    if (state.forgotPasswordError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.forgotPasswordError,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp
                        )
                    }

                    if (state.forgotPasswordCooldownSeconds > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn có thể gửi lại sau ${state.forgotPasswordCooldownSeconds}s",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.forgotPassword(forgotEmail) },
                    enabled = !state.isForgotPasswordLoading && state.forgotPasswordCooldownSeconds == 0,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
                ) {
                    if (state.isForgotPasswordLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (state.forgotPasswordCooldownSeconds > 0) {
                        Text("Gửi lại sau ${state.forgotPasswordCooldownSeconds}s")
                    } else {
                        Text("Gửi mật khẩu mới")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showForgotPasswordDialog = false
                        viewModel.clearForgotPasswordFeedback()
                    },
                    enabled = !state.isForgotPasswordLoading
                ) {
                    Text("Hủy")
                }
            }
        )
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
