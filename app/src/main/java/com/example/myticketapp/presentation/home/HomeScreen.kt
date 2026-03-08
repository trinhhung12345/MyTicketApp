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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.Event
import java.text.DecimalFormat
import kotlinx.coroutines.delay

// Hàm hỗ trợ format tiền tệ
fun Long.formatVND(): String {
    val formatter = DecimalFormat("###,###,###")
    return "Từ ${formatter.format(this)} đ"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEventClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onSessionExpired: () -> Unit,
    onCategoryViewAllClick: (Int, String) -> Unit,
    onChatClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val searchState by searchViewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf("Tất cả") }
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(
                onProfileClick = onProfileClick,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchViewModel.onSearchQueryChange(it) },
                isSearching = searchState.isLoading,
                searchResults = searchState.results,
                onEventClick = onEventClick
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refreshHome() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // Để tránh bị che bởi BottomBar
                    ) {
                        // 1. Categories
                        item {
                            CategoryTabs(
                                categories = state.categories.map { it.name },
                                selectedCategory = selectedTab,
                                onSelect = { categoryName ->
                                    selectedTab = categoryName
                                    val categoryId = state.categories
                                        .find { it.name == categoryName }
                                        ?.id
                                        ?: 0
                                    onCategoryViewAllClick(categoryId, categoryName)
                                }
                            )
                        }

                        // 2. Sự kiện Nổi Bật (Banner)
                        if (state.featuredEvents.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Sự kiện nổi bật",
                                    onViewAllClick = {
                                        onCategoryViewAllClick(0, "Tất cả sự kiện")
                                    }
                                )
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
                                            tagColor = primaryColor,
                                            modifier = Modifier.clickable { onEventClick(event.id) }
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Sự kiện Đặc Sắc (Carousel)
                        if (state.specialEvents.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 16.dp xuống 8.dp
                                SpecialEventsCarousel(events = state.specialEvents, onEventClick = onEventClick)
                            }
                        }

                        // 4. Các mục danh sách ngang theo TỪNG CATEGORY
                        state.eventsByCategory.forEach { (categoryName, events) ->
                            item {
                                Column(modifier = Modifier.padding(top = 16.dp)) { // Giảm top padding từ 24.dp xuống 16.dp
                                    SectionHeader(
                                        title = categoryName,
                                        showViewAll = true,
                                        onViewAllClick = {
                                            // Tìm categoryId tương ứng với categoryName
                                            val catId = state.categories.find { it.name == categoryName }?.id ?: 0
                                            val catName = categoryName
                                            onCategoryViewAllClick(catId, catName)
                                        }
                                    )
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
        }

        // FAB - Đưa vào Box để nổi lên trên Column
        FloatingActionButton(
            onClick = { onChatClick() },
            containerColor = primaryColor,
            contentColor = colorScheme.onPrimary,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "Chat"
            )
        }

        // Session Expired Dialog
        if (state.showSessionExpiredDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = primaryColor
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
                            onSessionExpired()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
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

@Composable
private fun SpecialEventsCarousel(
    events: List<Event>,
    onEventClick: (Int) -> Unit
) {
    val pages = remember(events) { events.chunked(2) }
    val pageCount = pages.size
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }
    var isForward by remember(pageCount) { mutableStateOf(true) }
    val colorScheme = MaterialTheme.colorScheme

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
        verticalArrangement = Arrangement.spacedBy(8.dp) // Giảm từ 12.dp xuống 8.dp
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
                    val color = if (pagerState.currentPage == index) {
                        colorScheme.primary
                    } else {
                        colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(6.dp) // Giảm size dot từ 8.dp xuống 6.dp
                            .background(color = color, shape = CircleShape)
                    )
                }
            }
        }
    }
}
