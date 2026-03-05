package com.example.myticketapp.presentation.detail

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myticketapp.domain.model.Showing
import com.example.myticketapp.navigation.Screen
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.ui.theme.PrimaryPink
import java.text.SimpleDateFormat
import java.util.Locale

val DarkBg = Color(0xFF0B0E14)
val DarkCard = Color(0xFF161B22)

@Composable
fun EventDetailScreen(
    onBackClick: () -> Unit,
    onTokenExpired: () -> Unit, // Callback văng ra Login
    navController: NavController,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    // Lắng nghe Token Expired
    LaunchedEffect(state.isTokenExpired) {
        if (state.isTokenExpired) {
            Toast.makeText(context, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show()
            onTokenExpired()
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryPink)
        }
        return
    }

    if (state.error.isNotBlank()) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBg), contentAlignment = Alignment.Center) {
            Text(text = state.error, color = Color.Red)
        }
        return
    }

    val event = state.event
    if (event != null) {
        EventDetailContent(event = event, onBackClick = onBackClick, navController = navController)
    }
}

@Composable
private fun EventDetailContent(
    event: com.example.myticketapp.domain.model.Event,
    onBackClick: () -> Unit,
    navController: NavController
) {
    val expandedShowingIds = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<Int>() }

    var showShowingSelection by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // Chừa chỗ cho Bottom Bar + Navigation Bar
        ) {
            // --- 1. HERO SECTION (Video hoặc Banner) ---
            item {
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    val youtubeUrl = event.youtubeUrl
                    if (youtubeUrl != null) {
                        YoutubePlayer(youtubeUrl = youtubeUrl)
                    } else {
                        AsyncImage(
                            model = event.bannerUrl, // Dùng bannerUrl (ảnh số 2)
                            contentDescription = "Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient đen che phủ bớt ảnh cho ngầu
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, DarkBg), startY = 300f)
                            )
                        )
                    }

                    // Nút Back
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .statusBarsPadding() // Tránh bị status bar đè
                            .padding(16.dp)
                            .background(Color.Black.copy(0.4f), CircleShape)
                    ) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                }
            }

            // --- 2. THÔNG TIN CHUNG TÓM TẮT ---
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Badge thể loại
                    Box(modifier = Modifier.background(PrimaryPink.copy(0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(event.categoryName, color = PrimaryPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(event.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 32.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Khối Lịch & Địa điểm
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Ngày & Giờ
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = PrimaryPink,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = event.startDate,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = event.startTime,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Địa điểm
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryPink,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = event.venue,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = event.address,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. GIỚI THIỆU SỰ KIỆN (Render HTML) ---
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Giới thiệu sự kiện", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    HtmlText(html = event.descriptionHtml)
                }
            }

            // --- 4. GALLERY ẢNH (Giao diện chuẩn hình mẫu) ---
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    EventGallery(imageUrls = event.gallery)
                }
            }

            // --- 5. LỊCH DIỄN & GIÁ VÉ ---
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Icon(Icons.Default.CalendarMonth, null, tint = PrimaryPink, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lịch diễn & Giá vé", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Card Suất diễn
                    event.showings.forEach { showing ->
                        val isExpanded = showing.id in expandedShowingIds
                        val arrowRotation by animateFloatAsState(
                            targetValue = if (isExpanded) 180f else 0f,
                            animationSpec = tween(durationMillis = 220),
                            label = "ticketTypeArrowRotation"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCard)
                                .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            // Phần thông tin chính: Thời gian + Giá
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF1F2937), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Schedule, null, tint = PrimaryPink, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = showing.startTime.formatToTime(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = showing.startTime.formatToDate(),
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                // Hiển thị giá nổi bật ở góc phải
                                Text(
                                    text = showing.minPrice.formatVND().replace("Từ ", ""),
                                    color = PrimaryPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFF1F2937), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Phần Action: Xem chi tiết & Nút Mua
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        if (isExpanded) {
                                            expandedShowingIds.remove(showing.id)
                                        } else {
                                            expandedShowingIds.add(showing.id)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (isExpanded) "Ẩn hạng vé" else "Xem hạng vé",
                                        color = Color.LightGray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer { rotationZ = arrowRotation }
                                    )
                                }

                                Button(
                                    onClick = { 
                                        navController.navigate(Screen.Booking.passShowingId(showing.id))
                                    },
                                    enabled = showing.isSalable,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Mua ngay", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(220)),
                                exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(180))
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFF1F2937), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    DropdownMenuContainer {
                                        if (showing.types.isEmpty()) {
                                            Text(
                                                text = "Chưa có hạng vé cho suất này",
                                                color = Color.Gray,
                                                fontSize = 13.sp
                                            )
                                        } else {
                                            showing.types.sortedBy { it.position }.forEach { type ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        modifier = Modifier.weight(1f),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(10.dp)
                                                                .clip(CircleShape)
                                                                .background(type.color.toColorOrDefault())
                                                        )
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Column {
                                                            Text(
                                                                text = type.name.ifBlank { "Hạng vé" },
                                                                color = Color.White,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 14.sp
                                                            )
                                                            Text(
                                                                text = "Còn ${type.remainingQuantity} vé",
                                                                color = Color.Gray,
                                                                fontSize = 12.sp
                                                            )
                                                        }
                                                    }

                                                    Text(
                                                        text = if (type.isFree) "Miễn phí" else type.price.formatVND().replace("Từ ", ""),
                                                        color = PrimaryPink,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // --- 6. STICKY BOTTOM BAR (Nút Đặt vé nổi bám đáy) ---
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DarkBg.copy(alpha = 0.95f))
                .border(1.dp, Color(0xFF1F2937))
                .navigationBarsPadding() // Tránh bị navigation bar/control bar đè
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Giá vé từ", color = Color.Gray, fontSize = 12.sp)
                Text(event.minPrice.formatVND().replace("Từ ", ""), color = PrimaryPink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {
                    if (event.showings.size >= 2) {
                        showShowingSelection = true
                    } else if (event.showings.size == 1) {
                        navController.navigate(Screen.Booking.passShowingId(event.showings[0].id))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(160.dp).height(48.dp)
            ) {
                Text("Đặt vé ngay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showShowingSelection) {
            ShowingSelectionDialog(
                showings = event.showings,
                onDismiss = { showShowingSelection = false },
                onShowingSelected = { showing ->
                    showShowingSelection = false
                    navController.navigate(Screen.Booking.passShowingId(showing.id))
                }
            )
        }
    }
}

@Composable
fun ShowingSelectionDialog(
    showings: List<Showing>,
    onDismiss: () -> Unit,
    onShowingSelected: (Showing) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .clickable(enabled = false) {}
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Chọn suất diễn",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vui lòng chọn suất diễn bạn muốn tham gia",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PrimaryPink,
                            modifier = Modifier.border(1.dp, PrimaryPink, CircleShape).padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(showings) { showing ->
                        val isSalable = showing.isSalable
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E293B))
                                .clickable(enabled = isSalable) { onShowingSelected(showing) }
                                .padding(16.dp)
                                .alpha(if (isSalable) 1f else 0.5f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = showing.startTime.formatToTime(), // Hoặc format range nếu có data
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = showing.startTime.formatToDate(),
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isSalable) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF334155), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "HẾT VÉ",
                                            color = Color.Red.copy(alpha = 0.7f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = PrimaryPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownMenuContainer(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF111827),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF273244)),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), content = content)
    }
}

private fun String.toColorOrDefault(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (_: IllegalArgumentException) {
        PrimaryPink
    }
}

// --- Các hàm tiện ích format ngày giờ ---
fun String.formatToTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.US)
        val date = inputFormat.parse(this)
        if (date != null) outputFormat.format(date) else this
    } catch (e: Exception) {
        if (this.contains("T")) this.substringAfter("T").substringBeforeLast(":") else this
    }
}

fun String.formatToDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN"))
        val date = inputFormat.parse(this)
        if (date != null) {
            outputFormat.format(date)
                .replace("Thứ Hai", "T2")
                .replace("Thứ Ba", "T3")
                .replace("Thứ Tư", "T4")
                .replace("Thứ Năm", "T5")
                .replace("Thứ Sáu", "T6")
                .replace("Thứ Bảy", "T7")
                .replace("Chủ Nhật", "CN")
        } else this
    } catch (e: Exception) {
        if (this.contains("T")) this.substringBefore("T") else this
    }
}
