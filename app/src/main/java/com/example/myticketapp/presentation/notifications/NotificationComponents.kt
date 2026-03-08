package com.example.myticketapp.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.Notification
import com.example.myticketapp.ui.theme.PrimaryPink

/**
 * Component Cái Chuông (Để nhúng vào HomeTopBar)
 * Hiển thị badge đỏ với số lượng thông báo chưa đọc
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBell(
    viewModel: NotificationViewModel = hiltViewModel(),
    onNavigateToEvent: (Int) -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }
    var showSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .clickable { showSheet = true }
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 6.dp, top = 6.dp)
                    .size(18.dp)
                    .background(Color.Red, CircleShape)
                    .border(1.dp, Color(0xFF0B0E14), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Modal trượt từ dưới lên chứa list thông báo
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            containerColor = Color(0xFF161B22),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            NotificationListSheet(
                notifications = notifications,
                onMarkAllRead = { viewModel.markAllAsRead() },
                onItemClick = { notif ->
                    viewModel.markAsRead(notif.id)
                    showSheet = false
                    notif.eventId.toIntOrNull()?.let { onNavigateToEvent(it) }
                }
            )
        }
    }
}

/**
 * Giao diện List bên trong Bottom Sheet
 */
@Composable
fun NotificationListSheet(
    notifications: List<Notification>,
    onMarkAllRead: () -> Unit,
    onItemClick: (Notification) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thông báo",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (notifications.any { !it.isRead }) {
                Text(
                    text = "Đánh dấu đã đọc",
                    color = PrimaryPink,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onMarkAllRead() }
                )
            }
        }

        Divider(color = Color(0xFF30363D))

        // Empty state
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có thông báo nào.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // List - Chỉ hiển thị 15 cái gần nhất
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(notifications.take(15)) { n ->
                NotificationItem(
                    notification = n,
                    onItemClick = { onItemClick(n) }
                )
            }
        }
    }
}

/**
 * Component một item thông báo
 */
@Composable
fun NotificationItem(
    notification: Notification,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (notification.isRead) Color.Transparent
                else PrimaryPink.copy(alpha = 0.05f)
            )
            .clickable { onItemClick() }
            .padding(vertical = 12.dp)
    ) {
        // Chấm xanh/hồng đánh dấu chưa đọc
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 8.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(if (notification.isRead) Color.Transparent else PrimaryPink)
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.title,
                    color = PrimaryPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryPink, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Mới",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.createdAt,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
    Divider(color = Color(0xFF30363D).copy(alpha = 0.5f))
}
