package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SendToDestination
import com.example.data.SendToTransferEvent
import com.example.data.UniversalSendToRegistry
import com.example.viewmodel.WorkstationViewModel
import kotlinx.coroutines.delay

/**
 * Universal "Send To" Button chip that triggers the Send To routing modal.
 */
@Composable
fun UniversalSendToButton(
    sourceName: String,
    modifier: Modifier = Modifier,
    buttonText: String = "SEND TO...",
    accentColor: Color = Color(0xFF00E5FF),
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Button(
        onClick = { onOpenSendToDialog(sourceName) },
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor.copy(alpha = 0.15f),
            contentColor = accentColor
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = modifier.height(32.dp).testTag("send_to_btn_${sourceName.replace(" ", "_")}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send To Icon",
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = buttonText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Universal "Send To" Modal Dialog allowing seamless zero-copy stream routing
 * to any module across the HZ CHORD AI application.
 */
@Composable
fun UniversalSendToDialog(
    sourceName: String,
    viewModel: WorkstationViewModel,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Guitar & Chords", "Piano & Keys", "Vocals & Lyrics", "Studio & DSP")

    val filteredDestinations = remember(searchQuery, selectedCategoryFilter) {
        UniversalSendToRegistry.destinations.filter { dest ->
            val matchesCategory = (selectedCategoryFilter == "All" || dest.category == selectedCategoryFilter)
            val matchesSearch = dest.name.contains(searchQuery, ignoreCase = true) ||
                    dest.description.contains(searchQuery, ignoreCase = true) ||
                    dest.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF040A17)),
            border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .testTag("universal_send_to_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Title Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Icon",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "UNIVERSAL SEND TO",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "ZERO-COPY MODULE ROUTING ENGINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("send_to_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close dialog",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Source Audio Stream Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A192F)),
                    border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE AUDIO SOURCE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF64748B),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = sourceName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "0 DUPLICATE FILES",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search target module or feature...", fontSize = 11.sp, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF00E5FF)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF081224),
                        unfocusedContainerColor = Color(0xFF081224),
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF1E3A60),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("send_to_search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Tabs Row
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategoryFilter).coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF00E5FF),
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {}
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == selectedCategoryFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00E5FF),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF0D1B2A),
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFF1E3A60),
                                selectedBorderColor = Color(0xFF00E5FF)
                            ),
                            modifier = Modifier.padding(end = 6.dp).testTag("send_to_cat_$cat")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Destination List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredDestinations) { dest ->
                        SendToDestinationCard(
                            destination = dest,
                            onSelect = {
                                viewModel.sendAudioToDestination(sourceName, dest.id)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SendToDestinationCard(
    destination: SendToDestination,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF081224)),
        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("send_to_target_${destination.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF13243C),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = destination.iconEmoji, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = destination.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF00E5FF).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = destination.targetSection.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = destination.description,
                        fontSize = 9.sp,
                        color = Color(0xFF8E9BAE),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select target",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Animated Notification Banner acknowledging completed Universal Send To stream transfer.
 */
@Composable
fun SendToToastNotificationBanner(
    event: SendToTransferEvent,
    onDismiss: () -> Unit
) {
    LaunchedEffect(event) {
        delay(4000)
        onDismiss()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF021B29)),
        border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("send_to_toast_banner")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = event.destination.iconEmoji, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "➡️ STREAM ROUTED TO ${event.destination.name.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF)
                    )
                    Text(
                        text = "${event.sourceName} • Zero-Copy Pointer ${event.sharedMemoryPointer}",
                        fontSize = 8.5.sp,
                        color = Color.White
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close notification",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
