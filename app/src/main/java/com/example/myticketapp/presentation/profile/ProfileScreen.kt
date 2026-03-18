package com.example.myticketapp.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.presentation.home.DarkBg
import com.example.myticketapp.presentation.home.DarkCard
import com.example.myticketapp.ui.theme.PrimaryPink
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Màn hình Profile hiển thị thông tin cá nhân của user
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Loading state
        if (state.isLoading) {
            CircularProgressIndicator(
                color = PrimaryPink,
                modifier = Modifier.align(Alignment.Center)
            )
            return@Box
        }

        val profile = state.profile ?: return@Box
        var tempName by remember(profile) { mutableStateOf(profile.name) }
        var tempEmail by remember(profile) { mutableStateOf(profile.email) }
        var tempPhone by remember(profile) { mutableStateOf(profile.phone) }
        var tempAddress by remember(profile, state.address) {
            mutableStateOf(profile.address.ifBlank { state.address })
        }
        var tempBirthday by remember(profile) { mutableStateOf(profile.birthday) }
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var showBirthdayDatePicker by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // --- HEADER CURVE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(PrimaryPink)
                    .padding(top = 40.dp, bottom = 48.dp, start = 16.dp, end = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top bar với back button và settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Cá nhân",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { /* Settings */ }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Cài đặt",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Avatar
                    Box(modifier = Modifier.size(100.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(4.dp, Color.White.copy(0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tempName.firstOrNull()?.uppercase() ?: "U",
                                color = PrimaryPink,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .size(28.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Đổi avatar",
                                tint = PrimaryPink,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name
                    Text(
                        text = tempName,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Email
                    Text(
                        text = tempEmail,
                        color = Color.White.copy(0.8f),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // --- THÔNG TIN CÁ NHÂN ---
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .offset(y = (-20).dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF30363D), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryPink
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thông tin cá nhân",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Fields
                        ProfileTextField(
                            label = "Họ và tên",
                            value = tempName,
                            onValueChange = { tempName = it },
                            placeholder = "Nhập họ và tên"
                        )
                        ProfileTextField(
                            label = "Email",
                            value = tempEmail,
                            onValueChange = { tempEmail = it },
                            placeholder = "Nhập email"
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProfileTextField(
                                label = "Số điện thoại",
                                value = tempPhone,
                                onValueChange = { tempPhone = it },
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTextField(
                                label = "Vai trò",
                                value = profile.roleName,
                                readOnly = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showBirthdayDatePicker = true }
                        ) {
                            ProfileTextField(
                                label = "Ngày sinh",
                                value = tempBirthday,
                                readOnly = true,
                                enabled = false,
                                placeholder = "Chọn ngày sinh",
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Chọn ngày sinh"
                                    )
                                }
                            )
                        }

                        // Address field
                        ProfileTextField(
                            label = "Địa chỉ",
                            value = tempAddress,
                            onValueChange = { tempAddress = it },
                            placeholder = "Nhập địa chỉ của bạn"
                        )

                        // Save button
                        Button(
                            onClick = {
                                viewModel.updateProfile(
                                    name = tempName,
                                    email = tempEmail,
                                    phone = tempPhone,
                                    address = tempAddress,
                                    birthday = tempBirthday
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryPink
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !state.isSaving
                        ) {
                            Text(
                                text = if (state.isSaving) "Đang lưu..." else "Lưu thay đổi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        if (state.error.isNotBlank()) {
                            Text(
                                text = state.error,
                                color = Color(0xFFFF6B6B),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF30363D), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryPink
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đổi mật khẩu",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        ProfilePasswordField(
                            label = "Mật khẩu cũ",
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            placeholder = "Nhập mật khẩu cũ"
                        )

                        ProfilePasswordField(
                            label = "Mật khẩu mới",
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            placeholder = "Nhập mật khẩu mới"
                        )

                        ProfilePasswordField(
                            label = "Xác nhận mật khẩu mới",
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Nhập lại mật khẩu mới"
                        )

                        Button(
                            onClick = {
                                viewModel.changePassword(
                                    oldPassword = oldPassword,
                                    newPassword = newPassword,
                                    confirmPassword = confirmPassword
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !state.isChangingPassword
                        ) {
                            Text(
                                text = if (state.isChangingPassword) {
                                    "Đang đổi mật khẩu..."
                                } else {
                                    "Đổi mật khẩu"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        if (state.passwordError.isNotBlank()) {
                            Text(
                                text = state.passwordError,
                                color = Color(0xFFFF6B6B),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- NÚT ĐĂNG XUẤT ---
                TextButton(
                    onClick = {
                        viewModel.logout(onLogoutSuccess = onLogout)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (state.showUpdateSuccessDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissUpdateSuccessDialog() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryPink
                    )
                },
                title = {
                    Text(
                        text = "Cập nhật thành công",
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = "Thông tin cá nhân đã được cập nhật.",
                        color = Color.Gray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.dismissUpdateSuccessDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPink
                        )
                    ) {
                        Text("OK")
                    }
                },
                containerColor = DarkCard
            )
        }

        if (state.showChangePasswordSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.dismissChangePasswordSuccessDialog()
                    oldPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryPink
                    )
                },
                title = {
                    Text(
                        text = "Đổi mật khẩu thành công",
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = "Mật khẩu của bạn đã được cập nhật.",
                        color = Color.Gray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.dismissChangePasswordSuccessDialog()
                            oldPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
                    ) {
                        Text("OK")
                    }
                },
                containerColor = DarkCard
            )
        }

        if (showBirthdayDatePicker) {
            val initialDateMillis = parseBirthdayToMillis(tempBirthday)
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialDateMillis
            )

            DatePickerDialog(
                onDismissRequest = { showBirthdayDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedMillis ->
                                tempBirthday = formatBirthdayFromMillis(selectedMillis)
                            }
                            showBirthdayDatePicker = false
                        },
                        enabled = datePickerState.selectedDateMillis != null,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
                    ) {
                        Text("Xác nhận")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBirthdayDatePicker = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = DarkCard)
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = DarkCard,
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = Color.Gray,
                        subheadContentColor = Color.Gray,
                        dayContentColor = Color.White,
                        disabledDayContentColor = Color.Gray.copy(alpha = 0.4f),
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = PrimaryPink,
                        todayContentColor = PrimaryPink,
                        todayDateBorderColor = PrimaryPink,
                        yearContentColor = Color.White,
                        selectedYearContentColor = Color.White,
                        selectedYearContainerColor = PrimaryPink,
                        currentYearContentColor = PrimaryPink,
                        navigationContentColor = PrimaryPink
                    )
                )
            }
        }

        // Session Expired Dialog
        if (state.showSessionExpiredDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PrimaryPink
                    )
                },
                title = {
                    Text(
                        text = "Phiên đăng nhập đã hết hạn",
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = "Vui lòng đăng nhập lại để tiếp tục",
                        color = Color.Gray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onSessionExpiredConfirmed()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPink
                        )
                    ) {
                        Text("Đăng nhập lại")
                    }
                },
                containerColor = DarkCard
            )
        }
    }
}

private fun parseBirthdayToMillis(rawDate: String): Long? {
    if (rawDate.isBlank()) return null

    return runCatching {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
            isLenient = false
        }
        formatter.parse(rawDate)?.time
    }.getOrNull()
}

private fun formatBirthdayFromMillis(dateMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(dateMillis))
}
