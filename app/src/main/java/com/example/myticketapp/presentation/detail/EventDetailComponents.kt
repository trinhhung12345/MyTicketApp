package com.example.myticketapp.presentation.detail

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.example.myticketapp.ui.theme.PrimaryPink
import kotlinx.coroutines.launch

// 1. Component hiển thị Video Youtube qua IFrame
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubePlayer(youtubeUrl: String, modifier: Modifier = Modifier) {
    // Trích xuất Video ID từ URL (Ví dụ: watch?v=ABCDEF -> ABCDEF)
    val videoId = youtubeUrl.substringAfter("v=").substringBefore("&")
    val embedHtml = """
        <html><body style="margin:0;padding:0;">
        <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoId?autoplay=0&controls=1" frameborder="0" allowfullscreen></iframe>
        </body></html>
    """.trimIndent()

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                loadDataWithBaseURL(null, embedHtml, "text/html", "utf-8", null)
            }
        }
    )
}

// 2. Component render HTML an toàn sang TextView
@Composable
fun HtmlText(html: String, color: Color = Color.LightGray) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context -> TextView(context) },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT); it.setTextColor(android.graphics.Color.LTGRAY) }
    )
}

// 3. Component Gallery y hệt ảnh bạn đưa
@Composable
fun EventGallery(imageUrls: List<String>) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    val coroutineScope = rememberCoroutineScope()
    var showFullScreen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Hình ảnh", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        // Main Pager Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { showFullScreen = true }
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Mũi tên trái
            IconButton(
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                modifier = Modifier.align(Alignment.CenterStart).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape),
                enabled = pagerState.currentPage > 0
            ) { Icon(Icons.Default.ChevronLeft, "Prev", tint = Color.White) }

            // Mũi tên phải
            IconButton(
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape),
                enabled = pagerState.currentPage < imageUrls.size - 1
            ) { Icon(Icons.Default.ChevronRight, "Next", tint = Color.White) }

            // Indicator (VD: 2/4)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("${pagerState.currentPage + 1} / ${imageUrls.size}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thumbnail row bên dưới
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            imageUrls.forEachIndexed { index, url ->
                val isSelected = pagerState.currentPage == index
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) PrimaryPink else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
                )
            }
        }
    }

    if (showFullScreen) {
        FullScreenImageDialog(
            imageUrls = imageUrls,
            initialIndex = pagerState.currentPage,
            onDismiss = { showFullScreen = false }
        )
    }
}

@Composable
fun FullScreenImageDialog(
    imageUrls: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { imageUrls.size })

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Để dialog chiếm toàn màn hình
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = null,
                    contentScale = ContentScale.Fit, // Hiện toàn bộ ảnh không cắt
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) { } // Để click vào ảnh không bị đóng dialog nhầm
                )
            }

            // Nút đóng
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
