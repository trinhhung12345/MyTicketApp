package com.example.myticketapp.presentation.booking

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myticketapp.domain.model.Section
import com.example.myticketapp.presentation.home.formatVND
import com.example.myticketapp.ui.theme.PrimaryPink

// Use theme colors instead of redefining (DarkBg and DarkCard are in Color.kt)
private val BgColor = Color(0xFF0B0E14)
private val CardColor = Color(0xFF161B22)
private val StageColor = Color(0xFFFFA500)

@Composable
fun BookingScreen(
    onBackClick: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    var clickedSection by remember { mutableStateOf<Section?>(null) }
    var isCartExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.uiEvent) {
        if (state.uiEvent.isNotEmpty()) {
            Toast.makeText(context, state.uiEvent, Toast.LENGTH_SHORT).show()
            viewModel.clearUiEvent()
        }
    }

    // Find the name of the section currently locked in cart
    val currentSectionName = remember(state.currentSectionId, state.seatMap) {
        state.seatMap?.sections?.find { it.id == state.currentSectionId }?.name ?: ""
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            BookingTopBar(
                eventName = viewModel.eventName,
                showingTime = viewModel.showingTime,
                cartCount = state.selectedSeats.size,
                onBackClick = onBackClick,
                onCartClick = {
                    if (state.selectedSeats.isNotEmpty()) isCartExpanded = !isCartExpanded
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.selectedSeats.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                ExpandableCartBottomBar(
                    state = state,
                    currentSectionName = currentSectionName,
                    isExpanded = isCartExpanded,
                    onToggleExpand = { isCartExpanded = !isCartExpanded },
                    onRemoveSeat = { viewModel.removeSeat(it) },
                    onCheckout = { /* TODO: Navigate to checkout */ }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = PrimaryPink,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error.isNotEmpty() -> {
                    Text(
                        text = state.error,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                state.seatMap != null -> {
                    ZoomableSeatMap(
                        viewboxWidth = state.seatMap.viewboxWidth,
                        viewboxHeight = state.seatMap.viewboxHeight,
                        sections = state.seatMap.sections,
                        selectedSeatIds = state.selectedSeats.map { it.id }.toSet(),
                        onSectionClick = { section ->
                            if (!section.isStage) clickedSection = section
                        }
                    )

                    // Section legend bottom-left
                    SectionLegend(
                        sections = state.seatMap.sections.filter { !it.isStage },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 12.dp, bottom = 12.dp)
                    )

                    // Zoom hint overlay
                    ZoomHint(
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }

    // Seat selection dialog
    clickedSection?.let { section ->
        SeatSelectionDialog(
            eventName = viewModel.eventName,
            showingTime = viewModel.showingTime,
            section = section,
            selectedSeats = state.selectedSeats.toSet(),
            onSeatToggle = { seat -> viewModel.toggleSeat(seat, section) },
            onDismiss = { clickedSection = null }
        )
    }
}

// ── TOP BAR ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingTopBar(
    eventName: String,
    showingTime: String,
    cartCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CardColor),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Color.White)
            }
        },
        title = {
            Column {
                Text(
                    eventName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(showingTime, color = Color.Gray, fontSize = 12.sp)
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable { onCartClick() }
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, "Giỏ hàng", tint = Color.White)
                }
                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp)
                            .size(18.dp)
                            .background(PrimaryPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            cartCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}

// ── ZOOMABLE SEAT MAP ─────────────────────────────────────────────────────────

@Composable
fun ZoomableSeatMap(
    viewboxWidth: Float,
    viewboxHeight: Float,
    sections: List<Section>,
    selectedSeatIds: Set<Int>,
    onSectionClick: (Section) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Use a lambda holder so the Canvas recomposes when scale/offset change,
    // and tap detection always reads the latest values.
    val currentScale by rememberUpdatedState(scale)
    val currentOffset by rememberUpdatedState(offset)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(sections) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var isDragging = false

                    do {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }

                        if (pressed.size >= 2) {
                            isDragging = true
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()
                            scale = (scale * zoomChange).coerceIn(0.4f, 6f)
                            offset += panChange
                            event.changes.forEach { it.consume() }
                        } else if (pressed.size == 1) {
                            val change = event.changes.first()
                            val moved = (change.position - down.position).getDistance()
                            if (moved > viewConfiguration.touchSlop || isDragging) {
                                isDragging = true
                                offset += change.position - change.previousPosition
                                change.consume()
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    if (!isDragging) {
                        // Tap detected — convert screen coords to viewbox coords
                        // Drawing uses: screenX = viewboxX * currentScale + currentOffset.x
                        // So:           viewboxX = (screenX - currentOffset.x) / currentScale
                        val tapX = (down.position.x - currentOffset.x) / currentScale
                        val tapY = (down.position.y - currentOffset.y) / currentScale

                        android.util.Log.d("SeatMap", "TAP screen=(${down.position.x}, ${down.position.y}) → viewbox=($tapX, $tapY) scale=$currentScale offset=$currentOffset")

                        sections.forEach { sec ->
                            val a = sec.attribute
                            android.util.Log.d("SeatMap", "  Section '${sec.name}': x=${a.x}..${a.x + a.width}, y=${a.y}..${a.y + a.height}, isStage=${sec.isStage}")
                        }

                        val hitSection = sections.firstOrNull { sec ->
                            val a = sec.attribute
                            tapX >= a.x && tapX <= a.x + a.width &&
                                tapY >= a.y && tapY <= a.y + a.height
                        }

                        android.util.Log.d("SeatMap", "  HIT: ${hitSection?.name ?: "NONE"}")
                        hitSection?.let { onSectionClick(it) }
                    }
                }
            }
    ) {
        // Draw everything with manual transform (no graphicsLayer)
        // screenCoord = viewboxCoord * scale + offset
        val s = currentScale
        val ox = currentOffset.x
        val oy = currentOffset.y

        sections.forEach { section ->
            val attr = section.attribute
            val fillColor = if (section.isStage) StageColor else Color(attr.color)
            val borderColor = if (section.isStage) Color(0xFFCC8800) else Color.White.copy(alpha = 0.6f)

            val left = attr.x * s + ox
            val top = attr.y * s + oy
            val w = attr.width * s
            val h = attr.height * s

            drawRect(
                color = fillColor,
                topLeft = Offset(left, top),
                size = Size(w, h)
            )
            drawRect(
                color = borderColor,
                topLeft = Offset(left, top),
                size = Size(w, h),
                style = Stroke(width = 2f)
            )

            // Label
            val label = if (section.isStage) "STAGE" else section.name
            val paint = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                color = android.graphics.Color.WHITE
                textSize = (h * 0.22f).coerceIn(12f, 48f)
                isFakeBoldText = true
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                label,
                left + w / 2f,
                top + h / 2f + paint.textSize / 3f,
                paint
            )
        }
    }
}

@Composable
private fun ZoomHint(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        visible = false
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.ZoomIn, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Text("Chụm/kéo để thu phóng & di chuyển", color = Color.White, fontSize = 11.sp)
            }
        }
    }
}

// ── SECTION LEGEND ────────────────────────────────────────────────────────────

@Composable
fun SectionLegend(sections: List<Section>, modifier: Modifier = Modifier) {
    if (sections.isEmpty()) return
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        sections.distinctBy { it.attribute.color }.forEach { section ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(section.attribute.color), RoundedCornerShape(3.dp))
                )
                Text(section.name, color = Color.White, fontSize = 11.sp)
            }
        }
    }
}

// ── SEAT SELECTION DIALOG (Visual Grid) ───────────────────────────────────────

@Composable
fun SeatSelectionDialog(
    eventName: String,
    showingTime: String,
    section: Section,
    selectedSeats: Set<com.example.myticketapp.domain.model.Seat>,
    onSeatToggle: (com.example.myticketapp.domain.model.Seat) -> Unit,
    onDismiss: () -> Unit
) {
    // Group seats by row for grid layout
    val seatsByRow = remember(section.seats) {
        section.seats.sortedWith(compareBy({ it.rowIndex }, { it.colIndex }))
            .groupBy { it.rowIndex }
            .toSortedMap()
    }
    val maxCols = remember(section.seats) {
        section.seats.maxOfOrNull { it.colIndex } ?: 1
    }
    val selectedInSection = remember(selectedSeats, section.seats) {
        selectedSeats.filter { sel -> section.seats.any { it.id == sel.id } }
            .sortedWith(compareBy({ it.rowIndex }, { it.colIndex }))
    }
    val selectedCount = selectedInSection.size

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.82f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F172A))
                    .clickable(enabled = false) {} // block click-through
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Header ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Event name
                        Text(
                            text = eventName,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Showing time
                        Text(
                            text = showingTime,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(8.dp))
                        // Section name
                        Text(
                            text = section.name,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Price badge
                            Surface(
                                color = PrimaryPink.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = section.seats.firstOrNull()?.price?.formatVND()?.replace("Từ ", "") ?: "—",
                                    color = PrimaryPink,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            // Separator
                            Box(
                                Modifier
                                    .size(4.dp)
                                    .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                            )
                            // Max tickets
                            Text(
                                "Tối đa ${section.maxQtyPerOrder} vé",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                            // Separator
                            Box(
                                Modifier
                                    .size(4.dp)
                                    .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                            )
                            // Available seats count
                            val availableCount = section.seats.count {
                                it.status.lowercase() !in listOf("sold", "unavailable", "booked")
                            }
                            Text(
                                "Còn $availableCount ghế",
                                color = Color(0xFF4ADE80),
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Close, "Đóng",
                            tint = PrimaryPink,
                            modifier = Modifier
                                .border(1.5.dp, PrimaryPink, CircleShape)
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Mini stage indicator ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(28.dp)
                        .background(
                            StageColor.copy(alpha = 0.3f),
                            RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                        )
                        .border(
                            1.dp,
                            StageColor.copy(alpha = 0.5f),
                            RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SÂN KHẤU", color = StageColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                // ── Seat status legend ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeatLegendItem(color = Color(0xFF2C3E50), label = "Trống")
                    Spacer(Modifier.width(14.dp))
                    SeatLegendItem(color = PrimaryPink, label = "Đã chọn")
                    Spacer(Modifier.width(14.dp))
                    SeatLegendItem(color = Color(0xFF3A3A3A), label = "Đã bán")
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(Modifier.height(12.dp))

                // ── Seat Grid (scrollable) ──
                val horizontalScrollState = rememberScrollState()
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    seatsByRow.forEach { (rowIndex, seatsInRow) ->
                        item(key = "row_$rowIndex") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .horizontalScroll(horizontalScrollState)
                            ) {
                                // Row label
                                Text(
                                    text = "R$rowIndex",
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(26.dp)
                                )

                                // Seat cells for this row
                                (1..maxCols).forEach { colIdx ->
                                    val seat = seatsInRow.find { it.colIndex == colIdx }
                                    if (seat != null) {
                                        val isSelected = selectedSeats.any { it.id == seat.id }
                                        val isSold = seat.status.lowercase() in listOf("sold", "unavailable", "booked")

                                        SeatCell(
                                            code = seat.code,
                                            isSelected = isSelected,
                                            isSold = isSold,
                                            onClick = {
                                                if (!isSold) onSeatToggle(seat)
                                            }
                                        )
                                    } else {
                                        // Empty slot (no seat at this position)
                                        Spacer(Modifier.size(36.dp).padding(2.dp))
                                    }
                                }

                                // Mirror row label
                                Text(
                                    text = "R$rowIndex",
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(26.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(Modifier.height(12.dp))

                // ── Bottom summary ──
                Column {
                    if (selectedCount > 0) {
                        // Selected seats detail chips
                        val seatCodes = selectedInSection.map { it.code }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            seatCodes.forEach { code ->
                                Surface(
                                    color = PrimaryPink.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = code,
                                        color = PrimaryPink,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Đã chọn: $selectedCount ghế",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (selectedCount > 0) {
                                val totalInSection = selectedInSection.sumOf { it.price }
                                Text(
                                    totalInSection.formatVND().replace("Từ ", ""),
                                    color = PrimaryPink,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Xong", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ── Single Seat Cell ──────────────────────────────────────────────────────────

@Composable
private fun SeatCell(
    code: String,
    isSelected: Boolean,
    isSold: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> PrimaryPink
        isSold -> Color(0xFF3A3A3A)
        else -> Color(0xFF2C3E50)
    }
    val borderColor = when {
        isSelected -> PrimaryPink.copy(alpha = 0.8f)
        else -> Color.Transparent
    }
    val textColor = when {
        isSold -> Color.Gray.copy(alpha = 0.5f)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(32.dp)
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .then(if (!isSold) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        } else {
            // Show last part of code (e.g. "01" from "K1-01")
            val label = code.substringAfterLast("-").ifEmpty { code.takeLast(2) }
            Text(
                text = label,
                color = textColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SeatLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

// ── EXPANDABLE CART BOTTOM BAR ────────────────────────────────────────────────

@Composable
fun ExpandableCartBottomBar(
    state: BookingState,
    currentSectionName: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onRemoveSeat: (com.example.myticketapp.domain.model.Seat) -> Unit,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Tránh bị navigation bar/control bar đè
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(CardColor)
            .padding(16.dp)
    ) {
        // Drag indicator
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .background(Color.Gray.copy(alpha = 0.4f), CircleShape)
                .align(Alignment.CenterHorizontally)
                .clickable { onToggleExpand() }
        )
        Spacer(Modifier.height(12.dp))

        // Summary row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryPink.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        null,
                        tint = PrimaryPink,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "${state.selectedSeats.size} vé · $currentSectionName",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        state.totalPrice.formatVND().replace("Từ ", ""),
                        color = PrimaryPink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                null,
                tint = Color.White
            )
        }

        // Expandable seat list
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.selectedSeats) { seat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF2C3440), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    seat.code,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    currentSectionName,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    seat.price.formatVND().replace("Từ ", ""),
                                    color = PrimaryPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFF2C3440), CircleShape)
                                        .clickable { onRemoveSeat(seat) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onCheckout,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Tiếp tục thanh toán →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
