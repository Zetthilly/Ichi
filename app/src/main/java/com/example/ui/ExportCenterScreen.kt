package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.data.DetectedPerformanceNote
import com.example.util.AdvancedExportEngine
import com.example.viewmodel.WorkstationViewModel

/**
 * Advanced Export Center™ Screen for HZ CHORD AI.
 * Designed and Built by Joseph Hilary Zulukwa.
 *
 * Provides complete multi-format exporting for Stems, Notes, Chords, Arpeggios,
 * MIDI, MusicXML, PDF Reports, and Practice Guides.
 */
@Composable
fun ExportCenterScreen(
    viewModel: WorkstationViewModel,
    modifier: Modifier = Modifier
) {
    val activeNotes by viewModel.activePerformanceNotes.collectAsState()
    val noteStream by viewModel.performanceNoteStream.collectAsState()
    val liveChords by viewModel.chordTimeline.collectAsState()
    val currentKey by viewModel.globalKeySignature.collectAsState()
    val currentBpm by viewModel.bpm.collectAsState()

    var selectedFormat by remember { mutableStateOf(AdvancedExportEngine.ExportFormat.PDF_REPORT) }
    var selectedAudioFormat by remember { mutableStateOf(AdvancedExportEngine.ExportFormat.WAV) }
    var selectedStem by remember { mutableStateOf("Guitar") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030814))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER BRANDING BANNER
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF081226),
            border = BorderStroke(1.dp, Color(0xFF13284C)),
            modifier = Modifier.fillMaxWidth().testTag("export_center_header")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HZ CHORD AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E5FF),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "ADVANCED EXPORT SYSTEM™",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Designed and Built by Joseph Hilary Zulukwa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export Hub",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "100% Offline Multi-Format Exporter: WAV, FLAC, MP3, AAC, MIDI, MusicXML, PDF, CSV, JSON, Chord Sheets & Practice Reports.",
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 14.sp
                )
            }
        }

        // 1. STEM SEPARATION EXPORT CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091428)),
            border = BorderStroke(1.dp, Color(0xFF1B2E4E)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("stem_export_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STEM SEPARATION EXPORT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    text = "Preserves original timing, sample rate, length, and synchronization position across all stems.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )

                // Audio Format Selector (WAV, AAC)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val audioFormats = listOf(
                        AdvancedExportEngine.ExportFormat.WAV,
                        AdvancedExportEngine.ExportFormat.AAC
                    )
                    audioFormats.forEach { fmt ->
                        val isPicked = selectedAudioFormat == fmt
                        OutlinedButton(
                            onClick = { selectedAudioFormat = fmt },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isPicked) Color(0xFF10B981) else Color(0xFF0F1E36),
                                contentColor = if (isPicked) Color.Black else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isPicked) Color(0xFF10B981) else Color(0xFF1E3252)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(32.dp)
                        ) {
                            Text(fmt.name, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Individual Stem Selector
                Text("Select Stem:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                val stems = listOf("Vocals", "Drums", "Bass", "Guitar", "Piano", "Strings", "Other")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(stems) { stem ->
                        val isSel = selectedStem == stem
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF0F1B2E),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF00E5FF) else Color(0xFF1E2D48)),
                            modifier = Modifier.clickable { selectedStem = stem }
                        ) {
                            Text(
                                text = stem,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color(0xFF00E5FF) else Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerStemExport(selectedStem, selectedAudioFormat) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("export_individual_stem_btn")
                    ) {
                        Text("Export $selectedStem", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.triggerStemExport("ALL_STEMS", selectedAudioFormat) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F), contentColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("export_all_stems_btn")
                    ) {
                        Text("Export All Stems", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.triggerStemPackageExport() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("export_stem_package_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stem Package", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. NOTE & CHORD TRANSCRIPTION EXPORT CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091428)),
            border = BorderStroke(1.dp, Color(0xFF1B2E4E)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("transcription_export_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NOTE & CHORD TRANSCRIPTION EXPORT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    text = "Preserves 100% of detected notes: Grace notes, passing tones, chromatic notes, ornamentation, slides & hammer-ons.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )

                // Format Pills (PDF, TXT, CSV, JSON, MIDI, MusicXML, Chord Sheet, Practice Report)
                Text("Select Format:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                val formats = listOf(
                    AdvancedExportEngine.ExportFormat.PDF_REPORT,
                    AdvancedExportEngine.ExportFormat.MIDI,
                    AdvancedExportEngine.ExportFormat.MUSIC_XML,
                    AdvancedExportEngine.ExportFormat.CHORD_SHEET,
                    AdvancedExportEngine.ExportFormat.CSV,
                    AdvancedExportEngine.ExportFormat.JSON,
                    AdvancedExportEngine.ExportFormat.TXT,
                    AdvancedExportEngine.ExportFormat.PRACTICE_REPORT
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(formats) { fmt ->
                        val isSel = selectedFormat == fmt
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFFD4AF37).copy(alpha = 0.2f) else Color(0xFF0F1B2E),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFFD4AF37) else Color(0xFF1E2D48)),
                            modifier = Modifier.clickable { selectedFormat = fmt }
                        ) {
                            Text(
                                text = fmt.displayName,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color(0xFFD4AF37) else Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.triggerAdvancedExport(selectedFormat, null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("generate_export_btn")
                ) {
                    Text("Export ${selectedFormat.displayName}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // 3. MIDI GENERATION HUB
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091428)),
            border = BorderStroke(1.dp, Color(0xFF1B2E4E)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("midi_generation_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = Color(0xFFEC4899), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MIDI GENERATION ENGINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    text = "Converts note positions, durations, velocity, chord markers, tempo ($currentBpm BPM), and key signature ($currentKey) to MIDI.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.triggerAdvancedExport(AdvancedExportEngine.ExportFormat.MIDI, null) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEC4899)),
                        border = BorderStroke(1.dp, Color(0xFFEC4899)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("export_full_midi_btn")
                    ) { Text("Full MIDI", fontSize = 9.sp, fontWeight = FontWeight.Bold) }

                    OutlinedButton(
                        onClick = { viewModel.triggerAdvancedExport(AdvancedExportEngine.ExportFormat.MIDI, null) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("export_chord_midi_btn")
                    ) { Text("Chord MIDI", fontSize = 9.sp, fontWeight = FontWeight.Bold) }

                    OutlinedButton(
                        onClick = { viewModel.triggerAdvancedExport(AdvancedExportEngine.ExportFormat.MIDI, null) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9100)),
                        border = BorderStroke(1.dp, Color(0xFFFF9100)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("export_melody_midi_btn")
                    ) { Text("Melody MIDI", fontSize = 9.sp, fontWeight = FontWeight.Bold) }

                    OutlinedButton(
                        onClick = { viewModel.triggerAdvancedExport(AdvancedExportEngine.ExportFormat.MIDI, null) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("export_bass_midi_btn")
                    ) { Text("Bass MIDI", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }

        // 4. SMART "SEND TO EXPORT" MODULE SHORTCUTS
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091428)),
            border = BorderStroke(1.dp, Color(0xFF1B2E4E)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("smart_send_to_export_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SMART EXPORT / MODULE SHORTCUTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD4AF37)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmartShortcutButton(
                        label = "Stem ➔ Guitar",
                        onClick = { viewModel.triggerStemExport("Guitar", AdvancedExportEngine.ExportFormat.WAV) },
                        modifier = Modifier.weight(1f)
                    )
                    SmartShortcutButton(
                        label = "Chord ➔ Sheet",
                        onClick = { viewModel.triggerAdvancedExport(AdvancedExportEngine.ExportFormat.CHORD_SHEET, null) },
                        modifier = Modifier.weight(1f)
                    )
                    SmartShortcutButton(
                        label = "Arpeggio ➔ Phrase",
                        onClick = { viewModel.triggerAdvancedExport(AdvancedExportEngine.ExportFormat.JSON, null) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SmartShortcutButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0F1E38),
        border = BorderStroke(1.dp, Color(0xFF1E3A60)),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}
