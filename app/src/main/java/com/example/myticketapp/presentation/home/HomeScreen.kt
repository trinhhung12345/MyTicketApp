package com.example.myticketapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.ui.theme.PrimaryPink
import java.text.DecimalFormat

// Hàm hỗ trợ format tiền tệ
fun Long.formatVND(): String {
    val formatter = DecimalFormat("###,###,###")
    return "Từ ${formatter.format(this)} đ"
}

// Mock Data để giao diện hiển thị ảnh
val mockImg1 =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuB-wjOFidXrpqO4X2h7cTRYlEhMjOSF9VGSJHD2Zj4NfCm9zzxidEJtrmb0c_3lzgEZqkeFAVK4qcYpHWR-dd4uBS9eQUr7bGP24yrLEbBxrx_DMebzzJXUcZcDtaHtPZcfuLwL_rMYllcpRpQNCmy0iidVdf6E-a-mCy0KMU9lnEO2DKaWqLsQiqzuR83TjsKSJA6I9Xyiq_Zvb0naI_rpOCZsPMHhobDjE9Pfx9nxNQF4n0q92VfJvQC8oclOg_8iwOzaG9rkVQ8"
val mockImg2 =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCUmNv0uDCseAHhSAjjDPErFtyM__2JJlN9JE-RsDXSBo77CEVpNSf627KYDBMJx3VoGHJ_9htQ69yQdVX69f21qQgbhzT156mqTibhUEspEMUZqv7i7Db-a8e7BLjRjzi192gvPO1GYEA618_5apNA4C7AwAHl_Kf-c3tGpmZE3Yc9ymjgIZfvJLAs5C8GPmPGXiA3MdpAmvQEOqF-YqW7vBqyI7lFHBxpBFE3CnIWjlkEHo_Lwbph-dIS1PXSegy8Bpy1_TqDN00"

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    var selectedTab by remember { mutableStateOf("Tất cả") }

    Scaffold(
        containerColor = DarkBg,
        topBar = { HomeTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Mở chat */ },
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Chat"
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkCard.copy(alpha = 0.95f),
                contentColor = Color.Gray
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Trang chủ") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPink,
                        selectedTextColor = PrimaryPink,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Tìm kiếm") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.ConfirmationNumber, null) },
                    label = { Text("Vé của tôi") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Cá nhân") }
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPink)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Categories
            item {
                CategoryTabs(
                    categories = state.categories.map { it.name },
                    selectedCategory = selectedTab,
                    onSelect = { selectedTab = it }
                )
            }

            // 2. Sự kiện Nổi Bật (Banner)
            if (state.featuredEvents.isNotEmpty()) {
                item {
                    SectionHeader("Sự kiện nổi bật")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.featuredEvents) { event ->
                            FeaturedEventCard(
                                imageUrl = event.thumbnailUrl,
                                title = event.title,
                                date = event.startDate,
                                price = event.minPrice.formatVND(),
                                tag = "HOT",
                                tagColor = PrimaryPink
                            )
                        }
                    }
                }
            }

            // 3. Sự kiện Đặc Sắc (Grid)
            if (state.specialEvents.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader("Sự kiện đặc sắc", showViewAll = false)
                    
                    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Chia list thành các cặp 2 phần tử để vẽ thành dòng (Row)
                        val chunkedEvents = state.specialEvents.chunked(2)
                        chunkedEvents.forEach { rowEvents ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                rowEvents.forEach { event ->
                                    SquareEventCard(
                                        imageUrl = event.thumbnailUrl,
                                        title = event.title,
                                        subtitle = event.venue,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Nếu dòng lẻ chỉ có 1 phần tử, thêm Spacer để lấp khoảng trống
                                if (rowEvents.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 4. Các mục danh sách ngang theo TỪNG CATEGORY (VD: POP, Rock...)
            state.eventsByCategory.forEach { (categoryName, events) ->
                item {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        SectionHeader(categoryName)
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(events) { event ->
                                SquareEventCard(
                                    imageUrl = event.thumbnailUrl,
                                    title = event.title,
                                    subtitle = event.venue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

