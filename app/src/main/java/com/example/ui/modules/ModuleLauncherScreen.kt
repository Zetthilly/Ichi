package com.example.ui.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.UniversalSendToButton
import com.example.ui.navigation.AppModuleRegistry
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.ModuleLauncherViewModel

@Composable
fun ModuleLauncherScreen(
    launcherViewModel: ModuleLauncherViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    val currentChordModel by launcherViewModel.currentChord.collectAsStateWithLifecycle()
    val globalKey by launcherViewModel.globalKeySignature.collectAsStateWithLifecycle()
    val isPlaying by launcherViewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val uploadedFileName by launcherViewModel.uploadedFileName.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val displayModules = remember(searchQuery) {
        AppModuleRegistry.searchModules(searchQuery).filter { it.id != "launcher" }
    }

    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "MODULE LAUNCHER",
                subtitle = "Dynamic Workspace Hub & Registry",
                moduleIcon = Icons.Default.GridView,
                accentColor = Color(0xFF00E5FF),
                viewModel = sharedViewModel,
                onOpenDrawer = onOpenDrawer,
                onOpenSendToDialog = onOpenSendToDialog
            )
        },
        containerColor = Color(0xFF030814)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("module_launcher_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Cyber Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF071122),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0C2038),
                                        Color(0xFF040A18)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🎛️", fontSize = 20.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "HZ CHORD AI CONSOLE",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "21+ Full-Screen Workspaces & Realtime Audio Engine",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF00E5FF)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isPlaying) Color(0xFF00E5FF) else Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = if (isPlaying) "PLAYING" else "READY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isPlaying) Color(0xFF0A0F1D) else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status Telemetry Pill Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0A1628),
                                border = BorderStroke(1.dp, Color(0xFF132F52)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("ACTIVE FILE", fontSize = 8.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    Text(uploadedFileName ?: "No File Staged", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF241C04),
                                border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("CHORD / KEY", fontSize = 8.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
                                    Text(currentChordModel?.name ?: globalKey ?: "C Maj", fontSize = 11.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Black, maxLines = 1)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F261C),
                                border = BorderStroke(1.dp, Color(0xFF10B981)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("REGISTERED", fontSize = 8.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    Text("${AppModuleRegistry.ALL_MODULES.size - 1} Modules", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Search Input Field inside Launcher
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("🔍 Filter registered modules...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
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
                                .height(46.dp)
                                .testTag("launcher_module_search_input")
                        )
                    }
                }
            }

            // Launcher Header Label
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DYNAMIC MODULE REGISTRY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "${displayModules.size} WORKSPACES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                }
            }

            // Module Cards List
            items(displayModules) { module ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF091426)),
                    border = BorderStroke(1.dp, module.color.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { launcherViewModel.navigateToModule(module.id) }
                        .testTag("launcher_module_card_${module.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = module.color.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, module.color),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = module.emoji, fontSize = 20.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = module.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = module.color.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = module.moduleTag,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = module.color,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = module.subtitle,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Launch Workspace",
                                tint = module.color,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { launcherViewModel.navigateToModule(module.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = module.color.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, module.color),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Launch, contentDescription = null, tint = module.color, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("OPEN WORKSPACE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = module.color)
                                }
                            }

                            UniversalSendToButton(
                                sourceName = "Master Project Payload -> ${module.title}",
                                buttonText = "SEND TO",
                                accentColor = module.color,
                                onOpenSendToDialog = onOpenSendToDialog
                            )
                        }
                    }
                }
            }
        }
    }
}

