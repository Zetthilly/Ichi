package com.example.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.WorkstationViewModel

@Composable
fun CyberAppDrawerSheet(
    activeRoute: String,
    viewModel: WorkstationViewModel,
    onNavigate: (route: String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val globalKey by viewModel.globalKeySignature.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val uploadedFileName by viewModel.uploadedFileName.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val filteredModules = remember(searchQuery) { AppModuleRegistry.searchModules(searchQuery) }

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF030814),
        drawerContentColor = Color.White,
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight()
            .testTag("cyber_navigation_drawer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF071122),
                            Color(0xFF030814)
                        )
                    )
                )
        ) {
            // Header Section
            Surface(
                color = Color(0xFF0C192E),
                border = BorderStroke(1.dp, Color(0xFF132F52)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF241C04),
                                border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "HZ CHORD AI",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "DYNAMIC MODULE DRAWER",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E5FF),
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onCloseDrawer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Drawer",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Project Telemetry Box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF070F1C),
                        border = BorderStroke(1.dp, Color(0xFF132F52)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE PROJECT",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = uploadedFileName ?: "Demo Project Session",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF241C04),
                                border = BorderStroke(1.dp, Color(0xFFD4AF37))
                            ) {
                                Text(
                                    text = currentChordModel?.name ?: globalKey ?: "C Maj",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD4AF37),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Search Modules Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "🔍 Search 21+ modules...",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF030814),
                            unfocusedContainerColor = Color(0xFF030814),
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF132F52),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("drawer_module_search_input")
                    )
                }
            }

            Divider(color = Color(0xFF132F52))

            // Scrollable Dynamic Module List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (filteredModules.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No modules found matching '$searchQuery'",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    filteredModules.forEach { item ->
                        val isSelected = activeRoute.equals(item.id, ignoreCase = true)
                        val itemBg = if (isSelected) item.color.copy(alpha = 0.18f) else Color.Transparent
                        val itemBorder = if (isSelected) BorderStroke(1.dp, item.color) else null

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = itemBg,
                            border = itemBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onNavigate(item.id)
                                    onCloseDrawer()
                                }
                                .testTag("drawer_item_${item.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) item.color.copy(alpha = 0.3f) else Color(0xFF0F1C2E),
                                    border = BorderStroke(1.dp, if (isSelected) item.color else Color(0xFF1B3150)),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.emoji,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = item.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(3.dp),
                                            color = item.color.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = item.moduleTag,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = item.color,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = item.subtitle,
                                        fontSize = 8.5.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = Color(0xFF132F52))

            // Footer Quick Transport Controls
            Surface(
                color = Color(0xFF070F1C),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isPlaying) Color(0xFF00E5FF) else Color(0xFF1E293B),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { viewModel.toggleMasterPlayback() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Playback",
                                    tint = if (isPlaying) Color(0xFF0A0F1D) else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = if (isPlaying) "MASTER BUS PLAYING" else "ENGINE READY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPlaying) Color(0xFF00E5FF) else Color(0xFF10B981)
                            )
                            Text(
                                text = "OBOE LOW LATENCY (3.2 ms)",
                                fontSize = 7.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onNavigate("launcher")
                            onCloseDrawer()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("LAUNCHER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    }
                }
            }
        }
    }
}

