package com.example.myticketapp.presentation.event_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.ui.theme.PrimaryPink

val DarkBg = Color(0xFF0B0E14)
val DarkCard = Color(0xFF161B22)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    onBackClick: () -> Unit,
    onEventClick: (Int) -> Unit,
    viewModel: EventListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text(viewModel.categoryName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            if (state.isLoading) {
                CircularProgressIndicator(color = PrimaryPink, modifier = Modifier.align(Alignment.Center))
            }
            else if (state.error.isNotBlank()) {
                Text(state.error, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            }
            else if (state.events.isEmpty()) {
                Text("Không có sự kiện nào trong mục này.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            }
            else {
                // Hiển thị lưới 2 cột
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.events) { event ->
                        GridEventCard(event = event, onClick = { onEventClick(event.id) })
                    }
                }
            }
        }
    }
}

// Component Thẻ hiển thị dạng Grid (Cao hơn SquareEventCard một chút để chứa Giá tiền)
@Composable
fun GridEventCard(event: Event, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Ảnh vuông
        Box(
            modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                .background(DarkCard).border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = event.thumbnailUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Tag Category (e.g. POP)
            Box(modifier = Modifier.padding(8.dp).background(PrimaryPink.copy(alpha = 0.9f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text(event.categoryName, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Thông tin text
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(event.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(event.startDate, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(event.minPrice.formatVND(), color = PrimaryPink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}