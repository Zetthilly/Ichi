package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.UniversalSendToButton
import com.example.util.UniversalAudioMetadataExtractor
import com.example.viewmodel.WorkstationViewModel

@Composable
fun AudioSourceBannerCard(
    viewModel: WorkstationViewModel,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val context = LocalContext.current
    val masterEngine = viewModel.masterAudioEngine
    val diagnostics by masterEngine.diagnostics.collectAsStateWithLifecycle()
    val errorMessage by masterEngine.errorMessage.collectAsStateWithLifecycle()
    val isPlaying by masterEngine.isPlaying.collectAsStateWithLifecycle()
    val playheadMs by masterEngine.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by masterEngine.durationMs.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val metadata = UniversalAudioMetadataExtractor.extractMetadataFromUri(context, uri)
            viewModel.importUniversalAudio(metadata)
        }
    }

    if (!diagnostics.isAudioLoaded) {
        // Compact No Audio Loaded Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0B172B),
            border = BorderStroke(1.dp, Color(0xFF132F52)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("audio_source_standby_banner")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFF59E0B), CircleShape)
                        )
                        Text(
                            text = "AUDIO SOURCE: STANDBY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF132F52)
                    ) {
                        Text(
                            text = "AWAITING AUDIO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "No audio loaded. Import an audio file directly to run real-time harmonic analysis, stem separation, and playback across all workstation modules.",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )

                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF331010),
                        border = BorderStroke(1.dp, Color(0xFFFF5252))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(14.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 10.sp,
                                color = Color(0xFFFF8888)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("import_audio_source_button")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import Audio File", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("audio_source_active_banner")
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Text(
                            text = "REAL AUDIO LOADED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        UniversalSendToButton(
                            sourceName = diagnostics.currentFileName,
                            buttonText = "SEND TO",
                            accentColor = Color(0xFF00E5FF),
                            onOpenSendToDialog = onOpenSendToDialog
                        )

                        // Import different file button
                        IconButton(
                            onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                            modifier = Modifier.size(28.dp).testTag("change_audio_file_button")
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Change Audio File", tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Text(
                    text = "FILE: ${diagnostics.currentFileName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Progress Bar
                val safeDuration = durationMs.coerceAtLeast(1L)
                val progressFraction = (playheadMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("PLAYHEAD: ${playheadMs / 1000}s", fontSize = 10.sp, color = Color(0xFF00E5FF))
                        Text("TOTAL: ${durationMs / 1000}s", fontSize = 10.sp, color = Color(0xFF5E718B))
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF132F52)
                    )
                }

                // Transport Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleMasterPlayback() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f).height(30.dp).testTag("master_play_banner_button")
                    ) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (isPlaying) "PAUSE" else "PLAY", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { masterEngine.stop() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132F52)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f).height(30.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("STOP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.closeProject() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF331010)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f).height(30.dp).testTag("close_project_banner_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("CLOSE", color = Color(0xFFFF8888), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
