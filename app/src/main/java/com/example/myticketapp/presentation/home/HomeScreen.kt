package com.example.myticketapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myticketapp.ui.theme.PrimaryPink

// Mock Data để giao diện hiển thị ảnh
val mockImg1 =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuB-wjOFidXrpqO4X2h7cTRYlEhMjOSF9VGSJHD2Zj4NfCm9zzxidEJtrmb0c_3lzgEZqkeFAVK4qcYpHWR-dd4uBS9eQUr7bGP24yrLEbBxrx_DMebzzJXUcZcDtaHtPZcfuLwL_rMYllcpRpQNCmy0iidVdf6E-a-mCy0KMU9lnEO2DKaWqLsQiqzuR83TjsKSJA6I9Xyiq_Zvb0naI_rpOCZsPMHhobDjE9Pfx9nxNQF4n0q92VfJvQC8oclOg_8iwOzaG9rkVQ8"
val mockImg2 =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCUmNv0uDCseAHhSAjjDPErFtyM__2JJlN9JE-RsDXSBo77CEVpNSf627KYDBMJx3VoGHJ_9htQ69yQdVX69f21qQgbhzT156mqTibhUEspEMUZqv7i7Db-a8e7BLjRjzi192gvPO1GYEA618_5apNA4C7AwAHl_Kf-c3tGpmZE3Yc9ymjgIZfvJLAs5C8GPmPGXiA3MdpAmvQEOqF-YqW7vBqyI7lFHBxpBFE3CnIWjlkEHo_Lwbph-dIS1PXSegy8Bpy1_TqDN00"

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf("Tất cả") }
    val categories = listOf("Tất cả", "POP", "Rock", "EDM", "Healing", "Workshop")

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Categories
            item {
                CategoryTabs(categories, selectedTab) { selectedTab = it }
            }

            // 2. Featured Events (Banner lướt ngang)
            item {
                SectionHeader("Sự kiện nổi bật")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        FeaturedEventCard(
                            imageUrl = mockImg1,
                            title = "Bang Bang Bang (Big Bang is back in VN) 2026",
                            date = "23/04/2026",
                            price = "Từ 10.000 đ",
                            tag = "MỚI NHẤT",
                            tagColor = PrimaryPink
                        )
                    }
                    item {
                        FeaturedEventCard(
                            imageUrl = mockImg2,
                            title = "Alan Walker 1st Stream in Viet Nam",
                            date = "28/03/2026",
                            price = "Từ 10.000 đ",
                            tag = "SẮP DIỄN RA",
                            tagColor = Color.Blue
                        )
                    }
                }
            }

            // 3. Sự kiện đặc sắc (Grid 2x2)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("Sự kiện đặc sắc", showViewAll = false)

                // Grid 2x2
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SquareEventCard(
                            imageUrl = mockImg1,
                            title = "Ali to Canada Tour",
                            subtitle = "Snoop Dogg & Friends",
                            modifier = Modifier.weight(1f)
                        )
                        SquareEventCard(
                            imageUrl = mockImg2,
                            title = "Thăng Long Show",
                            subtitle = "Hà Nội Opera House",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SquareEventCard(
                            imageUrl = mockImg1,
                            title = "Red Velvet Cake Workshop",
                            subtitle = "District 1, HCMC",
                            modifier = Modifier.weight(1f)
                        )
                        SquareEventCard(
                            imageUrl = mockImg2,
                            title = "The Eras Tour Vinyl",
                            subtitle = "Exhibition Center",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. Các mục List ngang khác (POP, Rock, EDM)
            item { EventHorizontalList("POP", mockImg1) }
            item { EventHorizontalList("ROCK", mockImg2) }
            item { EventHorizontalList("EDM", mockImg1) }
        }
    }
}

// Component phụ để sinh ra các list ngang (POP, Rock...)
@Composable
fun EventHorizontalList(title: String, sampleImage: String) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        SectionHeader(title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(4) { index ->
                SquareEventCard(
                    imageUrl = sampleImage,
                    title = "$title Event ${index + 1}",
                    subtitle = "Sân vận động quốc gia"
                )
            }
        }
    }
}
