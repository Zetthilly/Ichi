package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DetectedPerformanceNote
import com.example.data.HarmonicNoteClassification
import com.example.data.HarmonicReconstructionEngine
import com.example.viewmodel.WorkstationViewModel

/**
 * Control Card for Harmonic Reconstruction Engine settings,
 * including the mandatory 'Show Harmonic Classification' toggle,
 * color legend, and interactive performance sequence verification triggers.
 */
@Composable
fun HarmonicClassificationToggleCard(
    viewModel: WorkstationViewModel,
    modifier: Modifier = Modifier
) {
    val showClassification by viewModel.showHarmonicClassification.collectAsState()
    val activeNotes by viewModel.activePerformanceNotes.collectAsState()
    val noteStream by viewModel.performanceNoteStream.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1424)),
        border = BorderStroke(1.dp, Color(0xFF1E2E44)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("harmonic_classification_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header & Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Harmonic Engine",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HARMONIC RECONSTRUCTION ENGINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Accurate Note Preservation • No Dropped Pitches",
                            fontSize = 10.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }

                // Show Harmonic Classification Toggle Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.toggleHarmonicClassification() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Text(
                        text = "Classification",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showClassification) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = showClassification,
                        onCheckedChange = { viewModel.toggleHarmonicClassification() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00E5FF),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("show_harmonic_classification_toggle")
                    )
                }
            }

            Divider(color = Color(0xFF1E293B))

            // Explanation Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F1A2E),
                border = BorderStroke(1.dp, Color(0xFF1E3250))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% Transcription Accuracy: Passing notes, grace notes, ghost notes, and chromatic approach notes are never hidden or omitted.",
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 14.sp
                    )
                }
            }

            // Legend Grid when Classification is ON
            if (showClassification) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "HARMONIC CLASSIFICATION COLOR KEY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HarmonicTagPill("Chord Tone", Color(0xFF00E5FF), Modifier.weight(1f))
                        HarmonicTagPill("Passing Tone", Color(0xFFFF9100), Modifier.weight(1f))
                        HarmonicTagPill("Grace Note", Color(0xFFFF4081), Modifier.weight(1f))
                        HarmonicTagPill("Chromatic", Color(0xFFAA00FF), Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HarmonicTagPill("Neighbor", Color(0xFFFFD600), Modifier.weight(1f))
                        HarmonicTagPill("Ornament", Color(0xFFE040FB), Modifier.weight(1f))
                        HarmonicTagPill("Pedal Tone", Color(0xFF00E676), Modifier.weight(1f))
                        HarmonicTagPill("Suspension", Color(0xFF00B8D4), Modifier.weight(1f))
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF111827),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "☑ Classification Disabled: Displaying all notes in standard uniform styling while preserving exact performance timing.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Interactive Verification Sequence Buttons
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TEST CHRONOLOGICAL NOTE PRESERVATION RUNS:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.8.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.triggerExampleSequence("F#_G_G#_A") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f).height(32.dp).testTag("test_seq_fsharp_a")
                    ) {
                        Text("F#➔G➔G#➔A", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.triggerExampleSequence("A_Bb_B_C") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9100)),
                        border = BorderStroke(1.dp, Color(0xFFFF9100).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f).height(32.dp).testTag("test_seq_a_c")
                    ) {
                        Text("A➔Bb➔B➔C", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.triggerExampleSequence("GRACE_NOTE_DEMO") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4081)),
                        border = BorderStroke(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f).height(32.dp).testTag("test_seq_grace")
                    ) {
                        Text("Grace Note", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearPerformanceNotes() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Clear", fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun HarmonicTagPill(label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Chronological Horizontal Note Stream displaying every detected note in performance order.
 */
@Composable
fun ChronologicalNoteStreamView(
    notes: List<DetectedPerformanceNote>,
    showClassification: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF08101E)),
        border = BorderStroke(1.dp, Color(0xFF1E2D4A)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CHRONOLOGICAL NOTE STREAM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${notes.size} Notes Captured",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Play instrument or click a test run to view note stream...",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notes, key = { it.id }) { note ->
                        val chipColor = if (showClassification) note.classification.color else Color(0xFF00E5FF)
                        
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = chipColor.copy(alpha = 0.18f),
                            border = BorderStroke(
                                width = if (note.isGraceNote) 1.5.dp else 1.dp,
                                color = if (note.isGhostNote) chipColor.copy(alpha = 0.4f) else chipColor
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (note.isGraceNote) {
                                        Text("🌸 ", fontSize = 9.sp)
                                    } else if (note.isGhostNote) {
                                        Text("👻 ", fontSize = 9.sp)
                                    }
                                    Text(
                                        text = note.fullPitchName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                if (showClassification) {
                                    Text(
                                        text = note.classification.label,
                                        fontSize = 7.sp,
                                        color = chipColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enhanced Guitar Fretboard Renderer supporting 100% note preservation and optional classification color-coding.
 */
@Composable
fun EnhancedGuitarFretboard(
    activePerformanceNotes: List<DetectedPerformanceNote>,
    showClassification: Boolean,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Map notes to (String, Fret) positions for standard guitar tuning EADGBE
    val fretPositions = remember(activePerformanceNotes) {
        val map = mutableListOf<Triple<Int, Int, DetectedPerformanceNote>>()
        activePerformanceNotes.forEach { note ->
            val pos = getGuitarStringAndFret(note.cleanNoteName)
            map.add(Triple(pos.first, pos.second, note))
        }
        map
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0C1322))
            .border(1.dp, Color(0xFF1D2E49), RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("enhanced_guitar_fretboard")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎸 Guitar Fretboard • Chronological Note Overlay",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4AF37)
            )
            if (activePerformanceNotes.isNotEmpty()) {
                Text(
                    text = activePerformanceNotes.joinToString(" ➔ ") { it.cleanNoteName },
                    fontSize = 10.sp,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fretboard Drawing Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val fretsCount = 7
                val stepX = size.width / fretsCount
                val stringsCount = 6
                val stepY = size.height / (stringsCount + 1)

                // 1. Draw Frets (vertical lines)
                for (f in 0..fretsCount) {
                    val x = f * stepX
                    drawLine(Color(0xFF475569), Offset(x, 0f), Offset(x, size.height), strokeWidth = 3f)
                }

                // Fret markers (dots at 3rd and 5th fret)
                drawCircle(Color(0xFF334155), radius = 4f, center = Offset(2.5f * stepX, size.height / 2))
                drawCircle(Color(0xFF334155), radius = 4f, center = Offset(4.5f * stepX, size.height / 2))

                // 2. Draw Strings (horizontal lines)
                for (s in 0 until stringsCount) {
                    val y = (s + 1) * stepY
                    drawLine(Color(0xFFCBD5E1), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.2f + (s * 0.4f))
                }

                // 3. Highlight performance notes
                fretPositions.forEach { (stringNum, fretNum, note) ->
                    val sIndex = stringsCount - stringNum // string layout E2(6) to E4(1)
                    val y = (sIndex + 1) * stepY
                    val x = (fretNum.coerceIn(1, fretsCount) - 0.5f) * stepX

                    val dotColor = if (showClassification) note.classification.color else Color(0xFFD4AF37)
                    val radius = if (note.isGraceNote) 10f else if (note.isGhostNote) 11f else 13f

                    // Outer halo for grace / chromatic notes
                    if (note.isGraceNote || note.classification == HarmonicNoteClassification.GRACE_NOTE) {
                        drawCircle(
                            color = dotColor.copy(alpha = 0.4f),
                            radius = radius + 6f,
                            center = Offset(x, y)
                        )
                    }

                    drawCircle(
                        color = if (note.isGhostNote) dotColor.copy(alpha = 0.6f) else dotColor,
                        radius = radius,
                        center = Offset(x, y)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Open string touch controls
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
                    modifier = Modifier.weight(1f).height(28.dp).padding(horizontal = 2.dp)
                ) {
                    Text(text = lbl, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Enhanced Piano Keyboard supporting classification colors and accurate note preservation.
 */
@Composable
fun EnhancedPianoKeyboard(
    activePerformanceNotes: List<DetectedPerformanceNote>,
    showClassification: Boolean,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2")
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0C101B))
            .border(1.dp, Color(0xFF1D2E49), RoundedCornerShape(10.dp))
            .padding(8.dp)
            .testTag("enhanced_piano_keyboard")
    ) {
        // Red velvet felt bumper
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
            // White Keys
            Row(modifier = Modifier.fillMaxSize()) {
                whiteKeys.forEach { keyNote ->
                    val baseNote = keyNote.removeSuffix("2")
                    val matchedNote = activePerformanceNotes.find { 
                        it.cleanNoteName == baseNote || it.cleanNoteName == keyNote 
                    }
                    val isHighlighted = matchedNote != null

                    val keyColor = if (isHighlighted) {
                        if (showClassification) matchedNote!!.classification.color else Color(0xFFFEF9D9)
                    } else Color(0xFFFCFCFC)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = keyColor,
                                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                            )
                            .border(0.8.dp, Color(0xFF0F1522), RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .clickable { onKeyClick(keyNote) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (isHighlighted) {
                            Text(
                                text = baseNote,
                                fontSize = 8.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            // Black Keys
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalWidth = maxWidth
                val whiteKeyWidth = totalWidth / whiteKeys.size

                for (pair in blackKeys) {
                    val keyNote = pair.first
                    val position = pair.second
                    val baseNote = keyNote.removeSuffix("2")
                    val matchedNote = activePerformanceNotes.find { 
                        it.cleanNoteName == baseNote || it.cleanNoteName == keyNote 
                    }
                    val isHighlighted = matchedNote != null

                    val keyColor = if (isHighlighted) {
                        if (showClassification) matchedNote!!.classification.color else Color(0xFFFEF08A)
                    } else Color(0xFF1E293B)

                    val leftOffset = whiteKeyWidth * position - (whiteKeyWidth * 0.32f)

                    Box(
                        modifier = Modifier
                            .offset(x = leftOffset)
                            .width(whiteKeyWidth * 0.64f)
                            .height(72.dp)
                            .background(
                                color = keyColor,
                                shape = RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)
                            )
                            .border(0.8.dp, Color(0xFF020617), RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                            .clickable { onKeyClick(keyNote) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (isHighlighted) {
                            Text(
                                text = baseNote,
                                fontSize = 7.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard guitar fret position mapping helper (EADGBE tuning)
 */
private fun getGuitarStringAndFret(note: String): Pair<Int, Int> {
    return when (note.takeWhile { it.isLetter() || it == '#' || it == 'b' }) {
        "F#" -> Pair(1, 2)
        "G" -> Pair(6, 3)
        "G#" -> Pair(6, 4)
        "A" -> Pair(5, 0)
        "A#", "Bb" -> Pair(5, 1)
        "B" -> Pair(5, 2)
        "C" -> Pair(5, 3)
        "C#", "Db" -> Pair(5, 4)
        "D" -> Pair(4, 0)
        "D#", "Eb" -> Pair(4, 1)
        "E" -> Pair(6, 0)
        "F" -> Pair(6, 1)
        else -> Pair(3, 2)
    }
}
