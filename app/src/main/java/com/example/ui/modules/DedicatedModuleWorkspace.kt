package com.example.ui.modules

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
import com.example.ui.PitchAndTimeControlPanel
import com.example.ui.UniversalSendToButton
import com.example.ui.navigation.AppModuleRegistry
import com.example.ui.navigation.AppModule
import com.example.ui.navigation.ModuleToolbar
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
                            "audio_player" -> AudioPlayerWorkspaceContent(viewModel, onOpenSendToDialog)
                            "dsp" -> PitchAndTimeControlPanel(viewModel, onOpenSendToDialog = onOpenSendToDialog)
                            "arpeggio" -> ArpeggioWorkspaceContent(viewModel)
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
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()
    val playheadMs by viewModel.playbackPositionMs.collectAsStateWithLifecycle()
    val uploadedFileName by viewModel.uploadedFileName.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("STAGED FILE: ${uploadedFileName ?: "Demo Project Session"}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text("PLAYHEAD: ${playheadMs / 1000}s / 214s", fontSize = 11.sp, color = Color(0xFF00E5FF))

            LinearProgressIndicator(
                progress = { (playheadMs % 214000L).toFloat() / 214000f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Color(0xFF00E5FF),
                trackColor = Color(0xFF132F52)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleMasterPlayback() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isPlaying) "PAUSE AUDIO" else "PLAY AUDIO", color = Color(0xFF030814), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.setPlaybackPosition(0L) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("RESTART", color = Color.White)
                }
            }
        }

        PitchAndTimeControlPanel(
            viewModel = viewModel,
            onOpenSendToDialog = onOpenSendToDialog
        )
    }
}

@Composable
private fun ArpeggioWorkspaceContent(viewModel: WorkstationViewModel) {
    var selectedPattern by remember { mutableStateOf("Ascending 16ths") }
    val patterns = listOf("Ascending 16ths", "Descending 8ths", "Alberti Bass", "African Soukous 3-2")

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("SMART ARPEGGIO PATTERN GENERATOR", fontSize = 12.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)

        patterns.forEach { pattern ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedPattern == pattern) Color(0xFF241C04) else Color(0xFF0B172B),
                border = BorderStroke(1.dp, if (selectedPattern == pattern) Color(0xFFD4AF37) else Color(0xFF132F52)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPattern = pattern }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(pattern, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    if (selectedPattern == pattern) {
                        Text("ACTIVE", fontSize = 10.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.engine.tapLiveMusicalNote("C E G B") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("GENERATE ARPEGGIO LICK", color = Color.Black, fontWeight = FontWeight.Bold)
        }
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
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("MELODIC CONTOUR & ACOUSTIC LICK EXTRACTOR", fontSize = 12.sp, color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
        Text("Extracting melodic lines from input audio channel...", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0B172B),
            border = BorderStroke(1.dp, Color(0xFF3B82F6)),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Phrase Contour: [ C4 -> E4 -> G4 -> A4 -> G4 -> E4 ]", modifier = Modifier.padding(12.dp), fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AfricanMusicWorkspaceContent(viewModel: WorkstationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("AFRICAN MUSIC INTELLIGENCE ENGINE", fontSize = 12.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
        Text("Rhythmic Modes: Afrobeat 3:2 Polyrhythm, Soukous Guitarmonies, Amapiano Log-Bass", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        Button(
            onClick = { viewModel.engine.tapLiveMusicalNote("A C E G") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("GENERATE SOUKOUS INTERCEPT LICK", color = Color.Black, fontWeight = FontWeight.Bold)
        }
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
