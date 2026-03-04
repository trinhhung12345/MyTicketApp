package com.example.myticketapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.ui.theme.PrimaryPink
import java.text.DecimalFormat
import kotlinx.coroutines.delay

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
fun HomeScreen(
    onEventClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
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
                                tagColor = PrimaryPink,
                                modifier = Modifier.clickable { onEventClick(event.id) }
                            )
                        }
                    }
                }
            }

            // 3. Sự kiện Đặc Sắc (Carousel)
            if (state.specialEvents.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SpecialEventsCarousel(events = state.specialEvents, onEventClick = onEventClick)
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
                                    subtitle = event.venue,
                                    modifier = Modifier.clickable { onEventClick(event.id) }
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
private fun SpecialEventsCarousel(
    events: List<Event>,
    onEventClick: (Int) -> Unit
) {
    val pages = remember(events) { events.chunked(2) }
    val pageCount = pages.size
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }
    var isForward by remember(pageCount) { mutableStateOf(true) }

    LaunchedEffect(pageCount) {
        if (pageCount <= 1) return@LaunchedEffect

        while (true) {
            delay(3000)

            val currentPage = pagerState.currentPage
            val lastPage = pageCount - 1

            val nextPage = if (isForward) {
                if (currentPage >= lastPage) {
                    isForward = false
                    (currentPage - 1).coerceAtLeast(0)
                } else {
                    currentPage + 1
                }
            } else {
                if (currentPage <= 0) {
                    isForward = true
                    (currentPage + 1).coerceAtMost(lastPage)
                } else {
                    currentPage - 1
                }
            }

            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Sự kiện đặc sắc", showViewAll = false)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                pages[page].forEach { event ->
                    SquareEventCard(
                        imageUrl = event.thumbnailUrl,
                        title = event.title,
                        subtitle = event.venue,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onEventClick(event.id) }
                    )
                }

                if (pages[page].size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        if (pageCount > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val color = if (pagerState.currentPage == index) PrimaryPink else Color(0xFF374151)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .background(color = color, shape = CircleShape)
                    )
                }
            }
        }
    }
}

