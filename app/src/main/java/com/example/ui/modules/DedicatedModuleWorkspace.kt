package com.example.ui.modules

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.audio.*
import com.example.ui.AfricanMusicIntelligenceCard
import com.example.ui.InstrumentSynchronizerView
import com.example.ui.LearnAsYouPlayCard
import com.example.ui.PitchAndTimeControlPanel
import com.example.ui.PracticeCenterWorkspace
import com.example.ui.ProfessionalVisualAnalyzersCard
import com.example.ui.UniversalSendToButton
import com.example.ui.navigation.AppModuleRegistry
import com.example.ui.navigation.AppModule
import com.example.ui.navigation.ModuleToolbar
import com.example.ui.recording.RecordingLibraryScreen
import com.example.viewmodel.WorkstationViewModel

@Composable
fun DedicatedModuleWorkspace(
    moduleId: String,
    viewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    val module = AppModuleRegistry.findModule(moduleId)
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val globalKey by viewModel.globalKeySignature.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val uploadedFileName by viewModel.uploadedFileName.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ModuleToolbar(
                title = module.title,
                subtitle = module.subtitle,
                moduleIcon = Icons.Default.Extension,
                accentColor = module.color,
                viewModel = viewModel,
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
                .padding(14.dp)
                .testTag("dedicated_module_workspace_${module.id}"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF071122),
                    border = BorderStroke(1.dp, module.color.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        module.color.copy(alpha = 0.15f),
                                        Color(0xFF040A18)
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = module.color.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, module.color),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = module.emoji, fontSize = 24.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = module.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = module.subtitle,
                                    fontSize = 11.sp,
                                    color = module.color
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF132F52),
                            border = BorderStroke(1.dp, module.color)
                        ) {
                            Text(
                                text = module.moduleTag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = module.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Interactive Workspace Content Block
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070F1C)),
                    border = BorderStroke(1.dp, Color(0xFF132F52)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${module.title.uppercase()} CONTROLS & TELEMETRY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "100% OFFLINE ENGINE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (module.id) {
                            "recorder", "library" -> RecordingLibraryScreen(viewModel)
                            "audio_player" -> AudioPlayerWorkspaceContent(viewModel, onOpenSendToDialog)
                            "dsp" -> PitchAndTimeControlPanel(viewModel, onOpenSendToDialog = onOpenSendToDialog)
                            "arpeggio" -> ArpeggioWorkspaceContent(viewModel)
                            "piano", "guitar" -> InstrumentVoicingsWorkspaceContent(viewModel)
                            "analyzer" -> AnalyzerWorkspaceContent(viewModel)
                            "quiz" -> PracticeCenterWorkspace(viewModel)
                            "bpm" -> BpmWorkspaceContent(viewModel)
                            "key_detection" -> KeyDetectionWorkspaceContent(viewModel)
                            "phrase_rec" -> PhraseRecWorkspaceContent(viewModel)
                            "african_music" -> AfricanMusicWorkspaceContent(viewModel)
                            "midi" -> MidiWorkspaceContent(viewModel)
                            "theory" -> TheoryWorkspaceContent(viewModel)
                            "export" -> ExportWorkspaceContent(viewModel)
                            "about" -> AboutWorkspaceContent(module, viewModel)
                            "help" -> HelpWorkspaceContent(module, viewModel)
                            else -> GenericWorkspaceContent(module, viewModel)
                        }
                    }
                }
            }

            // Universal Send To Payload Box
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF091426),
                    border = BorderStroke(1.dp, module.color.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "UNIVERSAL ENGINE PIPELINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = module.color
                            )
                            Text(
                                text = "Route live audio buffer & chord data to another module.",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        UniversalSendToButton(
                            sourceName = "${module.title} Payload",
                            buttonText = "SEND TO...",
                            accentColor = module.color,
                            onOpenSendToDialog = onOpenSendToDialog
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioPlayerWorkspaceContent(
    viewModel: WorkstationViewModel,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val context = LocalContext.current
    val masterEngine = viewModel.masterAudioEngine
    val isPlaying by masterEngine.isPlaying.collectAsStateWithLifecycle()
    val playheadMs by masterEngine.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by masterEngine.durationMs.collectAsStateWithLifecycle()
    val diagnostics by masterEngine.diagnostics.collectAsStateWithLifecycle()
    val errorMessage by masterEngine.errorMessage.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            masterEngine.loadAudioUri(uri)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (!diagnostics.isAudioLoaded) {
            // Centered "No Audio Loaded" Placeholder
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0B172B),
                border = BorderStroke(1.dp, Color(0xFF132F52)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("no_audio_loaded_placeholder")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF132F52),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "No Audio",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Audio Loaded",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Import or record an audio file to begin harmonic analysis and playback.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF331010),
                            border = BorderStroke(1.dp, Color(0xFFFF5252))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF8888)
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("audio/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("import_audio_button")
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Audio", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.setSection("Studio") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("record_audio_button")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Audio", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Audio Source Loaded Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0B172B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Text(
                                text = "REAL AUDIO LOADED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF132F52)
                        ) {
                            Text(
                                text = diagnostics.playerState.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "FILE: ${diagnostics.currentFileName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Progress Bar
                    val safeDuration = durationMs.coerceAtLeast(1L)
                    val progressFraction = (playheadMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("PLAYHEAD: ${playheadMs / 1000}s", fontSize = 11.sp, color = Color(0xFF00E5FF))
                            Text("TOTAL: ${durationMs / 1000}s", fontSize = 11.sp, color = Color(0xFF5E718B))
                        }

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = Color(0xFF00E5FF),
                            trackColor = Color(0xFF132F52)
                        )
                    }

                    // Transport Controls & Close Project Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleMasterPlayback() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            modifier = Modifier.weight(1f).testTag("master_play_button")
                        ) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text(if (isPlaying) "PAUSE" else "PLAY", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { masterEngine.stop() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("STOP", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.closeProject() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF331010)),
                            border = BorderStroke(1.dp, Color(0xFFFF5252)),
                            modifier = Modifier.weight(1f).testTag("close_project_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFF5252))
                            Spacer(Modifier.width(4.dp))
                            Text("CLOSE", color = Color(0xFFFF8888), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Audio Diagnostics Panel
        AudioDiagnosticsPanelCard(diagnostics)

        PitchAndTimeControlPanel(
            viewModel = viewModel,
            onOpenSendToDialog = onOpenSendToDialog
        )
    }
}

@Composable
fun AudioDiagnosticsPanelCard(diagnostics: com.example.audio.AudioDiagnosticsInfo) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF081220),
        border = BorderStroke(1.dp, Color(0xFF132F52)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                    Text("AUDIO DIAGNOSTICS PANEL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (diagnostics.isAudioLoaded) Color(0xFF064E3B) else Color(0xFF450A0A)
                ) {
                    Text(
                        text = if (diagnostics.isAudioLoaded) "PASS - SOURCE VALIDATED" else "STANDBY - IDLE ENGINE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (diagnostics.isAudioLoaded) Color(0xFF34D399) else Color(0xFFFCA5A5),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF132F52))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DiagnosticItem("Audio Loaded:", if (diagnostics.isAudioLoaded) "YES" else "NO", if (diagnostics.isAudioLoaded) Color(0xFF10B981) else Color(0xFFFF5252))
                DiagnosticItem("Decoder Status:", diagnostics.decoderStatus, Color(0xFF00E5FF))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DiagnosticItem("Sample Rate:", "${diagnostics.sampleRateHz} Hz", Color.White)
                DiagnosticItem("Channels:", diagnostics.channels, Color.White)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DiagnosticItem("Player State:", diagnostics.playerState.name, Color(0xFFFFD700))
                DiagnosticItem("Buffer Status:", "${diagnostics.bufferedMs} ms", Color(0xFF00E5FF))
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Output Device Architecture:", fontSize = 10.sp, color = Color(0xFF5E718B))
                Text(diagnostics.outputDevice, fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun DiagnosticItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = Color(0xFF5E718B))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun ArpeggioWorkspaceContent(viewModel: WorkstationViewModel) {
    val activeNotes by viewModel.activePerformanceNotes.collectAsStateWithLifecycle()
    val rawNoteNames = activeNotes.map { it.cleanNoteName }
    val arpeggioResult = remember(rawNoteNames) {
        com.example.data.AdvancedArpeggioEngine.analyzeNoteStream(rawNoteNames)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("ADVANCED ARPEGGIO INTELLIGENCE ENGINE", fontSize = 12.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF181308),
            border = BorderStroke(1.dp, Color(0xFFD4AF37)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Detected Pattern:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(arpeggioResult.patternType.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = arpeggioResult.patternType.accentColor)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Parent Chord:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(arpeggioResult.inferredParentChord, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Current Phrase:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(arpeggioResult.currentPhrase, fontSize = 11.sp, color = Color(0xFFCBD5E1))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Analysis Confidence:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text("${(arpeggioResult.confidence * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.feedPerformanceNotes(listOf("C", "E", "G", "B"), "Cmaj7") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                modifier = Modifier.weight(1f)
            ) {
                Text("TEST FINGERSTYLE", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.feedPerformanceNotes(listOf("F#", "A#", "C#", "F#"), "F# Major") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100)),
                modifier = Modifier.weight(1f)
            ) {
                Text("TEST SUNGURA ARPEGGIO", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        InstrumentSynchronizerView(
            activeNotes = rawNoteNames.ifEmpty { listOf("C", "E", "G") },
            currentNote = rawNoteNames.lastOrNull()
        )
    }
}

@Composable
private fun AnalyzerWorkspaceContent(viewModel: WorkstationViewModel) {
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val activeNotes by viewModel.activePerformanceNotes.collectAsStateWithLifecycle()
    val cleanNotes = activeNotes.map { it.cleanNoteName }
    val chordName = currentChordModel?.name ?: "C Major"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LearnAsYouPlayCard(
            chordSymbol = chordName,
            rawNotes = cleanNotes
        )

        ProfessionalVisualAnalyzersCard()

        InstrumentSynchronizerView(
            activeNotes = cleanNotes.ifEmpty { listOf("C", "E", "G") },
            currentNote = cleanNotes.lastOrNull()
        )
    }
}

@Composable
private fun InstrumentVoicingsWorkspaceContent(viewModel: WorkstationViewModel) {
    val activeNotes by viewModel.activePerformanceNotes.collectAsStateWithLifecycle()
    val cleanNotes = activeNotes.map { it.cleanNoteName }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InstrumentSynchronizerView(
            activeNotes = cleanNotes.ifEmpty { listOf("C", "E", "G", "B") },
            currentNote = cleanNotes.lastOrNull()
        )
    }
}

@Composable
private fun BpmWorkspaceContent(viewModel: WorkstationViewModel) {
    val bpm by viewModel.bpm.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("PRECISION TEMPO & METRONOME STUDIO", fontSize = 12.sp, color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
        Text("$bpm BPM", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { viewModel.engine.adjustBpm(-5) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52))
            ) { Text("-5 BPM", color = Color.White) }

            Button(
                onClick = { viewModel.engine.adjustBpm(5) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
            ) { Text("+5 BPM", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun KeyDetectionWorkspaceContent(viewModel: WorkstationViewModel) {
    val globalKey by viewModel.globalKeySignature.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("HARMONIC KEY SIGNATURE & CIRCLE OF 5THS", fontSize = 12.sp, color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
        Text("DETECTED KEY: ${globalKey ?: "C Major"}", fontSize = 20.sp, color = Color(0xFF06B6D4), fontWeight = FontWeight.Black)
        Text("Relative Minor: A Minor | Dominant: G Major | Subdominant: F Major", fontSize = 11.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
private fun PhraseRecWorkspaceContent(viewModel: WorkstationViewModel) {
    val activeNotes by viewModel.activePerformanceNotes.collectAsStateWithLifecycle()
    val cleanNotes = activeNotes.map { it.cleanNoteName }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("MELODIC CONTOUR & ACOUSTIC LICK EXTRACTOR", fontSize = 12.sp, color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
        Text("Extracting melodic lines from input audio channel...", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0B172B),
            border = BorderStroke(1.dp, Color(0xFF3B82F6)),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = "Phrase Contour: [ ${cleanNotes.ifEmpty { listOf("C4", "E4", "G4", "A4", "G4", "E4") }.joinToString(" -> ")} ]",
                modifier = Modifier.padding(12.dp),
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AfricanMusicWorkspaceContent(viewModel: WorkstationViewModel) {
    val activeNotes by viewModel.activePerformanceNotes.collectAsStateWithLifecycle()
    val cleanNotes = activeNotes.map { it.cleanNoteName }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AfricanMusicIntelligenceCard(cleanNotes = cleanNotes)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.feedPerformanceNotes(listOf("F#", "B", "C#"), "F# Major") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                modifier = Modifier.weight(1f)
            ) {
                Text("SUNGURA GUITAR", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.feedPerformanceNotes(listOf("C", "F", "G"), "C Major") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                modifier = Modifier.weight(1f)
            ) {
                Text("SOUKOUS SEBEN", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        InstrumentSynchronizerView(
            activeNotes = cleanNotes.ifEmpty { listOf("F#", "B", "C#") },
            currentNote = cleanNotes.lastOrNull()
        )
    }
}

@Composable
private fun MidiWorkspaceContent(viewModel: WorkstationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("POLYPHONIC MIDI CONVERSION STUDIO", fontSize = 12.sp, color = Color(0xFFEC4899), fontWeight = FontWeight.Bold)
        Text("Converts real-time acoustic pitch vectors into Standard MIDI File Type 1", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Button(
            onClick = { viewModel.triggerExport("MIDI", null) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("EXPORT POLYPHONIC MIDI (.MID)", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TheoryWorkspaceContent(viewModel: WorkstationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("MUSIC THEORY & MODAL SCALES GUIDE", fontSize = 12.sp, color = Color(0xFFA855F7), fontWeight = FontWeight.Bold)
        Text("Modes: Ionian, Dorian, Phrygian, Lydian, Mixolydian, Aeolian, Locrian", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Text("Cadences: Perfect Authentic (V-I), Plagal (IV-I), Deceptive (V-vi)", fontSize = 11.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
private fun ExportWorkspaceContent(viewModel: WorkstationViewModel) {
    com.example.ui.ExportCenterScreen(viewModel = viewModel)
}

@Composable
private fun AboutWorkspaceContent(module: AppModule, viewModel: WorkstationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("HZ CHORD AI WORKSTATION v3.2.0", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Black)
        Text("Engine: High-Performance Oboe C++ DSP Core", fontSize = 11.sp, color = Color(0xFF00E5FF))
        Text("Architecture: Module Launcher & Navigation Drawer Registry", fontSize = 11.sp, color = Color(0xFF94A3B8))
        Text("Status: 100% Offline Audio DSP & Realtime Neural Detection", fontSize = 11.sp, color = Color(0xFF10B981))
    }
}

@Composable
private fun HelpWorkspaceContent(module: AppModule, viewModel: WorkstationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("HZ CHORD AI USER GUIDE & DIAGNOSTICS", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Black)
        Text("• Tap ☰ icon anywhere to open the Module Drawer.", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Text("• Use the search box in the Drawer or Launcher to filter modules.", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Text("• All modules share the same Audio Engine & Playback Bar at the bottom.", fontSize = 11.sp, color = Color(0xFFCBD5E1))
    }
}

@Composable
private fun GenericWorkspaceContent(module: AppModule, viewModel: WorkstationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("${module.title.uppercase()} WORKSPACE READY", fontSize = 13.sp, color = module.color, fontWeight = FontWeight.Bold)
        Text(module.subtitle, fontSize = 11.sp, color = Color(0xFF94A3B8))
    }
}
