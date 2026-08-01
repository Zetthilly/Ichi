package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.OpenableColumns
import android.net.Uri
import android.app.Activity
import com.example.util.AudioPermissionHelper
import com.example.util.AudioPermissionRationaleDialog
import com.example.util.UniversalAudioMetadataExtractor
import com.example.data.ImportedAudioMetadata
import com.example.data.UniversalAudioSharingState
import com.example.data.SharingModuleStage
import com.example.audio.DetectedChordInfo
import com.example.audio.StemSeparationState
import com.example.data.StemMixerState
import com.example.data.StemChannelData
import com.example.data.ProjectSession
import com.example.ui.recording.RecordingLibraryScreen
import com.example.data.GuitarLick
import com.example.viewmodel.WorkstationViewModel
import com.example.ui.navigation.*
import com.example.ui.modules.*
import com.example.viewmodel.modules.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.MyApplicationTheme
import kotlin.math.sin

@Composable
fun MainWorkstationApp(viewModel: WorkstationViewModel) {
    var showSplash by remember { mutableStateOf(true) }

    // Edge-to-edge window insets
    val systemBarsPadding = WindowInsets.safeDrawing.asPaddingValues()

    // Automatic Splash screen bypass after 3.8 seconds or tap
    LaunchedEffect(Unit) {
        delay(3800)
        showSplash = false
    }

    MyApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showSplash) {
                SplashScreen(
                    onDismiss = { showSplash = false },
                    modifier = Modifier.padding(systemBarsPadding)
                )
            } else {
                WorkstationMainLayout(
                    viewModel = viewModel,
                    modifier = Modifier.padding(systemBarsPadding)
                )
            }
        }
    }
}

// -----------------------------------------------------------------
// 1. PREMIUM ANIMATED SPLASH SCREEN
// -----------------------------------------------------------------
@Composable
fun SplashScreen(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    // Waveform scale animation
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    // Gold core glow pulse
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D2549),
                        Color(0xFF030A16)
                    ),
                    radius = 1200f
                )
            )
            .clickable { onDismiss() } // Bypassable
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Space holder top
        Spacer(modifier = Modifier.height(30.dp))

        // Center branding cluster
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // HZ Chord AI Logo Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .drawBehind {
                        // Background concentric frequency rings
                        drawCircle(
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            radius = size.width * 0.75f * waveScale,
                            style = Stroke(width = 3f)
                        )
                        drawCircle(
                            color = Color(0xFFD4AF37).copy(alpha = 0.25f),
                            radius = size.width * 0.55f * (1.8f - waveScale),
                            style = Stroke(width = 1.5f)
                        )
                    }
                    .background(Color(0xFF0C162A), shape = CircleShape)
                    .border(2.dp, Color(0xFFD4AF37), CircleShape)
            ) {
                // Outer circle glowing core
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "HZ Logo",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(50.dp)
                )
                
                // Overlay spark
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = (16).dp)
                        .background(Color(0xFFD4AF37), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "HZ CHORD AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = Color(0xFFF0F4FC),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle tagline
            Text(
                text = "Hear the Notes. Understand the Music.\nPowered by AI.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = Color(0xFF00E5FF).copy(alpha = glowIntensity),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // Bottom credit and progress
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Dynamic Waveform drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 40.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val count = 40
                    val spacing = size.width / count
                    for (i in 0 until count) {
                        val baseHeight = 5f + (i % 6) * 6f
                        val multiplier = sin((timeSeedScale(i) + waveScale * 4f)) * 20f
                        val currentHeight = (baseHeight + multiplier).coerceAtLeast(4f)
                        drawLine(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF00E5FF), Color(0xFFD4AF37))
                            ),
                            start = Offset(i * spacing, size.height / 2 - currentHeight),
                            end = Offset(i * spacing, size.height / 2 + currentHeight),
                            strokeWidth = 5f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Designed and Built by Joseph Hilary Zulukwa",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Professional Offline Workstation v1.1",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF5E718B),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun timeSeedScale(index: Int): Float {
    return (index.toFloat() * 0.15f)
}

// -----------------------------------------------------------------
// 2. MAIN APPLICATION WORKSTATION LAYOUT
// -----------------------------------------------------------------
@Composable
fun TopBarMetricPill(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF0C1424), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF132F52), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB0BEC5),
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun WorkstationMainTopAppBar(
    viewModel: WorkstationViewModel,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val currentBpm by viewModel.bpm.collectAsStateWithLifecycle()
    val globalKeySignature by viewModel.globalKeySignature.collectAsStateWithLifecycle()
    val currentKey = globalKeySignature ?: (currentChordModel?.root?.let { "$it Major" } ?: "C Major")

    Surface(
        color = Color(0xFF030A16),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Logo and Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "App Logo",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "HZ CHORD AI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = Color(0xFFF0F4FC)
                    )
                }

                // Real-Time Audio Engine Metrics/Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TopBarMetricPill(
                        label = "CHORD",
                        value = currentChordModel?.name ?: "—",
                        color = Color(0xFFD4AF37)
                    )
                    TopBarMetricPill(
                        label = "BPM",
                        value = "$currentBpm",
                        color = Color(0xFF00E5FF)
                    )
                    TopBarMetricPill(
                        label = "KEY",
                        value = currentKey,
                        color = Color(0xFFE040FB)
                    )
                }

                // Toolbar Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onSearchClick, modifier = Modifier.size(36.dp).testTag("top_bar_search")) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFF0F4FC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onNotificationsClick, modifier = Modifier.size(36.dp).testTag("top_bar_alerts")) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFF0F4FC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onSettingsClick, modifier = Modifier.size(36.dp).testTag("top_bar_settings")) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFF0F4FC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Divider(color = Color(0xFF122E54), thickness = 1.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSearchDialog(
    viewModel: WorkstationViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val licks by viewModel.allLicks.collectAsStateWithLifecycle()

    val filteredSessions = remember(query, sessions) {
        if (query.isEmpty()) emptyList()
        else sessions.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.notes.contains(query, ignoreCase = true) ||
                    it.keySignature.contains(query, ignoreCase = true) ||
                    it.detectedChords.contains(query, ignoreCase = true)
        }
    }

    val filteredLicks = remember(query, licks) {
        if (query.isEmpty()) emptyList()
        else licks.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.genre.contains(query, ignoreCase = true) ||
                    it.notes.contains(query, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1424)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF122E54)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "SEARCH WORKSTATION DATABASE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search sessions, licks, keys...", color = Color(0xFF5E718B)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF122E54),
                        focusedContainerColor = Color(0xFF030A16),
                        unfocusedContainerColor = Color(0xFF030A16)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("topbar_search_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (query.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Begin typing to search live sessions & transcribed guitar licks...",
                            fontSize = 11.sp,
                            color = Color(0xFFB0BEC5),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (filteredSessions.isEmpty() && filteredLicks.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No records found matching '$query'",
                            fontSize = 11.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (filteredSessions.isNotEmpty()) {
                            item {
                                Text(
                                    text = "MATCHED SESSIONS (${filteredSessions.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD4AF37)
                                )
                            }
                            items(filteredSessions) { s ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                                    border = BorderStroke(1.dp, Color(0xFF122E54)),
                                    onClick = {
                                        viewModel.setSection("Library")
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Chords: ${s.detectedChords}", fontSize = 11.sp, color = Color(0xFF00E5FF))
                                        Text("Key: ${s.keySignature} • BPM: ${s.bpm}", fontSize = 10.sp, color = Color(0xFFB0BEC5))
                                    }
                                }
                            }
                        }

                        if (filteredLicks.isNotEmpty()) {
                            item {
                                Text(
                                    text = "TRANSCRIPTED LICKS (${filteredLicks.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE040FB)
                                )
                            }
                            items(filteredLicks) { l ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                                    border = BorderStroke(1.dp, Color(0xFF122E54)),
                                    onClick = {
                                        viewModel.setSection("Library")
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(l.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Notes: ${l.notes}", fontSize = 11.sp, color = Color.Green)
                                        Text("Genre: ${l.genre} • Confidence: ${(l.confidence * 100).toInt()}%", fontSize = 10.sp, color = Color(0xFFB0BEC5))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF13233C)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun TopBarNotificationsDialog(
    onDismiss: () -> Unit
) {
    val systemNotifications = remember {
        listOf(
            Triple("Offline Key & Scale Inference Active", "AI Engine calibrated successfully for diatonic scale intervals and modal changes.", "Just Now"),
            Triple("Melodic Arpeggio & Lead Engine Ready", "STFT & autocorrelation models active for fingerstyle guitar, Rhumba & Seben riffs.", "2 min ago"),
            Triple("High-Fidelity Audio Processing Active", "Oboe C++ & Media3 audio engines ready for real-time harmonic transcription & export.", "8 min ago"),
            Triple("Local Database & MIDI Exporter Ready", "Room DB synced for session logging and 16-bit WAV / MIDI export.", "14 min ago")
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1424)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF122E54)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "AI SYSTEM ALERTS & NOTIFICATIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 280.dp)
                ) {
                    items(systemNotifications) { (title, desc, time) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF030A16), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF122E54).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD4AF37)
                                )
                                Text(
                                    text = time,
                                    fontSize = 8.sp,
                                    color = Color(0xFFB0BEC5)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 10.sp,
                                color = Color(0xFFF0F4FC)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF13233C)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun WorkstationMainLayout(viewModel: WorkstationViewModel, modifier: Modifier = Modifier) {
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val lastSendToEvent by viewModel.lastSendToEvent.collectAsStateWithLifecycle()
    val showResumeSessionPrompt by viewModel.showResumeSessionPrompt.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Dedicated Module ViewModels initialized sharing the single WorkstationViewModel
    val launcherViewModel = remember(viewModel) { ModuleLauncherViewModel(viewModel) }
    val analyzerViewModel = remember(viewModel) { RealtimeAnalyzerViewModel(viewModel) }
    val voicingsViewModel = remember(viewModel) { InstrumentVoicingsViewModel(viewModel) }
    val stemViewModel = remember(viewModel) { StemSeparatorViewModel(viewModel) }
    val dspViewModel = remember(viewModel) { StudioDspViewModel(viewModel) }
    val sharingViewModel = remember(viewModel) { UniversalSharingViewModel(viewModel) }
    val earTrainingViewModel = remember(viewModel) { EarTrainingViewModel(viewModel) }

    // Modals trigger states
    var showRecorderModal by remember { mutableStateOf(false) }
    var showAddSessionModal by remember { mutableStateOf(false) }
    var showImportModal by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) }
    var activeSendToSourceName by remember { mutableStateOf<String?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CyberAppDrawerSheet(
                activeRoute = currentSection,
                viewModel = viewModel,
                onNavigate = { route -> viewModel.setSection(route) },
                onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MasterSynchronizedPlaybackBar(
                        viewModel = viewModel,
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                }
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                ) {
                    // Expanded Sub-actions
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            // Action 1: Quick Record
                            ExtendedFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    showRecorderModal = true
                                },
                                containerColor = Color(0xFF260D0D),
                                contentColor = Color(0xFFEF5350),
                                icon = { Icon(Icons.Default.Mic, contentDescription = "Quick Record", modifier = Modifier.size(16.dp)) },
                                text = { Text("Quick Record", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(38.dp)
                            )

                            // Action 2: Quick Analyze
                            ExtendedFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    viewModel.setSection("analyzer")
                                },
                                containerColor = Color(0xFF241C04),
                                contentColor = Color(0xFFD4AF37),
                                icon = { Icon(Icons.Default.InterpreterMode, contentDescription = "Quick Analyze", modifier = Modifier.size(16.dp)) },
                                text = { Text("Quick Analyze", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(38.dp)
                            )

                            // Action 3: Quick Import Audio
                            ExtendedFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    showImportModal = true
                                },
                                containerColor = Color(0xFF04202B),
                                contentColor = Color(0xFF00E5FF),
                                icon = { Icon(Icons.Default.FileOpen, contentDescription = "Quick Import Audio", modifier = Modifier.size(16.dp)) },
                                text = { Text("Import Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(38.dp)
                            )
                        }
                    }

                    // Main Trigger FAB
                    ExtendedFloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                        icon = { Icon(if (isFabExpanded) Icons.Default.Close else Icons.Default.Add, "Action button") },
                        text = { Text(if (isFabExpanded) "Cancel" else "Quick Actions", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                        modifier = Modifier.testTag("quick_action_fab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Section Switcher - Full-Screen Workspace Modules
                when (currentSection) {
                    "launcher" -> ModuleLauncherScreen(
                        launcherViewModel = launcherViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "analyzer", "Analyzer" -> RealtimeAnalyzerModuleScreen(
                        analyzerViewModel = analyzerViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "voicings", "piano", "guitar", "arpeggio" -> InstrumentVoicingsModuleScreen(
                        voicingsViewModel = voicingsViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "stems", "Studio" -> StemSeparatorModuleScreen(
                        stemViewModel = stemViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "dsp" -> StudioDspModuleScreen(
                        dspViewModel = dspViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "sharing" -> UniversalSharingModuleScreen(
                        sharingViewModel = sharingViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "quiz" -> EarTrainingModuleScreen(
                        earTrainingViewModel = earTrainingViewModel,
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "library", "Sessions" -> ProjectLibraryModuleScreen(
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    "settings", "Settings" -> SettingsModuleScreen(
                        sharedViewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                    else -> DedicatedModuleWorkspace(
                        moduleId = currentSection,
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSendToDialog = { activeSendToSourceName = it }
                    )
                }

                // MODALS / DIALOGS
                if (showRecorderModal) {
                    AudioRecorderDialog(
                        viewModel = viewModel,
                        onDismiss = { showRecorderModal = false }
                    )
                }

                if (showAddSessionModal) {
                    AddSessionDialog(
                        viewModel = viewModel,
                        onDismiss = { showAddSessionModal = false }
                    )
                }

                if (showImportModal) {
                    ImportAudioDialog(
                        viewModel = viewModel,
                        onDismiss = { showImportModal = false }
                    )
                }

                if (showSearchDialog) {
                    TopBarSearchDialog(
                        viewModel = viewModel,
                        onDismiss = { showSearchDialog = false }
                    )
                }

                if (showNotificationsDialog) {
                    TopBarNotificationsDialog(
                        onDismiss = { showNotificationsDialog = false }
                    )
                }

                if (showResumeSessionPrompt) {
                    ResumeSessionDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.dismissResumePrompt() }
                    )
                }

                if (showResumeSessionPrompt) {
                    ResumeSessionDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.dismissResumePrompt() }
                    )
                }

                // Universal Send To Modal Dialog
                activeSendToSourceName?.let { srcName ->
                    UniversalSendToDialog(
                        sourceName = srcName,
                        viewModel = viewModel,
                        onDismiss = { activeSendToSourceName = null }
                    )
                }

                // Universal Send To Toast Notification Banner
                lastSendToEvent?.let { event ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        SendToToastNotificationBanner(
                            event = event,
                            onDismiss = { viewModel.clearLastSendToEvent() }
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// MASTER SYNCHRONIZED PLAYBACK CONTROLLER BAR & VIEWER CARDS
// -----------------------------------------------------------------
@Composable
fun MasterSynchronizedPlaybackBar(
    viewModel: WorkstationViewModel,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val masterEngine = viewModel.masterAudioEngine
    val loadedMeta by masterEngine.loadedMetadata.collectAsStateWithLifecycle()
    val isAudioLoaded = loadedMeta != null
    val isPlaying by masterEngine.isPlaying.collectAsStateWithLifecycle()
    val posMs by masterEngine.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by masterEngine.durationMs.collectAsStateWithLifecycle()
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSongSection.collectAsStateWithLifecycle()
    val pitchShift by viewModel.pitchShiftSemitones.collectAsStateWithLifecycle()
    val isLooping by viewModel.isLoopingEnabled.collectAsStateWithLifecycle()
    val speedMultiplier by viewModel.tempoPreservedMultiplier.collectAsStateWithLifecycle()

    val formattedCurrentTime = if (isAudioLoaded) String.format("%02d:%02d.%d", (posMs / 1000) / 60, (posMs / 1000) % 60, (posMs % 1000) / 100) else "00:00.0"
    val formattedTotalTime = if (isAudioLoaded) String.format("%02d:%02d.%d", (durationMs / 1000) / 60, (durationMs / 1000) % 60, (durationMs % 1000) / 100) else "00:00.0"

    Surface(
        color = Color(0xFF070F1C),
        border = BorderStroke(1.dp, Color(0xFF132F52)),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().testTag("master_synchronized_playback_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            // Master Progress Seekbar Slider & Time Labels
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formattedCurrentTime,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00E5FF)
                )

                Slider(
                    value = posMs.toFloat(),
                    onValueChange = { masterEngine.seekTo(it.toLong()) },
                    valueRange = 0f..(durationMs.toFloat().coerceAtLeast(1000f)),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF),
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .padding(horizontal = 6.dp)
                )

                Text(
                    text = formattedTotalTime,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )
            }

            // Transport Controls & Synchronized Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Transport Buttons (Rewind, Play/Pause, Forward, Loop)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.stepPlaybackBackward(1000L) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Replay, "Rewind 1s", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (isPlaying) Color(0xFF00E5FF) else Color(0xFF1E293B),
                        onClick = { viewModel.toggleMasterPlayback() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause All" else "Play All",
                                tint = if (isPlaying) Color(0xFF0A0F1D) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.stepPlaybackForward(1000L) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.FastForward, "Forward 1s", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = { viewModel.toggleLooping() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Loop",
                            tint = if (isLooping) Color(0xFF10B981) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Middle: Synchronized Badges (Current Chord, Song Section, Pitch, Speed)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Active Chord Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF241C04),
                        border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.MusicNote, "Chord", tint = Color(0xFFD4AF37), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isAudioLoaded) (currentChordModel?.name ?: "--") else "--",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD4AF37)
                            )
                        }
                    }

                    // Song Section Badge
                    currentSection?.let { section ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(section.colorHex).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(section.colorHex))
                        ) {
                            Text(
                                text = section.title.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(section.colorHex),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Pitch Shift / Key Transpose Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E1035),
                        border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f)),
                        onClick = {
                            val nextShift = if (pitchShift >= 6) -6 else pitchShift + 1
                            viewModel.setPitchShift(nextShift)
                        }
                    ) {
                        Text(
                            text = if (pitchShift == 0) "ORIGINAL KEY" else "${if (pitchShift > 0) "+$pitchShift" else pitchShift}ST",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA855F7),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // Speed Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0F261C),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        onClick = {
                            val nextSpeed = when {
                                speedMultiplier >= 2.0f -> 0.5f
                                speedMultiplier >= 1.5f -> 2.0f
                                speedMultiplier >= 1.25f -> 1.5f
                                speedMultiplier >= 1.0f -> 1.25f
                                speedMultiplier >= 0.75f -> 1.0f
                                else -> 0.75f
                            }
                            viewModel.setSpeedMultiplier(nextSpeed)
                        }
                    ) {
                        Text(
                            text = "${speedMultiplier}x",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Right: Universal Send To Button
                UniversalSendToButton(
                    sourceName = "Synchronized Master Audio Stream (${currentChordModel?.name ?: "Mix"})",
                    buttonText = "SEND TO",
                    accentColor = Color(0xFF00E5FF),
                    onOpenSendToDialog = onOpenSendToDialog
                )
            }
        }
    }
}

@Composable
fun SongSectionViewerCard(viewModel: WorkstationViewModel) {
    val sections by viewModel.songSections.collectAsStateWithLifecycle()
    val activeSection by viewModel.currentSongSection.collectAsStateWithLifecycle()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1729)),
        border = BorderStroke(1.dp, Color(0xFF132F52)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("song_sections_timeline_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Segment, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SONG SECTIONS TIMELINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "TAP TO SEEK",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sections.forEach { section ->
                    val isActive = activeSection?.title == section.title
                    val sectionColor = Color(section.colorHex)
                    val bg = if (isActive) sectionColor.copy(alpha = 0.25f) else Color(0xFF13233C)
                    val border = BorderStroke(if (isActive) 2.dp else 1.dp, if (isActive) sectionColor else Color(0xFF1E293B))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = bg,
                        border = border,
                        onClick = { viewModel.setPlaybackPosition(section.startMs) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = section.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.White else sectionColor,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format("%02d:%02d", section.startMs / 1000 / 60, section.startMs / 1000 % 60),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SyncedLyricsViewerCard(viewModel: WorkstationViewModel) {
    val lyrics by viewModel.syncedLyrics.collectAsStateWithLifecycle()
    val activeIdx by viewModel.activeLyricIndex.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSongSection.collectAsStateWithLifecycle()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1729)),
        border = BorderStroke(1.dp, Color(0xFF132F52)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("synced_lyrics_viewer_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Subtitles, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYNCED KARAOKE LYRICS & CHORDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                currentSection?.let { section ->
                    Text(
                        text = "SECTION: ${section.title.uppercase()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(section.colorHex)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            lyrics.forEachIndexed { index, line ->
                val isActive = index == activeIdx
                val bgColor = if (isActive) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color.Transparent
                val border = if (isActive) BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)) else null

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = bgColor,
                    border = border,
                    onClick = { viewModel.setPlaybackPosition(line.startMs) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(
                            text = line.chordsAbove,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isActive) Color(0xFFD4AF37) else Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = line.text,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 3. SECTOR SELECTIONS - BOTTOM NAVIGATION
// -----------------------------------------------------------------
@Composable
fun WorkstationBottomNavigation(
    activeSection: String,
    onSectionClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF0C1424),
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("app_bottom_bar")
    ) {
        val navItems = listOf(
            Triple("Home", Icons.Default.Grid3x3, "home_tab"),
            Triple("Analyzer", Icons.Default.InterpreterMode, "analyzer_tab"),
            Triple("Studio", Icons.Default.MusicVideo, "studio_tab"),
            Triple("Library", Icons.Default.LibraryMusic, "library_tab"),
            Triple("Settings", Icons.Default.Settings, "settings_tab")
        )

        navItems.forEach { (section, icon, testTag) ->
            val isSelected = activeSection == section
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSectionClick(section) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = section,
                        tint = if (isSelected) Color(0xFF00E5FF) else Color(0xFF5E718B)
                    )
                },
                label = {
                    Text(
                        text = section,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFF0F4FC) else Color(0xFF5E718B),
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF132F52)
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

// -----------------------------------------------------------------
// 4. HOME TAB - MODERN COMMAND CENTER DASHBOARD
// -----------------------------------------------------------------
@Composable
fun DashboardTab(
    viewModel: WorkstationViewModel,
    onOpenRecorder: () -> Unit,
    onOpenImport: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val currentBpm by viewModel.bpm.collectAsStateWithLifecycle()
    val isRecordingState by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingTimerSeconds by viewModel.recordingTimerSeconds.collectAsStateWithLifecycle()
    val detectedArpeggio by viewModel.detectedArpeggio.collectAsStateWithLifecycle()
    val activeLick by viewModel.africanStyleLick.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()

    val globalKeySignature by viewModel.globalKeySignature.collectAsStateWithLifecycle()
    val isAnalyzingKey by viewModel.isAnalyzingKey.collectAsStateWithLifecycle()
    val keyAnalysisProgress by viewModel.keyAnalysisProgress.collectAsStateWithLifecycle()
    val keyAnalysisLogs by viewModel.keyAnalysisLogs.collectAsStateWithLifecycle()
    val uploadedFileName by viewModel.uploadedFileName.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    // Realtime background drawing clock tick simulation
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            tick += 1
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF030A16), Color(0xFF0A1428))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Station Status Row
        item {
            val tunerState by viewModel.tunerState.collectAsStateWithLifecycle()
            val currentKey = globalKeySignature ?: (currentChordModel?.root?.let { "$it Major" } ?: "C Major")
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("hero_status_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(if (isRecordingState) Color.Red else Color.Green, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRecordingState) "RECORDING ON-THE-AIR" else "HZ AUDIO ENGINE: ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRecordingState) Color.Red else Color(0xFF00E5FF),
                                letterSpacing = 2.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UniversalSendToButton(
                                sourceName = uploadedFileName ?: "Master Active Audio Stream",
                                buttonText = "SEND STREAM",
                                accentColor = Color(0xFF00E5FF),
                                onOpenSendToDialog = onOpenSendToDialog
                            )
                            Text(
                                text = "OFFLINE MODE",
                                fontSize = 10.sp,
                                color = Color(0xFFB0BEC5),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0xFF13233C), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sparkline oscilloscope Canvas representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF030710))
                    ) {
                        val waveformSamples = remember(tick) { viewModel.engine.generateWaveformSamples(120) }
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val path = Path()
                            val stepX = size.width / (waveformSamples.size - 1)
                            val midY = size.height / 2
                            
                            for (i in waveformSamples.indices) {
                                val amplitudeMultiplier = if (isRecordingState) 2.0f else 1.0f
                                val x = i * stepX
                                val y = midY + (waveformSamples[i] * yStepOffset(midY) * amplitudeMultiplier)
                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            drawPath(
                                path = path,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF00E5FF), Color(0xFFD4AF37), Color(0xFF00E5FF))
                                ),
                                style = Stroke(width = 3.5f)
                            )
                        }
                        
                        if (isRecordingState) {
                            Text(
                                text = "REC TIME: ${formatSeconds(recordingTimerSeconds)}",
                                fontSize = 11.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dashboard Top Section details grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DashboardStatusText(label = "Current Chord", value = currentChordModel?.name ?: "SILENT...", icon = Icons.Default.MusicNote, tint = Color(0xFFD4AF37))
                            DashboardStatusText(label = "Current Key", value = currentKey, icon = Icons.Default.Key, tint = Color(0xFF00E5FF))
                            DashboardStatusText(label = "Current BPM", value = "$currentBpm BPM", icon = Icons.Default.Timer, tint = Color(0xFFE040FB))
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DashboardStatusText(label = "Tuning Pitch", value = "${tunerState.noteName} (${tunerState.targetFreq}Hz)", icon = Icons.Default.Tune, tint = Color.Green)
                            DashboardStatusText(label = "Mic Capture", value = "MIC ACTIVE", icon = Icons.Default.Mic, tint = Color(0xFFEF5350))
                            DashboardStatusText(label = "Battery State", value = "LOW LATENCY OPT.", icon = Icons.Default.Bolt, tint = Color(0xFF4CAF50))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Source: Offline Microphonic Input Calibration",
                        fontSize = 10.sp,
                        color = Color(0xFF5E718B),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }
            }
        }

        // Universal Audio Sharing Engine Card
        item {
            UniversalAudioSharingEngineCard(viewModel = viewModel)
        }

        // Song Sections Timeline
        item {
            SongSectionViewerCard(viewModel = viewModel)
        }

        // Synced Karaoke Lyrics & Chords
        item {
            SyncedLyricsViewerCard(viewModel = viewModel)
        }

        // Prominent Global Key Signature Analyzer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_key_analyzer_module")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Global Key Signature icon",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GLOBAL KEY SIGNATURE ANALYZER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                        if (isAnalyzingKey) {
                            Text(
                                text = "ANALYZING...",
                                fontSize = 8.sp,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        } else if (globalKeySignature != null) {
                            Text(
                                text = "LOCKED",
                                fontSize = 8.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        } else {
                            Text(
                                text = "AWAITING FILE",
                                fontSize = 8.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0xFF1E293B), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAnalyzingKey) {
                        // Key Signature extraction progress bar
                        LinearProgressIndicator(
                            progress = { keyAnalysisProgress },
                            color = Color(0xFF00E5FF),
                            trackColor = Color(0xFF132F52),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Real-time log console
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .background(Color.Black, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF132F52), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(keyAnalysisLogs) { logLine ->
                                    Text(
                                        text = ">> $logLine",
                                        fontSize = 9.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else if (globalKeySignature != null) {
                        // Display prominent detected key signature beautifully
                        val key = globalKeySignature!!
                        val details = when (key) {
                            "C Major" -> Triple("A Minor", listOf("C", "D", "E", "F", "G", "A", "B"), listOf("C", "Dm", "Em", "F", "G", "Am", "Bdim"))
                            "A Minor" -> Triple("C Major", listOf("A", "B", "C", "D", "E", "F", "G"), listOf("Am", "Bdim", "C", "Dm", "Em", "F", "G"))
                            "G Major" -> Triple("E Minor", listOf("G", "A", "B", "C", "D", "E", "F#"), listOf("G", "Am", "Bm", "C", "D", "Em", "F#dim"))
                            "E Minor" -> Triple("G Major", listOf("E", "F#", "G", "A", "B", "C", "D"), listOf("Em", "F#dim", "G", "Am", "Bm", "C", "D"))
                            "F Major" -> Triple("D Minor", listOf("F", "G", "A", "Bb", "C", "D", "E"), listOf("F", "Gm", "Am", "Bb", "C", "Dm", "Edim"))
                            "D Minor" -> Triple("F Major", listOf("D", "E", "F", "G", "A", "Bb", "C"), listOf("Dm", "Edim", "F", "Gm", "Am", "Bb", "C"))
                            "D Major" -> Triple("B Minor", listOf("D", "E", "F#", "G", "A", "B", "C#"), listOf("D", "Em", "F#m", "G", "A", "Bm", "C#dim"))
                            "B Minor" -> Triple("D Major", listOf("B", "C#", "D", "E", "F#", "G", "A"), listOf("Bm", "C#dim", "D", "Em", "F#m", "G", "A"))
                            "A Major" -> Triple("F# Minor", listOf("A", "B", "C#", "D", "E", "F#", "G#"), listOf("A", "Bm", "C#m", "D", "E", "F#m", "G#dim"))
                            "F# Minor" -> Triple("A Major", listOf("F#", "G#", "A", "B", "C#", "D", "E"), listOf("F#m", "G#dim", "A", "Bm", "C#m", "D", "E"))
                            "E Major" -> Triple("C# Minor", listOf("E", "F#", "G#", "A", "B", "C#", "D#"), listOf("E", "F#m", "G#m", "A", "B", "C#m", "D#dim"))
                            "C# Minor" -> Triple("E Major", listOf("C#", "D#", "E", "F#", "G#", "A", "B"), listOf("C#m", "D#dim", "E", "F#m", "G#m", "A", "B"))
                            "Bb Major" -> Triple("G Minor", listOf("Bb", "C", "D", "Eb", "F", "G", "A"), listOf("Bb", "Cm", "Dm", "Eb", "F", "Gm", "Adim"))
                            "G Minor" -> Triple("Bb Major", listOf("G", "A", "Bb", "C", "D", "Eb", "F"), listOf("Gm", "Adim", "Bb", "Cm", "Dm", "Eb", "F"))
                            else -> Triple("C Major", listOf("C", "D", "E", "F", "G", "A", "B"), listOf("C", "Dm", "Em", "F", "G", "Am", "Bdim"))
                        }
                        val relativeKey = details.first
                        val scaleNotes = details.second
                        val scaleChords = details.third

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0B1329), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF1E3A60), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DETECTED GLOBAL KEY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00E5FF)
                                )
                                Text(
                                    text = "$key",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "Relative mode",
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Relative Mode: $relativeKey",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                            }

                            // A beautiful key graphic display circle
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(Color(0xFF00E5FF), Color(0xFFE040FB), Color(0xFF00E5FF))
                                        ),
                                        CircleShape
                                    )
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF0B1329), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key.take(3).trim(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // High fidelity breakdown: Scale Notes & Chords reference
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Scale Notes Row
                            Column {
                                Text(
                                    text = "DIATONIC SCALE SPECTRUM INTERVALS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    scaleNotes.forEach { note ->
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                                                .border(0.5.dp, Color(0xFF475569), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = note,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            // Scale Chords Row
                            Column {
                                Text(
                                    text = "COMPATIBLE DIATONIC HARMONY CHORDS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    scaleChords.take(6).forEach { chord ->
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF030A16), RoundedCornerShape(4.dp))
                                                .border(0.5.dp, Color(0xFF1E3A60), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = chord,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00E5FF)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Awaiting files informational placeholder state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info icon",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ready to analyze global key profiles. Load the 18.4MB Studio Demo Track in the Stem Separation view to automatically trigger high-resolution Pitch Class Profile (PCP) chordality translation.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Chord Detection Scrolling Timeline Card
        item {
            val chordTimeline by viewModel.chordTimeline.collectAsStateWithLifecycle()
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF070F1E)),
                border = BorderStroke(1.dp, Color(0xFF132F52)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chord_timeline_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row with Live pulsing indicator & Clear action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF00E5FF), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REAL-TIME CHORD TIMELINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.5.sp
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Pulsing animation
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF132F52), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val pulseColor = if (tick % 2 == 0) Color(0xFFEF5350) else Color(0xFFEF5350).copy(alpha = 0.4f)
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(pulseColor, CircleShape)
                                    )
                                    Text(
                                        text = "LIVE FEED",
                                        fontSize = 7.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Reset/Clear button
                            IconButton(
                                onClick = { viewModel.clearChordTimeline() },
                                modifier = Modifier.size(24.dp).testTag("clear_timeline_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear Timeline",
                                    tint = Color(0xFFEF5350),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scrolling list of chords
                    if (chordTimeline.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp)
                                .background(Color(0xFF030712).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF122E54).copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.HourglassEmpty,
                                    contentDescription = "Empty Timeline",
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No live chord stream detected yet.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap some simulation chord triggers below to feed the stream!",
                                    fontSize = 9.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    } else {
                        // Horizontal scrolling feed
                        val lazyListState = rememberLazyListState()
                        // Scroll to the end automatically when new items are added
                        LaunchedEffect(chordTimeline.size) {
                            if (chordTimeline.isNotEmpty()) {
                                lazyListState.animateScrollToItem(chordTimeline.size - 1)
                            }
                        }

                        LazyRow(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemsIndexed(chordTimeline) { index, entry ->
                                val isLatest = index == chordTimeline.size - 1
                                val cardBg = if (isLatest) {
                                    Brush.linearGradient(colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                } else {
                                    Brush.linearGradient(colors = listOf(Color(0xFF0B1329), Color(0xFF030712)))
                                }
                                val borderColor = if (isLatest) Color(0xFF00E5FF) else Color(0xFF1E293B)
                                val accentTint = when (entry.type) {
                                    "Minor Triad" -> Color(0xFF4CAF50)
                                    "Major Seventh", "Seventh" -> Color(0xFFE040FB)
                                    "Extended Jazz", "Jazz Extended Voicing" -> Color(0xFF00E5FF)
                                    else -> Color(0xFFD4AF37)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, borderColor),
                                        modifier = Modifier
                                            .width(135.dp)
                                            .fillMaxHeight()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(cardBg)
                                                .padding(8.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = entry.timestamp,
                                                        fontSize = 8.sp,
                                                        color = Color(0xFF64748B),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(accentTint, CircleShape)
                                                    )
                                                }

                                                Text(
                                                    text = entry.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isLatest) Color(0xFF00E5FF) else Color.White
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.Bottom
                                                ) {
                                                    Text(
                                                        text = entry.type,
                                                        fontSize = 7.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = Color(0xFF94A3B8),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        text = "${(entry.confidence * 100).toInt()}% Conf",
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF10B981)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    if (index < chordTimeline.size - 1) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Connector",
                                            tint = Color(0xFF334155),
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Chord Multi-Trigger Progression Deck
                    Text(
                        text = "SIMULATE LIVE WORKSTATION SEQUENCE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E718B),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.injectDemonstrationChords(listOf("C", "Am", "F", "G")) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("sim_pop_prog"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Pop Sequence", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                        }

                        Button(
                            onClick = { viewModel.injectDemonstrationChords(listOf("Cmaj7", "Am9", "Dm7", "G13")) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("sim_jazz_prog"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Jazz Extension", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE040FB))
                        }

                        Button(
                            onClick = { viewModel.injectDemonstrationChords(listOf("Em", "Am7", "G7", "C")) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("sim_minor_prog"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Minor Cadence", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37))
                        }
                    }
                }
            }
        }

        // Animated chord helper text
        if (detectedArpeggio != null || activeLick != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161F14)),
                        border = BorderStroke(1.dp, Color.Green.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.FlashOn,
                                contentDescription = "Intelligence",
                                tint = Color.Green,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                if (detectedArpeggio != null) {
                                    Text(
                                        text = "HARMONY DETECTED: $detectedArpeggio",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Green
                                    )
                                }
                                if (activeLick != null) {
                                    Text(
                                        text = "GUITAR LICK MATCHED: $activeLick",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Green,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Station Studios Modules
        item {
            Text(
                text = "HZ WORKSTATION SUITE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                color = Color(0xFF5E718B)
            )
        }

        // Grid-like list of cards: 8 Large Interactive Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // [Card 1/8] Live Chord Detector
                StudioModuleCard(
                    title = "Live Chord Detector",
                    subtitle = "Open live audio stream chord recognition to identify progression logs instantly.",
                    icon = Icons.Outlined.MusicNote,
                    colorAccent = Color(0xFFD4AF37),
                    actionLabel = "Open Screen",
                    onClick = { viewModel.setSection("Analyzer") }
                )

                // [Card 2/8] BPM Studio
                StudioModuleCard(
                    title = "BPM Studio Engine",
                    subtitle = "Calibrate, stretch and preserve play pitch through manual Tap or live Mic analysis.",
                    icon = Icons.Outlined.Timer,
                    colorAccent = Color(0xFF00E5FF),
                    actionLabel = "Tap Tempo",
                    onClick = { viewModel.engine.tapTempo() }
                )

                // [Card 3/8] AI Stem Separation
                StudioModuleCard(
                    title = "AI Stem Separation",
                    subtitle = "Unmix vocals, drum track overlays, bass and lead guitar loops in deep separation grids.",
                    icon = Icons.Outlined.MusicVideo,
                    colorAccent = Color(0xFFE040FB),
                    actionLabel = "Separation Desk",
                    onClick = { viewModel.setSection("Studio") }
                )

                // [Card 4/8] Recorder
                StudioModuleCard(
                    title = "Workstation Audio Recorder",
                    subtitle = "Record crystal-clear WAV loops or microphone notes for direct transcription analysis.",
                    icon = Icons.Outlined.Mic,
                    colorAccent = Color.Red,
                    actionLabel = "Open Recorder",
                    onClick = onOpenRecorder
                )

                // [Card 5/8] Tuner
                StudioModuleCard(
                    title = "Chromatic instrument Tuner",
                    subtitle = "Access ultra-precise, real-time chromatic tuning and EADGBE guitar reference pitches.",
                    icon = Icons.Outlined.GraphicEq,
                    colorAccent = Color(0xFF00E5FF),
                    actionLabel = "Tune BaseNote",
                    onClick = { viewModel.setSection("Analyzer") }
                )

                // [Card 6/8] Arpeggio Detector
                StudioModuleCard(
                    title = "Arpeggio & Lick Detector",
                    subtitle = "Extract single-note guitar leads, scale walk-ups, and specialized Sungura melodic hooks.",
                    icon = Icons.Outlined.QueryStats,
                    colorAccent = Color.Green,
                    actionLabel = "Analyze Arpeggios",
                    onClick = { viewModel.setSection("Analyzer") }
                )

                // [Card 7/8] Song Analyzer
                StudioModuleCard(
                    title = "Song Analyzer Station",
                    subtitle = "Import offline MP3 or WAV files to outline structures, key signatures, and chord changes.",
                    icon = Icons.Outlined.FileOpen,
                    colorAccent = Color(0xFF4CAF50),
                    actionLabel = "Import Song",
                    onClick = onOpenImport
                )

                // [Card 8/8] Session Manager
                StudioModuleCard(
                    title = "Session Manager",
                    subtitle = "Browse stored project data cards, custom recordings, and guitar licks libraries.",
                    icon = Icons.Outlined.LibraryMusic,
                    colorAccent = Color(0xFF5E718B),
                    actionLabel = "Open Database",
                    onClick = { viewModel.setSection("Library") }
                )
            }
        }

        // Session Manager List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT SESSIONS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    color = Color(0xFF5E718B)
                )
                TextButton(onClick = { viewModel.setSection("Library") }) {
                    Text("View Database", color = Color(0xFF00E5FF), fontSize = 12.sp)
                }
            }
        }

        if (sessions.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No recorded sessions yet. Use the '+' floating action button below to create your very first AI project session!",
                            fontSize = 12.sp,
                            color = Color(0xFFB0BEC5),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(sessions.take(3)) { session ->
                SessionWorkstationItemRow(
                    session = session,
                    onDelete = { viewModel.deleteSession(session) },
                    onExport = { viewModel.triggerExport("csv", session) },
                    onExportMidi = { viewModel.triggerExport("midi", session) }
                )
            }
        }

        // Bottom space padding
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

private fun yStepOffset(midY: Float): Float {
    return (midY * 0.7f)
}

@Composable
fun DashboardStatusText(label: String, value: String, icon: ImageVector, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C1424), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF132F52), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 9.sp, color = Color(0xFFB0BEC5), fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun QuickMetricCell(label: String, value: String, tint: Color) {
    Column {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB0BEC5), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = tint)
    }
}

@Composable
fun StudioModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colorAccent: Color,
    actionLabel: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(colorAccent.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF5E718B),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SessionWorkstationItemRow(
    session: ProjectSession,
    onDelete: () -> Unit,
    onExport: () -> Unit,
    onExportMidi: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1729)),
        border = BorderStroke(1.dp, Color(0xFF132F52)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${session.bpm} BPM  •  ${session.keySignature}",
                        fontSize = 11.sp,
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    UniversalSendToButton(
                        sourceName = session.title,
                        buttonText = "SEND TO",
                        accentColor = Color(0xFF00E5FF),
                        onOpenSendToDialog = onOpenSendToDialog
                    )

                    // MIDI Export Icon
                    IconButton(onClick = onExportMidi, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "Export MIDI",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    // Export Icon
                    IconButton(onClick = onExport, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Export Report",
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    // Delete Icon
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF5350).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (session.notes.isNotEmpty()) {
                Text(
                    text = session.notes,
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (session.detectedChords.isNotEmpty()) {
                Text(
                    text = "Detected Progression: ${session.detectedChords}",
                    fontSize = 11.sp,
                    color = Color(0xFFD4AF37),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------
// 5. ANALYZER TAB - CHORD & FREQUENCIES SPECTROGRAM (CHORD AI PRO)
// -----------------------------------------------------------------
data class ChordPadInfo(val symbol: String, val degree: String)

@Composable
fun RealtimeAnalyzerTab(
    viewModel: WorkstationViewModel,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val activeNotes by viewModel.liveNotesBuffer.collectAsStateWithLifecycle()
    val detectedArpeggio by viewModel.detectedArpeggio.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val playPositionMs by viewModel.playbackPositionMs.collectAsStateWithLifecycle()
    val showClassification by viewModel.showHarmonicClassification.collectAsStateWithLifecycle()
    val activePerformanceNotes by viewModel.activePerformanceNotes.collectAsStateWithLifecycle()
    val performanceNoteStream by viewModel.performanceNoteStream.collectAsStateWithLifecycle()

    var isGridViewEnabled by remember { mutableStateOf(true) }
    var chordsMode by remember { mutableStateOf("Basic") } // "Basic" or "Precise"
    var voicingsEnabled by remember { mutableStateOf(true) } // true: single keyboard, false: dual keyboard
    var transposeSemi by remember { mutableStateOf(-1) }
    var isLooping by remember { mutableStateOf(false) }
    var speedMultiplier by remember { mutableStateOf(1.0f) }

    val currentChordName = currentChordModel?.name ?: "Chord ai"
    val currentChordSymbol = currentChordModel?.root ?: "Chord ai"
    val chordDegree = when (currentChordSymbol) {
        "F#", "F#add9" -> "III"
        "B", "Badd9" -> "VI"
        "C#" -> "VII"
        "D#m", "D#m7" -> "I"
        "A#m" -> "V"
        "G#m" -> "IV"
        else -> "I"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070F1A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. TOP HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1525))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.setSection("Home") }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { /* playlist view */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.List, contentDescription = "Playlist", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { /* Instruments toggle */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF13233C)),
                        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Instruments", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* Lyrics toggle */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF13233C)),
                        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Lyrics", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = { /* overflow */ }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. MAIN DISPLAY (GRID OR WAVEFORM)
            if (isGridViewEnabled) {
                // Chord Pad Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    // Top Wide Card: e.g. D#m
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B8A7E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .clickable { viewModel.engine.selectChord("D#m") }
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            Column(modifier = Modifier.align(Alignment.TopStart)) {
                                Text(
                                    text = "D#m",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "I",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.BottomEnd).size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Grid layout of chord pads
                    val pads = listOf(
                        listOf(ChordPadInfo("F#", "III"), ChordPadInfo("", ""), ChordPadInfo("", ""), ChordPadInfo("B", "VI")),
                        listOf(ChordPadInfo("B", "VI"), ChordPadInfo("", ""), ChordPadInfo("", ""), ChordPadInfo("", "")),
                        listOf(ChordPadInfo("B", "VI"), ChordPadInfo("", ""), ChordPadInfo("", ""), ChordPadInfo("", "")),
                        listOf(ChordPadInfo("B", "VI"), ChordPadInfo("", ""), ChordPadInfo("", ""), ChordPadInfo("C#", "VII")),
                        listOf(ChordPadInfo("D#m7", "I"), ChordPadInfo("", ""), ChordPadInfo("", ""), ChordPadInfo("", ""))
                    )

                    pads.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { pad ->
                                val isActive = pad.symbol.isNotEmpty() && currentChordSymbol == pad.symbol
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) Color(0xFF2E3E50) else if (pad.symbol.isNotEmpty()) Color(0xFF1E242C) else Color(0xFF141A22)
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isActive) Color(0xFF00E5FF) else if (pad.symbol.isNotEmpty()) Color(0xFF2E343E) else Color(0xFF1B222D)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(72.dp)
                                        .clickable(enabled = pad.symbol.isNotEmpty()) {
                                            viewModel.engine.selectChord(pad.symbol)
                                        }
                                ) {
                                    if (pad.symbol.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = pad.symbol,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = pad.degree,
                                                fontSize = 10.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Waveform / Chord Analysis view (Screenshot 4)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A2B)),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Chord analysis",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Analyzing...",
                                    fontSize = 9.sp,
                                    color = Color(0xFF00E5FF)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { 0.65f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFF00E5FF),
                                trackColor = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Audio Waveform Timeline Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFF020617), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barCount = 60
                            val spacing = size.width / barCount
                            val centerY = size.height / 2
                            
                            drawLine(
                                color = Color(0xFF1E293B),
                                start = Offset(0f, centerY),
                                end = Offset(size.width, centerY),
                                strokeWidth = 2f
                            )

                            drawLine(
                                color = Color.White,
                                start = Offset(size.width / 2, 0f),
                                end = Offset(size.width / 2, size.height),
                                strokeWidth = 3f
                            )

                            for (i in 0 until barCount) {
                                val amplitudeFraction = sin(i * 0.15f) * 0.4f + 0.5f
                                val barHeight = (amplitudeFraction * size.height * 0.6f).toFloat()
                                val posX = i * spacing
                                
                                drawLine(
                                    color = if (i < barCount / 2) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color(0xFF475569).copy(alpha = 0.4f),
                                    start = Offset(posX, centerY - barHeight / 2),
                                    end = Offset(posX, centerY + barHeight / 2),
                                    strokeWidth = 3f
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Chord ai",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "AI real-time chord detection engine active",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. CONTROL BUTTONS BAR (CHORDS, VOICINGS, GRID VIEW)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Chords
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101C2E)),
                    border = BorderStroke(1.dp, Color(0xFF1E2E44)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Chords", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .background(Color(0xFF070F1A), RoundedCornerShape(12.dp))
                                .padding(2.dp)
                                .fillMaxWidth()
                        ) {
                            listOf("Basic", "Precise").forEach { mode ->
                                val isSel = chordsMode == mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSel) Color.White else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { chordsMode = mode }
                                        .padding(vertical = 3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Card 2: Voicings
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101C2E)),
                    border = BorderStroke(1.dp, Color(0xFF1E2E44)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Voicings", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Switch(
                            checked = voicingsEnabled,
                            onCheckedChange = { voicingsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00E5FF),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }

                // Card 3: Grid view
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101C2E)),
                    border = BorderStroke(1.dp, Color(0xFF1E2E44)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.0f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Grid view", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(4.dp))

                        IconButton(
                            onClick = { isGridViewEnabled = !isGridViewEnabled },
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    if (isGridViewEnabled) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (isGridViewEnabled) Icons.Default.Grid3x3 else Icons.Default.GridOff,
                                contentDescription = "Toggle Grid View",
                                tint = if (isGridViewEnabled) Color(0xFF00E5FF) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. ACTIVE DISPLAY & DUAL PIANO KEYBOARD VIEW
            if (isGridViewEnabled) {
                if (!voicingsEnabled) {
                    // DUAL PIANO KEYBOARDS (Screenshot 2)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "F#",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00E5FF)
                                )
                                Text(
                                    text = "III",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "B",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00E5FF)
                                )
                                Text(
                                    text = "VI",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                MiniChordPianoKeyboard(
                                    activeNotes = listOf("F#", "A#", "C#"),
                                    onKeyClick = { viewModel.engine.tapLiveMusicalNote(it) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MiniChordPianoKeyboard(
                                    activeNotes = listOf("B", "D#", "F#"),
                                    onKeyClick = { viewModel.engine.tapLiveMusicalNote(it) }
                                )
                            }
                        }
                    }
                } else {
                    // SINGLE LARGE KEYBOARD (Screenshot 1 & 3)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentChordName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF)
                        )
                        Text(
                            text = chordDegree,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ChordAIPianoKeyboard(
                            activeNotes = currentChordModel?.notes ?: emptyList(),
                            onKeyClick = { viewModel.engine.tapLiveMusicalNote(it) }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentChordName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ChordAIPianoKeyboard(
                        activeNotes = currentChordModel?.notes ?: emptyList(),
                        onKeyClick = { viewModel.engine.tapLiveMusicalNote(it) }
                    )
                }
            }

            // 4.5 HARMONIC RECONSTRUCTION ENGINE & ACCURATE NOTE PRESERVATION
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HarmonicClassificationToggleCard(viewModel = viewModel)

                ChronologicalNoteStreamView(
                    notes = performanceNoteStream,
                    showClassification = showClassification
                )

                EnhancedGuitarFretboard(
                    activePerformanceNotes = activePerformanceNotes,
                    showClassification = showClassification,
                    onNoteClick = { viewModel.feedPerformanceNotes(listOf(it)) }
                )

                EnhancedPianoKeyboard(
                    activePerformanceNotes = activePerformanceNotes,
                    showClassification = showClassification,
                    onKeyClick = { viewModel.feedPerformanceNotes(listOf(it)) }
                )
            }

            // 5. OTHER RICH MODULES FOR VALUE RETENTION
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFF1E293B).copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 12.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1424)),
                border = BorderStroke(1.dp, Color(0xFF1E2E44)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "REAL-TIME FFT WAVE SPECTRUM ANALYSIS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val visualAmplitudes = remember(playPositionMs) { viewModel.engine.generateRealtimeFFTAmplitudes(24) }
                    Box(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val spacing = size.width / visualAmplitudes.size
                            for (i in visualAmplitudes.indices) {
                                val barHeight = visualAmplitudes[i] * size.height * 0.9f
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF00E5FF), Color(0xFF1E293B))
                                    ),
                                    topLeft = Offset(i * spacing, size.height - barHeight),
                                    size = Size(spacing * 0.7f, barHeight)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1424)),
                border = BorderStroke(1.dp, Color(0xFF1E2E44)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                val tunerState by viewModel.tunerState.collectAsStateWithLifecycle()
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "CHROMATIC TUNER CENTER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = tunerState.noteName, fontSize = 24.sp, fontWeight = FontWeight.Black, color = if (tunerState.isTuned) Color.Green else Color(0xFF00E5FF))
                            Text(text = "Target: ${tunerState.targetFreq} Hz", fontSize = 9.sp, color = Color(0xFF64748B))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = String.format("%+2.0f Cents", tunerState.deviationCents), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (tunerState.isTuned) Color.Green else Color(0xFFFF9100))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(130.dp))
        }

        // 6. FLOATING BOTTOM MEDIA PLAYER BAR
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF0D1525))
                .border(BorderStroke(1.dp, Color(0xFF1E3A60).copy(alpha = 0.5f)))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { transposeSemi = if (transposeSemi == -1) 0 else -1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF08A)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = if (transposeSemi == -1) "-1" else "Pitch", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { speedMultiplier = if (speedMultiplier == 1.0f) 0.8f else 1.0f },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF13233C)),
                        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = "Speed (${speedMultiplier}x)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isLooping = !isLooping },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLooping) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF13233C)
                        ),
                        border = BorderStroke(1.dp, if (isLooping) Color(0xFF00E5FF) else Color(0xFF1E3A60)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = "Loop", color = if (isLooping) Color(0xFF00E5FF) else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sliderValue = playPositionMs.toFloat()
                    val seconds = playPositionMs / 1000L
                    val formattedTime = formatSeconds(seconds.toInt()) + " / 13:36"

                    Slider(
                        value = sliderValue,
                        onValueChange = { viewModel.setPlaybackPosition(it.toLong()) },
                        valueRange = 0f..24000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFEF08A),
                            activeTrackColor = Color(0xFFFEF08A),
                            inactiveTrackColor = Color(0xFF475569)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFFFEF08A), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Cover note",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Yeshua_____LIVE_____Josue_Avila______Calvary_Orlando(256k)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        modifier = Modifier.weight(0.7f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val prevPos = (playPositionMs - 3000L).coerceAtLeast(0L)
                                viewModel.setPlaybackPosition(prevPos)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(24.dp))
                        }

                        IconButton(
                            onClick = { viewModel.toggleStemPlayback() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val nextPos = (playPositionMs + 3000L).coerceAtMost(24000L)
                                viewModel.setPlaybackPosition(nextPos)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChordAIPianoKeyboard(
    activeNotes: List<String>,
    onKeyClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C101B))
            .border(1.dp, Color(0xFF1D2E49), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFFC62828))
                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .background(Color(0xFF111827))
        ) {
            val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2")
            Row(modifier = Modifier.fillMaxSize()) {
                whiteKeys.forEach { note ->
                    val baseNote = note.removeSuffix("2")
                    val isHighlighted = activeNotes.contains(baseNote) || activeNotes.contains(note)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (isHighlighted) Color(0xFFFEF9D9) else Color(0xFFFCFCFC),
                                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                            )
                            .border(0.8.dp, Color(0xFF0F1522), RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .clickable { onKeyClick(note) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = if (isHighlighted) baseNote else "",
                            fontSize = 9.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }

            val blackKeys = listOf(
                Pair("C#", 1f),
                Pair("D#", 2f),
                Pair("F#", 4f),
                Pair("G#", 5f),
                Pair("A#", 6f),
                Pair("C#2", 8f),
                Pair("D#2", 9f),
                Pair("F#2", 11f)
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalWidth = maxWidth
                val whiteKeyWidth = totalWidth / whiteKeys.size

                for (pair in blackKeys) {
                    val note = pair.first
                    val position = pair.second
                    val baseNote = note.removeSuffix("2")
                    val isHighlighted = activeNotes.contains(baseNote) || activeNotes.contains(note)
                    val leftOffset = whiteKeyWidth * position - (whiteKeyWidth * 0.32f)

                    Box(
                        modifier = Modifier
                            .offset(x = leftOffset)
                            .width(whiteKeyWidth * 0.64f)
                            .height(72.dp)
                            .background(
                                color = if (isHighlighted) Color(0xFFFEF08A) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)
                            )
                            .border(0.8.dp, Color(0xFF020617), RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                            .clickable { onKeyClick(note) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = if (isHighlighted) baseNote else "",
                            fontSize = 8.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniChordPianoKeyboard(
    activeNotes: List<String>,
    onKeyClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C101B))
            .border(1.dp, Color(0xFF1D2E49), RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(Color(0xFF111827))
        ) {
            val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2")
            Row(modifier = Modifier.fillMaxSize()) {
                whiteKeys.forEach { note ->
                    val baseNote = note.removeSuffix("2")
                    val isHighlighted = activeNotes.contains(baseNote) || activeNotes.contains(note)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (isHighlighted) Color(0xFFFEF9D9) else Color(0xFFFCFCFC),
                                shape = RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)
                            )
                            .border(0.5.dp, Color(0xFF0F1522), RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                            .clickable { onKeyClick(note) }
                    )
                }
            }

            val blackKeys = listOf(
                Pair("C#", 1f),
                Pair("D#", 2f),
                Pair("F#", 4f),
                Pair("G#", 5f),
                Pair("A#", 6f),
                Pair("C#2", 8f),
                Pair("D#2", 9f)
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalWidth = maxWidth
                val whiteKeyWidth = totalWidth / whiteKeys.size

                for (pair in blackKeys) {
                    val note = pair.first
                    val position = pair.second
                    val baseNote = note.removeSuffix("2")
                    val isHighlighted = activeNotes.contains(baseNote) || activeNotes.contains(note)
                    val leftOffset = whiteKeyWidth * position - (whiteKeyWidth * 0.3f)

                    Box(
                        modifier = Modifier
                            .offset(x = leftOffset)
                            .width(whiteKeyWidth * 0.6f)
                            .height(42.dp)
                            .background(
                                color = if (isHighlighted) Color(0xFFFEF08A) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                            )
                            .border(0.5.dp, Color(0xFF020617), RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                            .clickable { onKeyClick(note) }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 5B. PLAYBACK-LINKED REAL-TIME CHORD TIMELINE & VOICINGS
// -----------------------------------------------------------------
@Composable
fun RealtimePlaybackChordTimeline(viewModel: WorkstationViewModel) {
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val playPositionMs by viewModel.playbackPositionMs.collectAsStateWithLifecycle()
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    
    val totalDurationMs = 24000L
    val segmentDurationMs = 3000L
    val chordsList = listOf("C", "Am", "F", "G7", "Cmaj7", "Am7", "G13", "Sungura A")
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("playback_chord_timeline_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleStemPlayback() },
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (isPlaying) Color(0xFFEF5350) else Color(0xFF00E5FF), CircleShape)
                            .testTag("timeline_quick_play_toggle")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Quick Playback Trigger",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "REAL-TIME CHORD DETECTOR & VOICINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                
                // Position tracker
                Text(
                    text = String.format("%.1f s / 24.0 s", playPositionMs / 1000f),
                    fontSize = 11.sp,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Render the Horizontal timeline grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF132F52), RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                // Chord segment blocks
                Row(modifier = Modifier.fillMaxSize()) {
                    chordsList.forEachIndexed { index, chord ->
                        val startSec = (index * segmentDurationMs) / 1000f
                        val isActive = playPositionMs >= index * segmentDurationMs && playPositionMs < (index + 1) * segmentDurationMs
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(1.dp)
                                .background(
                                    if (isActive) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color(0xFF0F172A),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isActive) Color(0xFF00E5FF) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    viewModel.setPlaybackPosition(index * segmentDurationMs)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = chord,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isActive) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                                )
                                Text(
                                    text = String.format("%.0fs", startSec),
                                    fontSize = 7.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
                
                // Playhead layout
                val fraction = playPositionMs.toFloat() / totalDurationMs
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val xOffset = maxWidth * fraction
                    
                    Box(
                        modifier = Modifier
                            .offset(x = xOffset - 1.dp)
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(Color(0xFFEF5350))
                    )
                    
                    Box(
                        modifier = Modifier
                            .offset(x = xOffset - 4.dp, y = (-2).dp)
                            .size(8.dp)
                            .background(Color(0xFFEF5350), CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "💡 Click on any chord block in the roadmap to instantly scrub / seek output playhead.",
                fontSize = 8.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            if (currentChordModel != null) {
                val chord = currentChordModel!!
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "HARMONIC DETECTED PROFILE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = chord.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD4AF37)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "FORMULA", fontSize = 7.sp, color = Color(0xFF64748B))
                                Text(text = chord.formula, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text(text = "ROOT", fontSize = 7.sp, color = Color(0xFF64748B))
                                Text(text = chord.root, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                            }
                            Column {
                                Text(text = "CONFIDENCE", fontSize = 7.sp, color = Color(0xFF64748B))
                                Text(text = String.format("%.0f%%", chord.confidence * 100), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "VOICINGS & INTERVALS GUIDE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val cleanNotes = chord.notes.filter { it != "unknown" }
                        Text(
                            text = "Active notes: " + cleanNotes.joinToString(", "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        val intervals = when (cleanNotes.size) {
                            3 -> listOf("Root", "3rd", "5th")
                            4 -> listOf("Root", "3rd", "5th", "7th")
                            5 -> listOf("Root", "3rd", "5th", "7th", "9th")
                            else -> listOf("R", "3", "5", "7", "9", "13")
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            cleanNotes.forEachIndexed { i, nt ->
                                val label = intervals.getOrNull(i) ?: "Ext"
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1E293B), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = nt, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = label, fontSize = 6.sp, color = Color(0xFFE040FB))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabSelectionPill(text: String, isSel: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isSel) Color(0xFF1B2A4A) else Color.Transparent, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color(0xFF00E5FF) else Color(0xFFB0BEC5))
    }
}

// -----------------------------------------------------------------
// 6. CUSTOM GUITAR FRETBOARD RENDER COMPOSABLE
// -----------------------------------------------------------------
@Composable
fun GuitarFretboardRenderer(
    selectedChords: DetectedChordInfo?,
    onNoteClick: (String) -> Unit
) {
    val notesPositions = when (selectedChords?.name) {
        "C Major" -> mapOf(5 to 3, 4 to 2, 2 to 1)
        "G Major" -> mapOf(6 to 3, 5 to 2, 1 to 3)
        "D Major" -> mapOf(3 to 2, 2 to 3, 1 to 2)
        "A Minor" -> mapOf(4 to 2, 3 to 2, 2 to 1)
        "E Minor" -> mapOf(5 to 2, 4 to 2)
        "F Major" -> mapOf(6 to 1, 5 to 3, 4 to 3, 3 to 2, 2 to 1, 1 to 1)
        "C Major 7th" -> mapOf(5 to 3, 4 to 2, 2 to 0)
        "A Minor 7th" -> mapOf(5 to 0, 4 to 2, 3 to 0, 2 to 1)
        "G Dominant 7th" -> mapOf(6 to 3, 5 to 2, 1 to 1)
        "A Major (Sungura)" -> mapOf(4 to 2, 3 to 2, 2 to 2)
        else -> mapOf()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F1522))
            .border(1.dp, Color(0xFF1D2E49), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Active Fingering Overlay (${selectedChords?.name ?: "No chord overlay"})",
            fontSize = 11.sp,
            color = Color(0xFFD4AF37),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Fretboard Drawing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val fretsCount = 6
                val stepX = size.width / fretsCount
                val stringsCount = 6
                val stepY = size.height / (stringsCount + 1)

                // 1. Draw Fret lines (vertical)
                for (f in 0..fretsCount) {
                    val x = f * stepX
                    drawLine(Color(0xFF5E718B), Offset(x, 0f), Offset(x, size.height), strokeWidth = 3f)
                }

                // 2. Draw Strings (horizontal)
                for (s in 0 until stringsCount) {
                    val y = (s + 1) * stepY
                    drawLine(Color(0xFFE0E0E0), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f + (s * 0.5f))
                }

                // 3. Highlight chord fingerings
                notesPositions.forEach { (stringNum, fretNum) ->
                    val sIndex = stringsCount - stringNum // convert EADGBE to string layout
                    val y = (sIndex + 1) * stepY
                    val x = (fretNum - 0.5f) * stepX
                    
                    if (fretNum > 0) {
                        drawCircle(
                            color = Color(0xFFD4AF37),
                            radius = 12f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Triggerable tapping string notes helper buttons
        Text(text = "Tap string to hear and add manually:", fontSize = 10.sp, color = Color(0xFF5E718B))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val stringsMap = listOf(
                Pair("E (6th)", "E2"),
                Pair("A (5th)", "A2"),
                Pair("D (4th)", "D3"),
                Pair("G (3rd)", "G3"),
                Pair("B (2nd)", "B3"),
                Pair("E (1st)", "E4")
            )
            stringsMap.forEach { (lbl, note) ->
                OutlinedButton(
                    onClick = { onNoteClick(note) },
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .padding(horizontal = 2.dp)
                ) {
                    Text(text = lbl, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 7. CUSTOM PIANO KEYBOARD RENDER COMPOSABLE
// -----------------------------------------------------------------
@Composable
fun PianoKeyboardRenderer(
    activeMatchingNotes: List<String>,
    onKeyClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C101B))
            .border(1.dp, Color(0xFF1D2E49), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎹 Interactive 3D Piano Keys (Voicings Highlighted)",
                fontSize = 11.sp,
                color = Color(0xFF00E5FF),
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (activeMatchingNotes.isNotEmpty()) {
                Text(
                    text = "Voicing: " + activeMatchingNotes.joinToString(", "),
                    fontSize = 10.sp,
                    color = Color(0xFFD4AF37),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Real-feeling red velvet felt bumper strip at the top of piano keys, just like a grand piano!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color(0xFFC62828)) // Rich crimson red keyfelt
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp)
                .background(Color(0xFF111827))
        ) {
            // White keys
            val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2")
            Row(modifier = Modifier.fillMaxSize()) {
                whiteKeys.forEach { note ->
                    val baseNote = note.removeSuffix("2")
                    val isHighlighted = activeMatchingNotes.contains(baseNote) || activeMatchingNotes.contains(note)
                    
                    // Interval name matching
                    val intervalName = when {
                        isHighlighted -> {
                            val idx = activeMatchingNotes.indexOfFirst { it == baseNote || it == note }
                            listOf("Root", "3rd", "5th", "7th", "9th", "13th").getOrNull(idx) ?: "Voice"
                        }
                        else -> null
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                brush = if (isHighlighted) {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF80FAFF),
                                            Color(0xFF00D2E6),
                                            Color(0xFF009EB1)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFFCFCFC),
                                            Color(0xFFF1F5F9),
                                            Color(0xFFE2E8F0)
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
                            )
                            .border(0.8.dp, Color(0xFF0F1522), RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
                            .clickable { onKeyClick(note) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // 3D lip at the front edge of the white key
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(
                                    color = if (isHighlighted) Color(0xFF007080) else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
                                )
                                .align(Alignment.BottomCenter)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            if (intervalName != null) {
                                Text(
                                    text = intervalName,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.5.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = note,
                                fontSize = 10.sp,
                                color = if (isHighlighted) Color.Black else Color(0xFF475569),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Black keys overlay positioned precisely with real-looking 3D drop-shadow overlays
            val blackKeys = listOf(
                Pair("C#", 1f),
                Pair("D#", 2f),
                Pair("F#", 4f),
                Pair("G#", 5f),
                Pair("A#", 6f),
                Pair("C#2", 8f),
                Pair("D#2", 9f),
                Pair("F#2", 11f)
            )
            
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalWidth = maxWidth
                val whiteKeyWidth = totalWidth / whiteKeys.size
                
                for (pair in blackKeys) {
                    val note = pair.first
                    val position = pair.second
                    val baseNote = note.removeSuffix("2")
                    val isHighlighted = activeMatchingNotes.contains(baseNote) || activeMatchingNotes.contains(note)
                    val leftOffset = whiteKeyWidth * position - (whiteKeyWidth * 0.32f)
                    
                    // 1. Realistic Drop Shadow of Black Key on White Keys underneath
                    Box(
                        modifier = Modifier
                            .offset(x = leftOffset + 2.5.dp, y = 2.dp)
                            .width(whiteKeyWidth * 0.64f)
                            .height(86.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                            )
                    )

                    // 2. The Black Key itself
                    Box(
                        modifier = Modifier
                            .offset(x = leftOffset)
                            .width(whiteKeyWidth * 0.64f)
                            .height(85.dp)
                            .background(
                                brush = if (isHighlighted) {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFFF85FF),
                                            Color(0xFFD500F9),
                                            Color(0xFF7B0091)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF64748B), // Top gloss/bevel highlight line
                                            Color(0xFF334155),
                                            Color(0xFF0F172A),
                                            Color(0xFF030712)  // Deep matte black base at bottom
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                            )
                            .border(1.dp, if (isHighlighted) Color(0xFFE040FB) else Color(0xFF020617), RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .clickable { onKeyClick(note) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // 3D lip at the front edge of the black key
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(
                                    color = if (isHighlighted) Color(0xFF4A0057) else Color.Black,
                                    shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                                )
                                .align(Alignment.BottomCenter)
                        )

                        Text(
                            text = note,
                            fontSize = 8.sp,
                            color = if (isHighlighted) Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 8. STUDIO AUDIO SEPARATION & RESTORATION TAB
// -----------------------------------------------------------------
@Composable
fun StudioAudioSeparationTab(
    viewModel: WorkstationViewModel,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val stemSepState by viewModel.stemSeparation.collectAsStateWithLifecycle()
    val uploadedFileName by viewModel.uploadedFileName.collectAsStateWithLifecycle()
    val uploadedFileSize by viewModel.uploadedFileSize.collectAsStateWithLifecycle()
    val aiAnalysisResult by viewModel.aiAnalysisResult.collectAsStateWithLifecycle()

    val isAnalyzingTempo by viewModel.isAnalyzingTempo.collectAsStateWithLifecycle()
    val tempoDetectionProgress by viewModel.tempoDetectionProgress.collectAsStateWithLifecycle()
    val tempoDetectionLogs by viewModel.tempoDetectionLogs.collectAsStateWithLifecycle()

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var name = "uploaded_track.wav"
            var size = "8.2 MB"
            try {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) name = cursor.getString(nameIndex)
                        if (sizeIndex != -1) {
                            val bytes = cursor.getLong(sizeIndex)
                            size = String.format("%.2f MB", bytes.toFloat() / (1024 * 1024))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.setUploadedFile(name, size)
        }
    }

    val nrEnabled by viewModel.noiseReductionEnabled.collectAsStateWithLifecycle()
    val humEnabled by viewModel.humRemovalEnabled.collectAsStateWithLifecycle()
    val clipEnabled by viewModel.clippingRepairEnabled.collectAsStateWithLifecycle()
    val vocalEnabled by viewModel.vocalEnhancementEnabled.collectAsStateWithLifecycle()

    var processingMode by remember { mutableStateOf("Balanced") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030A16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI STEM SEPARATOR CORE
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF071124)),
            border = BorderStroke(1.dp, Color(0xFF1E3A60)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("ai_stem_separator_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI NEURAL STEM EXTRACTOR & STUDY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 1.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "GEMINI-3.5 INTEGRATION",
                            fontSize = 7.sp,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Upload any vocal/instrumental audio file. Our pipeline uses machine learning models to split tracks into raw stem waves, coupled with custom analysis.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Render based on state
                when (val state = stemSepState) {
                    is StemSeparationState.Idle -> {
                        if (uploadedFileName == null) {
                            // File Picker Drag/Drop container layout
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.5.dp, Brush.sweepGradient(listOf(Color(0xFF1E3A60), Color(0xFF00E5FF), Color(0xFF1E3A60)))), RoundedCornerShape(12.dp))
                                    .background(Color(0xFF020712).copy(alpha = 0.6f))
                                    .clickable { fileLauncher.launch("audio/*") }
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.FileUpload,
                                        contentDescription = "Upload Icon",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "DRAG & DROP OR TAP TO UPLOAD",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Supports MP3, WAV, FLAC, M4A up to 50MB",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { fileLauncher.launch("audio/*") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A60)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("choose_file_btn")
                                    ) {
                                        Text("Choose Local File", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Demo track fast track helper
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Don't have a local stem file?",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                                TextButton(
                                    onClick = { viewModel.setUploadedFile("Acoustic_Guitar_Funk_Session_115.wav", "18.4 MB") },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(24.dp).testTag("demo_track_btn")
                                ) {
                                    Text("Load 18.4MB Studio Demo Track", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Loaded file details state
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1528)),
                                border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = Icons.Default.Audiotrack,
                                            contentDescription = "Audio track loaded",
                                            tint = Color(0xFFEF5350),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = uploadedFileName!!,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Size: ${uploadedFileSize!!} • Local Path Staging",
                                                fontSize = 9.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.setUploadedFile(null, null) },
                                        modifier = Modifier.size(24.dp).testTag("clear_uploaded_file")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove File",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Automated Tempo Analyzer Status Block
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                                border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("tempo_detection_module")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Timer,
                                                contentDescription = "Tempo detection icon",
                                                tint = Color(0xFFE040FB),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "AUTOMATED TEMPO & BEAT DETECTION",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                        if (isAnalyzingTempo) {
                                            Text(
                                                text = "ANALYZING...",
                                                fontSize = 8.sp,
                                                color = Color(0xFFE040FB),
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier
                                                    .background(Color(0xFFE040FB).copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "LOCKED",
                                                fontSize = 8.sp,
                                                color = Color(0xFF10B981),
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier
                                                    .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    if (isAnalyzingTempo) {
                                        // Progress Bar
                                        LinearProgressIndicator(
                                            progress = { tempoDetectionProgress },
                                            color = Color(0xFFE040FB),
                                            trackColor = Color(0xFF132F52),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        // Scrolling log lines
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .background(Color.Black, RoundedCornerShape(6.dp))
                                                .border(1.dp, Color(0xFF132F52), RoundedCornerShape(6.dp))
                                                .padding(6.dp)
                                        ) {
                                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                items(tempoDetectionLogs) { logLine ->
                                                    Text(
                                                        text = "> $logLine",
                                                        fontSize = 9.sp,
                                                        color = Color(0xFFCBD5E1),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Analysis Completed - Show Detected BPM
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "ESTIMATED AVERAGE TEMPO",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFF64748B)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                val bpmValue = viewModel.bpm.value
                                                Text(
                                                    text = "$bpmValue BPM",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFFE040FB)
                                                )
                                            }
                                            
                                            // Real-time flashing beat indicator
                                            val infiniteBeat = rememberInfiniteTransition(label = "beat")
                                            val currentBpmVal = viewModel.bpm.value
                                            val beatScale by infiniteBeat.animateFloat(
                                                initialValue = 1.0f,
                                                targetValue = 1.25f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(durationMillis = (60_000 / currentBpmVal).coerceIn(200, 1500), easing = FastOutSlowInEasing),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "beat_scale"
                                            )
                                            
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .graphicsLayer(scaleX = beatScale, scaleY = beatScale)
                                                    .background(Color(0xFFE040FB).copy(alpha = 0.15f), CircleShape)
                                                    .border(1.dp, Color(0xFFE040FB), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Favorite,
                                                    contentDescription = "Pulse beat icon",
                                                    tint = Color(0xFFE040FB),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "✓ Pulse-train periodicity verified. BPM added automatically to current active session metronome.",
                                            fontSize = 9.sp,
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Speed mode pickers
                            Text(
                                text = "SELECT NEURAL SPEED MODE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5E718B),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val modes = listOf("Fast", "Balanced", "High Quality", "Studio Quality")
                                modes.forEach { m ->
                                    val isPicked = processingMode == m
                                    Button(
                                        onClick = { processingMode = m },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPicked) Color(0xFF00E5FF) else Color(0xFF13243C),
                                            contentColor = if (isPicked) Color.Black else Color.White
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                    ) {
                                        Text(text = m, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.runStemSeparation(processingMode) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("trigger_stem_split_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SettingsVoice,
                                    contentDescription = "Split",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PROCEED WITH AI SEPARATION SEQUENCE", fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                    is StemSeparationState.Processing -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF00E5FF)
                                )
                                Text(
                                    text = "De-mixing track into individual elements (${state.mode} mode)...",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { state.progress },
                                color = Color(0xFF00E5FF),
                                trackColor = Color(0xFF132F52),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Running FFT analysis and voice-gate masking • ETA ${state.etaSeconds}s",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    is StemSeparationState.Success -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UNMIX CHANNELS READY",
                                        fontSize = 11.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                TextButton(
                                    onClick = { 
                                        viewModel.engine.resetStemSeparation() 
                                    },
                                    modifier = Modifier.testTag("reset_separator_btn")
                                ) {
                                    Text("Load New File", color = Color(0xFFEF5350), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Loaded File label in success screen
                            if (uploadedFileName != null) {
                                Text(
                                    text = "Separated from track: ${uploadedFileName!!} (${uploadedFileSize!!})",
                                    fontSize = 9.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Playback Hub & Waveform Indicator Card
                            val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
                            val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
                            val animationState by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "equalizer_state"
                            )
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                                border = BorderStroke(1.dp, if (isPlaying) Color(0xFF00E5FF) else Color(0xFF1E3A60)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("stem_playback_hub_card")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        // Dynamic Play/Pause Button
                                        IconButton(
                                            onClick = { viewModel.toggleStemPlayback() },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(if (isPlaying) Color(0xFFEF5350) else Color(0xFF00E5FF), CircleShape)
                                                .testTag("stem_playback_toggle")
                                        ) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Playback Control",
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .background(if (isPlaying) Color(0xFF00E5FF) else Color(0xFF64748B), CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isPlaying) "PLAYING MULTI-TRACK REMIX" else "PLAYBACK STOPPED",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isPlaying) Color(0xFF00E5FF) else Color(0xFF64748B)
                                                )
                                            }
                                            // Real-time bouncing equalizer indicator
                                            Row(
                                                modifier = Modifier.padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                val heights = if (isPlaying) {
                                                    listOf(12, 6, 14, 8, 16, 10, 8, 12, 4, 10)
                                                } else {
                                                    listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2)
                                                }
                                                heights.forEachIndexed { i, h ->
                                                    val animatedHeight = animateDpAsState(
                                                        targetValue = if (isPlaying) {
                                                            (h * (0.3f + 0.7f * animationState * (1f - (i % 3) * 0.12f))).coerceIn(2f, 22f).dp
                                                        } else {
                                                            2.dp
                                                        },
                                                        label = "wave_ht_$i"
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .width(2.dp)
                                                            .height(animatedHeight.value)
                                                            .background(Color(0xFF00E5FF).copy(alpha = 0.8f))
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Active Remix badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isPlaying) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color(0xFF1E3A60).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isPlaying) "LIVE REMIXACTIVE" else "READY TO PLAY",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPlaying) Color(0xFF00E5FF) else Color(0xFF10B981)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // PROFESSIONAL 5-STEM MIXER SECTION
                            ProfessionalStemMixerSection(
                                viewModel = viewModel,
                                successState = state,
                                onOpenSendToDialog = onOpenSendToDialog
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TrackChordTimelineModule(viewModel = viewModel)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Gemini AI Study Report Box
                            Text(
                                text = "AI MUSIC STUDY & ANALYSIS REPORT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF),
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF030712), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF1E3A60), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                if (aiAnalysisResult == null) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFFE040FB))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Waiting for model analysis...", fontSize = 10.sp, color = Color(0xFF64748B))
                                    }
                                } else {
                                    Column {
                                        Text(
                                            text = aiAnalysisResult!!,
                                            fontSize = 11.sp,
                                            color = Color(0xFFE2E8F0),
                                            lineHeight = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SettingsSuggest,
                                                contentDescription = "Analysis suggestion",
                                                tint = Color(0xFFE040FB),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Dynamic analysis generated real-time via Gemini AI.",
                                                fontSize = 8.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.triggerExport("zip", null) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderZip,
                                    contentDescription = "ZIP Export",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export Separated Stems (.zip archive)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // AUDIO RESTORATION SUITE CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AUDIO RESTORATION & MASTERING DESK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFB0BEC5),
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Offline smart-algorithm cleaning filters for low-quality mic inputs or outdoor guitar tracks.",
                    fontSize = 11.sp,
                    color = Color(0xFF5E718B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Checkboxes logic
                RestorationSwitchRow(label = "Intelligent Ambient Noise Reduction", desc = "Dampens wind noise, outdoor hum floor", checked = nrEnabled, onCheckedChange = { viewModel.engine.toggleNoiseReduction() })
                RestorationSwitchRow(label = "Line Hum & Hiss Suppressor", desc = "Removes 50Hz/60Hz guitar amplifier buzzing", checked = humEnabled, onCheckedChange = { viewModel.engine.toggleHumRemoval() })
                RestorationSwitchRow(label = "Digital Clipping Repair", desc = "Intelligent reconstructor interpolates audio peaks", checked = clipEnabled, onCheckedChange = { viewModel.engine.toggleClippingRepair() })
                RestorationSwitchRow(label = "Vocal Formant Enhancer", desc = "Emphasizes main mids for lyrics analysis clarity", checked = vocalEnabled, onCheckedChange = { viewModel.engine.toggleVocalEnhancement() })
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun MixerFaderRow(label: String, volume: Float, onVolumeChange: (Float) -> Unit, onMuteToggle: () -> Unit) {
    val isEnabled = volume > 0.01f
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isEnabled) Color.White else Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = String.format("%.0f%%", volume * 100),
                    fontSize = 11.sp,
                    color = if (isEnabled) Color(0xFF00E5FF) else Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (volume <= 0.01f) {
                                onMuteToggle()
                            }
                        } else {
                            if (volume > 0.01f) {
                                onMuteToggle()
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E5FF),
                        checkedTrackColor = Color(0xFF13233C),
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = Color(0xFF0F172A)
                    ),
                    modifier = Modifier
                        .scale(0.7f)
                        .testTag("switch_${label.lowercase().replace("/", "").replace(" ", "_")}")
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                enabled = isEnabled,
                colors = SliderDefaults.colors(
                    thumbColor = if (isEnabled) Color(0xFF00E5FF) else Color(0xFF475569),
                    activeTrackColor = if (isEnabled) Color(0xFF00E5FF) else Color(0xFF334155),
                    inactiveTrackColor = Color(0xFF132F52)
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (volume <= 0.01f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Mute Toggle",
                    tint = if (volume <= 0.01f) Color(0xFFEF5350) else Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RestorationSwitchRow(
    label: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = desc, fontSize = 11.sp, color = Color(0xFFB0BEC5))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00E5FF),
                checkedTrackColor = Color(0xFF13233C)
            )
        )
    }
}

// -----------------------------------------------------------------
// 9. LIBRARY & SEARCH DATABASE TAB
// -----------------------------------------------------------------
@Composable
fun LibrarySessionTab(
    viewModel: WorkstationViewModel,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val licks by viewModel.allLicks.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Recordings") } // Recordings vs Sessions vs Licks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030A16))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle view
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0C1424), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { selectedTab = "Recordings" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "Recordings") Color(0xFF00E5FF) else Color.Transparent,
                    contentColor = if (selectedTab == "Recordings") Color.Black else Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
            ) {
                Text("Recording Storage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { selectedTab = "Sessions" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "Sessions") Color(0xFF00E5FF) else Color.Transparent,
                    contentColor = if (selectedTab == "Sessions") Color.Black else Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
            ) {
                Text("Saved Sessions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { selectedTab = "Licks" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "Licks") Color(0xFF00E5FF) else Color.Transparent,
                    contentColor = if (selectedTab == "Licks") Color.Black else Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
            ) {
                Text("Licks Library", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Contents
        if (selectedTab == "Recordings") {
            RecordingLibraryScreen(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        } else if (selectedTab == "Sessions") {
            val filteredSessions = sessions.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.categoryTags.contains(searchQuery, ignoreCase = true) ||
                it.keySignature.contains(searchQuery, ignoreCase = true)
            }

            if (filteredSessions.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No sessions found matching search.", color = Color(0xFFB0BEC5), fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSessions) { session ->
                        SessionWorkstationItemRow(
                            session = session,
                            onDelete = { viewModel.deleteSession(session) },
                            onExport = { viewModel.triggerExport("json", session) },
                            onExportMidi = { viewModel.triggerExport("midi", session) },
                            onOpenSendToDialog = onOpenSendToDialog
                        )
                    }
                }
            }
        } else {
            // African recognized guitar phrases
            val filteredLicks = licks.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.genre.contains(searchQuery, ignoreCase = true) ||
                it.notes.contains(searchQuery, ignoreCase = true)
            }

            if (filteredLicks.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No guitar phrases recorded yet.", color = Color(0xFFB0BEC5), fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredLicks) { lick ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1424)),
                            border = BorderStroke(1.dp, Color(0xFF132F52)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = lick.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(
                                            text = "Style: ${lick.genre}  •  ${lick.bpm} BPM",
                                            fontSize = 11.sp,
                                            color = Color(0xFF00E5FF),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleLickFavorite(lick) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (lick.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorited",
                                            tint = if (lick.isFavorite) Color.Red else Color(0xFF5E718B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Notes representation string
                                Text(
                                    text = "Notes Run: ${lick.notes}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD4AF37),
                                    modifier = Modifier
                                        .background(Color(0xFF04060C), RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = String.format("Confidence: %.1f%%", lick.confidence * 100),
                                        fontSize = 10.sp,
                                        color = Color(0xFFB0BEC5)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        UniversalSendToButton(
                                            sourceName = lick.title,
                                            buttonText = "SEND TO",
                                            accentColor = Color(0xFFD4AF37),
                                            onOpenSendToDialog = onOpenSendToDialog
                                        )

                                        TextButton(
                                            onClick = { viewModel.triggerExportLick(lick) },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Export MIDI", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                        }

                                        TextButton(
                                            onClick = { viewModel.deleteLick(lick) },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Delete", color = Color(0xFFEF5350), fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// -----------------------------------------------------------------
// 10. SETTINGS & MUSIC THEORY ASSISTANT INJECTOR
// -----------------------------------------------------------------
@Composable
fun SettingsTab(viewModel: WorkstationViewModel) {
    val quizQuestion by viewModel.quizQuestion.collectAsStateWithLifecycle()
    val quizOpt by viewModel.quizOptions.collectAsStateWithLifecycle()
    val quizFeed by viewModel.quizFeedback.collectAsStateWithLifecycle()
    val exportLog by viewModel.exportLog.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var customNotesName by remember { mutableStateOf("") }
    var selectedExportFormat by remember { mutableStateOf("pdf") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030A16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // JETPACK DATASTORE USER PREFERENCES & STATE FLAGS
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A192F)),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("datastore_user_preferences_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "JETPACK DATASTORE USER PREFERENCES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981),
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Asynchronous DataStore persistence engine managing user preferences, active module state flags, and the session recovery trigger.",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Preferences summary grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF040C1A), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Global Key Signature:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text(userPrefs.globalKeySignature, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    }
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Engine BPM:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text("${userPrefs.bpm} BPM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Module ID:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text(userPrefs.activeModuleId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA855F7))
                    }
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Detection Mode:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text(userPrefs.detectionMode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    }
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Resume Dialog Trigger Flag:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text(
                            if (userPrefs.shouldShowResumeDialog && userPrefs.hasUnsavedSession) "ACTIVE (Will Prompt)" else "CLEARED / INACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (userPrefs.shouldShowResumeDialog && userPrefs.hasUnsavedSession) Color(0xFF10B981) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.userPreferencesRepository.setUnsavedSessionData(
                                    title = "Unfinished DataStore Test Session",
                                    bpm = 132,
                                    key = "F# Minor",
                                    chords = "F#m, C#m, D, E",
                                    hasUnsaved = true,
                                    showDialog = true
                                )
                                viewModel.saveCurrentModuleStateImmediately()
                            }
                        },
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trigger_resume_dialog_btn")
                    ) {
                        Text("FLAG RESUME DIALOG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.userPreferencesRepository.clearResumeDialogTrigger()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("clear_resume_dialog_btn")
                    ) {
                        Text("CLEAR TRIGGER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // AI MUSIC THEORY ESSENTIALS
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI MUSIC THEORY & EAR TRAINING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Hone your ear recognition with customized, interactive modal, progression, and interval questions designed offline.",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quiz Panel Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF04060C), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Ear Training Challenge:", fontSize = 11.sp, color = Color(0xFF5E718B), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = quizQuestion, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Choices grid-button
                        quizOpt.forEach { opt ->
                            Button(
                                onClick = { viewModel.answerQuiz(opt) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF13233C)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(opt, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }

                        // Feedback row
                        if (quizFeed != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = quizFeed!!,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (quizFeed!!.startsWith("Correct")) Color.Green else Color(0xFFEF5350)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { viewModel.loadNewQuiz() },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Next Question →", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // FULL REPORT EXPORT CENTER
        ExportCenterScreen(viewModel = viewModel)

        // Credit notice
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF061021)),
            border = BorderStroke(1.dp, Color(0xFF13233C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Designed and Built by Joseph Hilary Zulukwa",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4AF37)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hear the Notes. Understand the Music. Powered by AI.",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All machine learning processing, sound separation models, and music analysis runs fully on-device natively in high-performance Kotlin & Java.",
                    fontSize = 9.sp,
                    color = Color(0xFF5E718B),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // Modal popup loader for Export Logs representation
    if (exportLog != null) {
        Dialog(onDismissRequest = { viewModel.dismissExportLog() }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1424)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HZ SYSTEM EXPORT DESK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF04060C), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = exportLog!!,
                            fontSize = 11.sp,
                            color = Color(0xFF00E5FF),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.dismissExportLog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52))
                    ) {
                        Text("Dismiss Console", color = Color.White)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 11. COMMON MODALS & OVERLAYS IMPORTS
// -----------------------------------------------------------------
@Composable
fun AudioRecorderDialog(
    viewModel: WorkstationViewModel,
    onDismiss: () -> Unit
) {
    val isRec by viewModel.isRecording.collectAsStateWithLifecycle()
    val timerSecs by viewModel.recordingTimerSeconds.collectAsStateWithLifecycle()

    var sessionTitle by remember { mutableStateOf("New Studio Recording") }
    var keySignature by remember { mutableStateOf("C Major") }

    val context = LocalContext.current
    val activity = context as? Activity
    var showRationale by remember { mutableStateOf(false) }
    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleRecording(sessionTitle, "WAV", "Manual on-device workstation mic recording.")
        } else {
            if (activity != null && !AudioPermissionHelper.shouldShowPermissionRationale(activity)) {
                isPermanentlyDenied = true
            }
            showRationale = true
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1424)),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECORDING WORKSTATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Red,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Divider(color = Color.Red.copy(alpha = 0.3f))

                OutlinedTextField(
                    value = sessionTitle,
                    onValueChange = { sessionTitle = it },
                    label = { Text("Recording Title") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Red),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Render Timer
                Text(
                    text = formatSeconds(timerSecs),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isRec) Color.Red else Color.White
                )

                // High-fidelity recording bounce meter Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .background(Color(0xFF04060C), RoundedCornerShape(4.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val spacing = size.width / 15
                        val bounceY = if (isRec) sin(timeSeedScale(timerSecs)) else 0f
                        for (i in 0 until 15) {
                            val ampHeight = if (isRec) (10f + (i % 4) * 8f * (1f + bounceY)) else 4f
                            drawCircle(
                                color = if (i > 11) Color.Red else if (i > 8) Color.Yellow else Color.Green,
                                radius = 6f,
                                center = Offset((i + 0.5f) * spacing, size.height/2 + (sin((i + timerSecs).toFloat()) * ampHeight * 0.1f))
                            )
                        }
                    }
                }

                Text(
                    text = "Clipping protection metrics active. Local standard: FLAC format, 48kHz, 24-bit PCM.",
                    fontSize = 9.sp,
                    color = Color(0xFF5E718B),
                    textAlign = TextAlign.Center
                )

                // Control Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (isRec) {
                                viewModel.toggleRecording(sessionTitle, "WAV", "Manual on-device workstation mic recording.")
                            } else {
                                if (AudioPermissionHelper.hasRecordAudioPermission(context)) {
                                    viewModel.toggleRecording(sessionTitle, "WAV", "Manual on-device workstation mic recording.")
                                } else {
                                    if (activity != null && AudioPermissionHelper.shouldShowPermissionRationale(activity)) {
                                        isPermanentlyDenied = false
                                        showRationale = true
                                    } else {
                                        permissionLauncher.launch(AudioPermissionHelper.RECORD_AUDIO_PERMISSION)
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRec) Color(0xFFEF5350) else Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (isRec) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRec) "Pause Rec" else "Start Rec", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            if (isRec) {
                                viewModel.toggleRecording(sessionTitle, "WAV", "Manual on-device workstation mic recording.")
                            }
                            // Save to Room db
                            viewModel.addSession(
                                title = sessionTitle,
                                bpm = viewModel.bpm.value,
                                key = keySignature,
                                notes = "Manual on-device workstation mic recording.",
                                chords = "C, G, Am, F",
                                tags = "Mic, Stereo, WAV"
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save & Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showRationale) {
        AudioPermissionRationaleDialog(
            onDismiss = { showRationale = false },
            onRequestPermission = {
                showRationale = false
                permissionLauncher.launch(AudioPermissionHelper.RECORD_AUDIO_PERMISSION)
            },
            onOpenSettings = {
                showRationale = false
                AudioPermissionHelper.launchAppSettings(context)
            },
            isPermanentlyDenied = isPermanentlyDenied
        )
    }
}

@Composable
fun AddSessionDialog(
    viewModel: WorkstationViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("New Song Project") }
    var bpmVal by remember { mutableStateOf("120") }
    var keySelection by remember { mutableStateOf("C Major") }
    var notesInput by remember { mutableStateOf("") }
    var customTags by remember { mutableStateOf("Manual") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1424)),
            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEW HZ WORKSTATION PROJECT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Divider(color = Color(0xFF00E5FF).copy(alpha = 0.3f))

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project / Song Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bpmVal, onValueChange = { bpmVal = it }, label = { Text("Tempo (BPM)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = keySelection, onValueChange = { keySelection = it }, label = { Text("Key Signature") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notesInput, onValueChange = { notesInput = it }, label = { Text("Session Description / Notes") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = customTags, onValueChange = { customTags = it }, label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val parsedBpm = bpmVal.toIntOrNull() ?: 120
                        viewModel.addSession(title, parsedBpm, keySelection, notesInput, "G, C, F, C", customTags)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Provision Workstation Project", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ImportAudioDialog(
    viewModel: WorkstationViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedSource by remember { mutableStateOf("All Storage Sources") }
    var isLoading by remember { mutableStateOf(false) }
    var previewPlaying by remember { mutableStateOf(false) }

    var currentMetadata by remember {
        mutableStateOf(
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "sungura_master_lead_135bpm.flac",
                artist = "Franco & OK Jazz",
                album = "Kinshasa Sessions Vol 2",
                durationFormatted = "04:18",
                bitrateKbps = "1412 kbps",
                sampleRateHz = "96.0 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "28.4 MB",
                formatExtension = "FLAC",
                sourceType = "SD Card",
                bpm = 135,
                key = "A Major"
            )
        )
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isLoading = true
            coroutineScope.launch {
                delay(300)
                val meta = UniversalAudioMetadataExtractor.extractMetadataFromUri(
                    context = context,
                    uri = uri,
                    sourceType = if (selectedSource == "All Storage Sources") "File Manager" else selectedSource
                )
                currentMetadata = meta
                isLoading = false
            }
        }
    }

    val storagePresets = remember {
        listOf(
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "sungura_master_lead.flac",
                artist = "Franco & OK Jazz",
                album = "Kinshasa Sessions",
                durationFormatted = "04:18",
                bitrateKbps = "1412 kbps",
                sampleRateHz = "96.0 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "28.4 MB",
                formatExtension = "FLAC",
                sourceType = "SD Card",
                bpm = 135,
                key = "A Major"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "seben_guitar_rhythm.wav",
                artist = "Zaiko Langa Langa",
                album = "Rumba Wave",
                durationFormatted = "03:45",
                bitrateKbps = "1536 kbps",
                sampleRateHz = "48.0 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "41.2 MB",
                formatExtension = "WAV",
                sourceType = "Downloads",
                bpm = 138,
                key = "F# Major"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "rhumba_brass_section.mp3",
                artist = "Tabu Ley Rochereau",
                album = "African Soukous Classics",
                durationFormatted = "05:12",
                bitrateKbps = "320 kbps",
                sampleRateHz = "44.1 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "11.8 MB",
                formatExtension = "MP3",
                sourceType = "Internal Storage",
                bpm = 124,
                key = "G Major"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "highlife_acoustic_solo.m4a",
                artist = "Ebo Taylor & Band",
                album = "Saltpond Highlife",
                durationFormatted = "03:10",
                bitrateKbps = "256 kbps",
                sampleRateHz = "44.1 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "6.1 MB",
                formatExtension = "M4A",
                sourceType = "Music Folder",
                bpm = 118,
                key = "D Major"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "afrobeat_poly_percussion.ogg",
                artist = "Fela Anikulapo Kuti",
                album = "Shrine Masters",
                durationFormatted = "06:30",
                bitrateKbps = "320 kbps",
                sampleRateHz = "48.0 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "15.2 MB",
                formatExtension = "OGG",
                sourceType = "External USB Storage",
                bpm = 122,
                key = "E Minor"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "gospel_praise_chords.aac",
                artist = "Soweto Gospel Group",
                album = "Divine Harmonies",
                durationFormatted = "04:02",
                bitrateKbps = "256 kbps",
                sampleRateHz = "44.1 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "7.8 MB",
                formatExtension = "AAC",
                sourceType = "Recent Files",
                bpm = 112,
                key = "C Major"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "jazz_fusion_improvisation.aiff",
                artist = "Hugh Masekela",
                album = "Graret Town Sessions",
                durationFormatted = "05:45",
                bitrateKbps = "1412 kbps",
                sampleRateHz = "44.1 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "58.1 MB",
                formatExtension = "AIFF",
                sourceType = "File Manager",
                bpm = 104,
                key = "B Minor"
            ),
            UniversalAudioMetadataExtractor.createPresetMetadata(
                fileName = "studio_archival_tape.wma",
                artist = "Master Audio Lab",
                album = "HZ Acoustic Archive",
                durationFormatted = "02:50",
                bitrateKbps = "192 kbps",
                sampleRateHz = "44.1 kHz",
                channels = "Stereo (2 ch)",
                fileSize = "4.2 MB",
                formatExtension = "WMA",
                sourceType = "Internal Storage",
                bpm = 120,
                key = "A Minor"
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091220)),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(8.dp)
                .testTag("universal_audio_import_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Title & Close Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Universal Audio Import System",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "UNIVERSAL AUDIO IMPORT SYSTEM™",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Extract metadata, create project & deploy to all analysis modules offline",
                                fontSize = 10.sp,
                                color = Color(0xFF8C9BAE)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Storage Source Filter Chips Row
                val sourceTypes = listOf(
                    "All Storage Sources",
                    "Internal Storage",
                    "SD Card",
                    "Downloads",
                    "Music Folder",
                    "Recent Files",
                    "External USB Storage",
                    "File Manager"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sourceTypes) { src ->
                        val isSel = selectedSource == src
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSel) Color(0xFF00E5FF) else Color(0xFF132338),
                            border = BorderStroke(0.5.dp, if (isSel) Color(0xFF00E5FF) else Color(0xFF233B58)),
                            modifier = Modifier.clickable {
                                selectedSource = src
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = when (src) {
                                        "SD Card" -> Icons.Default.SdCard
                                        "Downloads" -> Icons.Default.Download
                                        "Music Folder" -> Icons.Default.LibraryMusic
                                        "External USB Storage" -> Icons.Default.Usb
                                        "Recent Files" -> Icons.Default.History
                                        else -> Icons.Default.Folder
                                    },
                                    contentDescription = src,
                                    tint = if (isSel) Color.Black else Color(0xFF00E5FF),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = src,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Drag & Drop / System Picker Zone
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF08192E)),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { filePickerLauncher.launch("audio/*") }
                        .testTag("drag_drop_zone")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileOpen,
                                contentDescription = "Open Storage",
                                tint = Color(0xFFD4AF37),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "BROWSE DEVICE STORAGE & FILE MANAGER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37)
                            )
                        }

                        Text(
                            text = "Drag & drop audio file here or tap to select from Internal Storage, SD Card, Downloads, Music, or USB",
                            fontSize = 10.sp,
                            color = Color(0xFFB0BEC5),
                            textAlign = TextAlign.Center
                        )

                        // Supported Formats Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf("MP3", "WAV", "FLAC", "AAC", "OGG", "M4A", "AIFF", "WMA").forEach { fmt ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF102848),
                                    border = BorderStroke(0.5.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = fmt,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00E5FF),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content area (Selected File Details + Waveform + Preset List)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(color = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
                                Text("Reading file header & extracting metadata offline...", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    } else {
                        // SELECTED FILE METADATA & WAVEFORM PREVIEW CARD
                        val meta = currentMetadata
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F233C)),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Header Badge Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF00E5FF),
                                    ) {
                                        Text(
                                            text = "${meta.formatExtension} • ${meta.sourceType}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = "SELECTED TRACK DETAILS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8C9BAE)
                                    )
                                }

                                // File Name, Artist & Album
                                Column {
                                    Text(
                                        text = meta.fileName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Artist: ${meta.artist}",
                                            fontSize = 10.sp,
                                            color = Color(0xFFD4AF37)
                                        )
                                        Text(
                                            text = "• Album: ${meta.album}",
                                            fontSize = 10.sp,
                                            color = Color(0xFFB0BEC5)
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color(0xFF1E3A60), thickness = 0.5.dp)

                                // 8 Metadata Fields Display Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    ImportMetadataItem("Duration", meta.durationFormatted, Icons.Default.Schedule)
                                    ImportMetadataItem("Bitrate", meta.bitrateKbps, Icons.Default.Speed)
                                    ImportMetadataItem("Sample Rate", meta.sampleRateHz, Icons.Default.GraphicEq)
                                    ImportMetadataItem("Channels", meta.channels, Icons.Default.SurroundSound)
                                    ImportMetadataItem("File Size", meta.fileSize, Icons.Default.Storage)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Waveform Preview Canvas
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "OFFLINE WAVEFORM PREVIEW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00E5FF)
                                    )

                                    IconButton(
                                        onClick = { previewPlaying = !previewPlaying },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (previewPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Preview Audio",
                                            tint = Color(0xFF00E5FF)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .background(Color(0xFF081424), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    val amps = meta.waveformAmplitudes
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        if (amps.isNotEmpty()) {
                                            val barWidth = size.width / amps.size
                                            amps.forEachIndexed { index, amp ->
                                                val barHeight = size.height * amp
                                                val x = index * barWidth
                                                val y = (size.height - barHeight) / 2f
                                                val color = if (index < (amps.size * if (previewPlaying) 0.6f else 0.35f)) Color(0xFF00E5FF) else Color(0xFF2A4B75)
                                                drawRect(
                                                    color = color,
                                                    topLeft = Offset(x, y),
                                                    size = Size(barWidth * 0.7f, barHeight)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Presets List by Filter
                        Text(
                            text = "DEVICE STORAGE & OFFLINE PRESETS (${selectedSource.uppercase()})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8C9BAE)
                        )

                        val filteredPresets = if (selectedSource == "All Storage Sources") {
                            storagePresets
                        } else {
                            storagePresets.filter { it.sourceType.equals(selectedSource, ignoreCase = true) }.ifEmpty { storagePresets }
                        }

                        filteredPresets.forEach { item ->
                            val isSelected = item.fileName == meta.fileName
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF102D4E) else Color(0xFF0D1C30)
                                ),
                                border = BorderStroke(
                                    0.5.dp,
                                    if (isSelected) Color(0xFF00E5FF) else Color(0xFF1D3B60)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentMetadata = item
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (item.formatExtension) {
                                                "FLAC", "WAV", "AIFF" -> Color(0xFF00E5FF)
                                                "MP3", "AAC", "M4A" -> Color(0xFFD4AF37)
                                                else -> Color(0xFF9C27B0)
                                            }
                                        ) {
                                            Text(
                                                text = item.formatExtension,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = item.fileName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${item.artist} • ${item.sourceType} • ${item.durationFormatted} • ${item.fileSize}",
                                                fontSize = 9.sp,
                                                color = Color(0xFF8C9BAE)
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Primary Import & Create Project Button
                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            delay(600)
                            viewModel.importUniversalAudio(currentMetadata)
                            isLoading = false
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("confirm_import_and_create_project_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Create Project",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "IMPORT & AUTOMATICALLY CREATE PROJECT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportMetadataItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF00E5FF), modifier = Modifier.size(10.dp))
            Text(text = label, fontSize = 8.sp, color = Color(0xFF8C9BAE))
        }
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

// -----------------------------------------------------------------
// 12. HELPER UTILITIES
// -----------------------------------------------------------------
fun formatSeconds(totalSecs: Int): String {
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}

@Composable
fun TrackChordTimelineModule(viewModel: WorkstationViewModel) {
    val timeline by viewModel.trackChordTimeline.collectAsStateWithLifecycle()
    val playPositionMs by viewModel.playbackPositionMs.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val globalKeySignature by viewModel.globalKeySignature.collectAsStateWithLifecycle()

    if (timeline.isEmpty()) return

    val listState = rememberLazyListState()
    var autoScrollEnabled by remember { mutableStateOf(true) }

    // Find active chord index
    val activeIndex = (playPositionMs / 3000L).toInt().coerceIn(0, timeline.size - 1)

    // Smoothly scroll to the active item in real-time
    LaunchedEffect(activeIndex, autoScrollEnabled) {
        if (autoScrollEnabled && timeline.isNotEmpty()) {
            listState.animateScrollToItem(activeIndex)
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071124)),
        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("track_chord_timeline_module_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with status details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REAL-TIME CHORD RECOGNITION TIMELINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Synchronized analysis of current audio track",
                        fontSize = 9.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { autoScrollEnabled = !autoScrollEnabled },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Toggle Auto-Scroll",
                            tint = if (autoScrollEnabled) Color(0xFF00E5FF) else Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (autoScrollEnabled) "Autofollow" else "Manual",
                        fontSize = 8.sp,
                        color = if (autoScrollEnabled) Color(0xFF00E5FF) else Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key info banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF132F52), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Key Signature",
                        tint = Color(0xFFE040FB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Detected Key Signature:",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = globalKeySignature ?: "Analyzing...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE040FB)
                    )
                }

                val activeChord = timeline.getOrNull(activeIndex)
                if (activeChord != null) {
                    val confPercent = (activeChord.confidence * 100).toInt()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Confidence: $confPercent%",
                            fontSize = 9.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Timeline list of chord entries
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scrollable_chord_timeline_lazyrow"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
            ) {
                itemsIndexed(timeline) { index, entry ->
                    val isActive = index == activeIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isActive) 1.03f else 1.0f,
                        label = "card_scale"
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) Color(0xFF0F1E36) else Color(0xFF080D1A)
                        ),
                        border = BorderStroke(
                            width = if (isActive) 1.5.dp else 1.dp,
                            color = if (isActive) Color(0xFF00E5FF) else Color(0xFF132F52)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(135.dp)
                            .scale(scale)
                            .clickable {
                                viewModel.setPlaybackPosition(entry.timeMs)
                            }
                            .testTag("timeline_chord_item_$index")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            // Top Row: Time Badge and Indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = entry.timestampLabel,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                if (isActive && isPlaying) {
                                    val pulseAnim by rememberInfiniteTransition(label = "").animateFloat(
                                        initialValue = 0.4f,
                                        targetValue = 1.0f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(600, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "pulse"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFFEF5350).copy(alpha = pulseAnim), CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = entry.chordSymbol,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isActive) Color(0xFF00E5FF) else Color.White
                            )

                            Text(
                                text = entry.chordName,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isActive) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "NOTES FOUND",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                entry.notes.take(4).forEach { note ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isActive) Color(0xFF00E5FF).copy(alpha = 0.12f) else Color(0xFF1E293B),
                                                RoundedCornerShape(3.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = note,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isActive) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val durationOfSegment = 3000L
                            val relativeProgress = ((playPositionMs - entry.timeMs).toFloat() / durationOfSegment).coerceIn(0f, 1f)
                            if (isActive) {
                                LinearProgressIndicator(
                                    progress = { relativeProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .clip(RoundedCornerShape(1.dp)),
                                    color = Color(0xFF00E5FF),
                                    trackColor = Color(0xFF1E293B)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UniversalAudioSharingEngineCard(
    viewModel: WorkstationViewModel
) {
    val sharingState by viewModel.universalAudioSharingState.collectAsStateWithLifecycle()
    var selectedModuleId by remember { mutableStateOf("import") }

    val selectedModule = sharingState.modulesChain.find { it.id == selectedModuleId }
        ?: sharingState.modulesChain.first()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF030914)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("universal_audio_sharing_engine_card")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Universal Audio Sharing Engine",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "UNIVERSAL AUDIO SHARING ENGINE™",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Direct zero-copy audio routing across all 12 modules • Single Project Session",
                            fontSize = 9.sp,
                            color = Color(0xFF8C9BAE)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, Color(0xFF00E5FF))
                ) {
                    Text(
                        text = "12 MODULES LINKED",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Shared Project & Audio Memory Pointer Info Bar
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF091628)),
                border = BorderStroke(0.5.dp, Color(0xFF1E3A60)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sharingState.activeProjectTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "Source: ${sharingState.audioSourceFileName} • ${sharingState.sampleRateHz}Hz • ${sharingState.bitrateKbps}kbps",
                            fontSize = 9.sp,
                            color = Color(0xFFD4AF37)
                        )
                        Text(
                            text = "Engine: ${sharingState.oboeBackendName} • ${String.format("%.1f", sharingState.oboeLatencyMs)}ms Latency",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00E5FF)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (sharingState.oboeEngineRunning) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (sharingState.oboeEngineRunning) "OBOE STREAM LIVE" else "OBOE STANDBY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (sharingState.oboeEngineRunning) Color(0xFF4CAF50) else Color.LightGray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "DUP FILES: ${sharingState.duplicateFilesCreated}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = sharingState.sharedBufferMemoryRef,
                            fontSize = 8.sp,
                            color = Color(0xFF8C9BAE)
                        )
                    }
                }
            }

            // Synchronized Audio Timeline Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF081424), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.toggleSharingPlayback() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (sharingState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Sync Transport Play Pause",
                        tint = Color(0xFF00E5FF)
                    )
                }

                val currentMs = sharingState.currentPlayheadMs
                val totalMs = sharingState.audioDurationMs.coerceAtLeast(1000L)
                val curSec = (currentMs / 1000).toInt()
                val totSec = (totalMs / 1000).toInt()

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "SYNCHRONIZED AUDIO TIMELINE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                        Text(text = "${formatSeconds(curSec)} / ${formatSeconds(totSec)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Slider(
                        value = currentMs.toFloat(),
                        onValueChange = { viewModel.setSharingPlayheadMs(it.toLong()) },
                        valueRange = 0f..totalMs.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF00E5FF),
                            inactiveTrackColor = Color(0xFF1E3A60)
                        ),
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            // 12-Module Sequential Pipeline Flow Diagram
            Text(
                text = "ROUTING MATRIX (TAP MODULE TO INSPECT & PASS-THROUGH)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8C9BAE)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(sharingState.modulesChain) { idx, stage ->
                    val isSelected = stage.id == selectedModuleId

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF00E5FF) else if (stage.isBypassed) Color(0xFF261D08) else Color(0xFF0D1E36),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF00E5FF) else if (stage.isBypassed) Color(0xFFFFB300) else Color(0xFF1E3A60)
                        ),
                        modifier = Modifier.clickable { selectedModuleId = stage.id }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "${idx + 1}.",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.Black else Color(0xFF00E5FF)
                                )
                                Text(
                                    text = stage.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                            Text(
                                text = if (stage.isBypassed) "BYPASSED" else "ACTIVE (0ms)",
                                fontSize = 8.sp,
                                color = if (isSelected) Color.Black.copy(alpha = 0.8f) else if (stage.isBypassed) Color(0xFFFFB300) else Color(0xFF4CAF50)
                            )
                        }
                    }

                    if (idx < sharingState.modulesChain.size - 1) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Routing Arrow",
                            tint = Color(0xFF00E5FF).copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Detailed Inspector Drawer for Selected Module
            val currStage = selectedModule
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF09162A)),
                border = BorderStroke(0.5.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MODULE INSPECTOR: ${currStage.name.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E5FF)
                            )
                            Text(
                                text = currStage.description,
                                fontSize = 9.sp,
                                color = Color(0xFFB0BEC5)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (currStage.isBypassed) "BYPASS" else "PASS-THROUGH",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currStage.isBypassed) Color(0xFFFFB300) else Color(0xFF4CAF50)
                            )
                            Switch(
                                checked = !currStage.isBypassed,
                                onCheckedChange = { viewModel.toggleSharingModuleBypass(currStage.id) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF00E5FF),
                                    checkedTrackColor = Color(0xFF102848),
                                    uncheckedThumbColor = Color(0xFFFFB300),
                                    uncheckedTrackColor = Color(0xFF261D08)
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ${currStage.statusMessage}",
                            fontSize = 9.sp,
                            color = Color(0xFF8C9BAE)
                        )
                        Text(
                            text = "Latency: ${currStage.syncLatencyMs} ms (Sync Lock)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    // Gain Trim Control
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Gain Trim:", fontSize = 9.sp, color = Color.White)
                        Slider(
                            value = currStage.gainDb,
                            onValueChange = { viewModel.setSharingModuleGain(currStage.id, it) },
                            valueRange = -12f..12f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFD4AF37),
                                activeTrackColor = Color(0xFFD4AF37)
                            ),
                            modifier = Modifier.weight(1f).height(20.dp)
                        )
                        Text(
                            text = String.format("%+.1f dB", currStage.gainDb),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// PROFESSIONAL 5-STEM DAW MIXER COMPONENT
// -----------------------------------------------------------------
@Composable
fun ProfessionalStemMixerSection(
    viewModel: WorkstationViewModel,
    successState: StemSeparationState.Success,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val mixer = successState.mixerState

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF040A17)),
        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("professional_stem_mixer_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Stem Mixer Icon",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PROFESSIONAL 5-STEM DAW MIXER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "SYNCHRONIZED ZERO-LATENCY CHANNEL ROUTING",
                            fontSize = 8.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UniversalSendToButton(
                        sourceName = "Full 5-Stem Mix",
                        buttonText = "SEND MIX TO...",
                        accentColor = Color(0xFF00E5FF),
                        onOpenSendToDialog = onOpenSendToDialog
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // QUICK STEM ISOLATION BAR
            Text(
                text = "SOLO SINGLE STEM (QUICK ISOLATE)",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Scrollable Row for 5 stems solo isolation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val stemList = listOf(
                    Triple("guitar", "Only Guitar", "🎸"),
                    Triple("bass", "Only Bass", "🎸"),
                    Triple("piano", "Only Piano", "🎹"),
                    Triple("vocals", "Only Vocals", "🎤"),
                    Triple("drums", "Only Drums", "🥁")
                )

                stemList.forEach { (id, label, emoji) ->
                    val isSoloedOnly = mixer.channels.any { it.id == id && it.isSoloed } && mixer.channels.count { it.isSoloed } == 1
                    Button(
                        onClick = { viewModel.playOnlyStem(id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSoloedOnly) Color(0xFFFFD700) else Color(0xFF13243C),
                            contentColor = if (isSoloedOnly) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("quick_solo_$id")
                    ) {
                        Text("$emoji $label", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Clear All / Reset Button
                Button(
                    onClick = { viewModel.clearStemSoloAndMute() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                        contentColor = Color(0xFF00E5FF)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp).testTag("quick_unsolo_all")
                ) {
                    Text("🔄 All Stems On", fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MULTI-STEM COMBINATION PRESETS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val combos = listOf(
                    Pair("Guitar + Vocals", setOf("guitar", "vocals")),
                    Pair("Bass + Drums", setOf("bass", "drums")),
                    Pair("Piano + Vocals", setOf("piano", "vocals")),
                    Pair("Guitar + Bass + Drums", setOf("guitar", "bass", "drums"))
                )

                combos.forEach { (comboLabel, setIds) ->
                    OutlinedButton(
                        onClick = { viewModel.playCombinationStems(setIds) },
                        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f).height(28.dp).testTag("combo_${setIds.joinToString("_")}")
                    ) {
                        Text(comboLabel, fontSize = 7.5.sp, color = Color(0xFFCBD5E1), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 INDIVIDUAL STEM CHANNEL STRIPS
            Text(
                text = "INDIVIDUAL CHANNEL STRIPS (SOLO / MUTE / FADER / SEND TO)",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            mixer.channels.forEach { channel ->
                val isEffectiveActive = mixer.isChannelActive(channel.id)
                StemChannelStripCard(
                    channel = channel,
                    isPlaying = isPlaying,
                    isEffectiveActive = isEffectiveActive,
                    onVolumeChange = { newVol -> viewModel.engine.adjustStemVolume(channel.id, newVol) },
                    onSoloToggle = { viewModel.toggleStemSolo(channel.id) },
                    onMuteToggle = { viewModel.toggleStemMute(channel.id) },
                    onPlayOnly = { viewModel.playOnlyStem(channel.id) },
                    onOpenSendToDialog = onOpenSendToDialog
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Sync Verification Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYNCHRONIZED AUDIOTRACK STREAM • ZERO DRIFT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                val activeCount = mixer.channels.count { mixer.isChannelActive(it.id) }
                Text(
                    text = "$activeCount / 5 CHANNELS ACTIVE",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF)
                )
            }
        }
    }
}

@Composable
fun StemChannelStripCard(
    channel: StemChannelData,
    isPlaying: Boolean,
    isEffectiveActive: Boolean,
    onVolumeChange: (Float) -> Unit,
    onSoloToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    onPlayOnly: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val stemColor = Color(channel.colorHex)
    val containerBg = if (channel.isSoloed) {
        Color(0xFFFFD700).copy(alpha = 0.08f)
    } else if (channel.isMuted) {
        Color(0xFFEF5350).copy(alpha = 0.06f)
    } else {
        Color(0xFF081224)
    }

    val borderCol = if (channel.isSoloed) {
        Color(0xFFFFD700)
    } else if (channel.isMuted) {
        Color(0xFFEF5350).copy(alpha = 0.5f)
    } else {
        Color(0xFF1E3A60)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.dp, borderCol),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("stem_channel_card_${channel.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header: Emoji, Title, Freq Range & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = channel.iconEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = channel.name.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = channel.frequencyRange,
                            fontSize = 8.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Controls: Solo (S) & Mute (M) Buttons + Universal Send To
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SOLO Button
                    Button(
                        onClick = onSoloToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (channel.isSoloed) Color(0xFFFFD700) else Color(0xFF13243C),
                            contentColor = if (channel.isSoloed) Color.Black else Color(0xFFFFD700)
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp).testTag("stem_solo_${channel.id}")
                    ) {
                        Text(text = "S", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    // MUTE Button
                    Button(
                        onClick = onMuteToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (channel.isMuted) Color(0xFFEF5350) else Color(0xFF13243C),
                            contentColor = if (channel.isMuted) Color.White else Color(0xFFEF5350)
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp).testTag("stem_mute_${channel.id}")
                    ) {
                        Text(text = "M", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    // Quick Only Chip
                    OutlinedButton(
                        onClick = onPlayOnly,
                        border = BorderStroke(1.dp, stemColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier.height(28.dp).testTag("stem_only_${channel.id}")
                    ) {
                        Text("ONLY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = stemColor)
                    }

                    // Universal Send To Button
                    UniversalSendToButton(
                        sourceName = "Separated ${channel.name} Stem",
                        buttonText = "SEND TO",
                        accentColor = stemColor,
                        onOpenSendToDialog = onOpenSendToDialog
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Volume Fader Slider & Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (channel.volume <= 0.01f || channel.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Volume icon",
                    tint = if (isEffectiveActive) stemColor else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )

                Slider(
                    value = channel.volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0.0f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = stemColor,
                        activeTrackColor = stemColor,
                        inactiveTrackColor = Color(0xFF1E3A60)
                    ),
                    modifier = Modifier.weight(1f).height(24.dp).testTag("stem_fader_${channel.id}")
                )

                val gainDbText = if (channel.isMuted) {
                    "MUTED"
                } else if (channel.volume <= 0.001f) {
                    "-∞ dB"
                } else {
                    val db = (20 * Math.log10(channel.volume.toDouble())).toFloat()
                    String.format("%+.1f dB", db)
                }

                Text(
                    text = gainDbText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = if (channel.isMuted) Color(0xFFEF5350) else Color.White,
                    modifier = Modifier.widthIn(min = 45.dp)
                )
            }

            // Peak VU Level Meter
            if (isPlaying && isEffectiveActive) {
                Spacer(modifier = Modifier.height(4.dp))
                val infiniteMeter = rememberInfiniteTransition(label = "meter_${channel.id}")
                val meterValue by infiniteMeter.animateFloat(
                    initialValue = 0.2f,
                    targetValue = channel.peakLevel * channel.volume.coerceAtMost(1.0f),
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 250, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "meter_val"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF030712), RoundedCornerShape(2.dp))
                        .clip(RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(meterValue.coerceIn(0.05f, 1.0f))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF10B981), Color(0xFFFFD700), Color(0xFFEF5350))
                                )
                            )
                    )
                }
            }
        }
    }
}
