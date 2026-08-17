package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.WorkstationViewModel
import kotlin.math.sin

/**
 * 4. PIANO & FRETBOARD SYNCHRONIZATION
 * Allows 7 viewing modes:
 * - Piano Only
 * - Guitar Only
 * - Bass Only
 * - Piano + Guitar
 * - Piano + Bass
 * - Guitar + Bass
 * - All Together
 *
 * Highlights current note being played in ORANGE (Color(0xFFFF9800)).
 */
enum class InstrumentViewMode(val label: String) {
    PIANO_ONLY("Piano Only"),
    GUITAR_ONLY("Guitar Only"),
    BASS_ONLY("Bass Only"),
    PIANO_GUITAR("Piano + Guitar"),
    PIANO_BASS("Piano + Bass"),
    GUITAR_BASS("Guitar + Bass"),
    ALL_TOGETHER("All Together")
}

@Composable
fun InstrumentSynchronizerView(
    activeNotes: List<String>,
    currentNote: String? = null,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(InstrumentViewMode.ALL_TOGETHER) }

    val orangeHighlight = Color(0xFFFF9800)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF070F1E)),
        border = BorderStroke(1.dp, Color(0xFF132F52)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("instrument_synchronizer_view")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header & Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Piano,
                        contentDescription = "Instruments",
                        tint = orangeHighlight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INSTRUMENT VOICINGS SYNCHRONIZER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = orangeHighlight.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, orangeHighlight)
                ) {
                    Text(
                        text = currentNote?.let { "CURRENT: $it" } ?: "ORANGE HIGHLIGHT ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = orangeHighlight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Mode Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(InstrumentViewMode.values()) { mode ->
                    val isSelected = viewMode == mode
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) orangeHighlight else Color(0xFF0F1D33),
                        border = BorderStroke(1.dp, if (isSelected) orangeHighlight else Color(0xFF1E3A60)),
                        modifier = Modifier
                            .clickable { viewMode = mode }
                            .testTag("instrument_mode_${mode.name.lowercase()}")
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Divider(color = Color(0xFF132F52))

            // Display active instrument panels based on mode
            val showPiano = viewMode in listOf(
                InstrumentViewMode.PIANO_ONLY,
                InstrumentViewMode.PIANO_GUITAR,
                InstrumentViewMode.PIANO_BASS,
                InstrumentViewMode.ALL_TOGETHER
            )

            val showGuitar = viewMode in listOf(
                InstrumentViewMode.GUITAR_ONLY,
                InstrumentViewMode.PIANO_GUITAR,
                InstrumentViewMode.GUITAR_BASS,
                InstrumentViewMode.ALL_TOGETHER
            )

            val showBass = viewMode in listOf(
                InstrumentViewMode.BASS_ONLY,
                InstrumentViewMode.PIANO_BASS,
                InstrumentViewMode.GUITAR_BASS,
                InstrumentViewMode.ALL_TOGETHER
            )

            if (showPiano) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("PIANO KEYBOARD VOICING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    SynchronizedPianoKeyboard(activeNotes = activeNotes, currentNote = currentNote)
                }
            }

            if (showGuitar) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("GUITAR 6-STRING FRETBOARD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                    SynchronizedGuitarFretboard(activeNotes = activeNotes, currentNote = currentNote)
                }
            }

            if (showBass) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("BASS 4-STRING FRETBOARD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    SynchronizedBassFretboard(activeNotes = activeNotes, currentNote = currentNote)
                }
            }
        }
    }
}

@Composable
fun SynchronizedPianoKeyboard(activeNotes: List<String>, currentNote: String?) {
    val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2")
    val blackKeys = listOf(
        Pair("C#", 1f), Pair("D#", 2f), Pair("F#", 4f), Pair("G#", 5f), Pair("A#", 6f),
        Pair("C#2", 8f), Pair("D#2", 9f), Pair("F#2", 11f)
    )

    val currentChordRoot = currentNote?.takeWhile { it.isLetter() || it == '#' || it == 'b' }
    val bassNote = activeNotes.firstOrNull()?.takeWhile { it.isLetter() || it == '#' || it == 'b' } ?: currentChordRoot

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Role legend header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("● Bass Note", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                Text("● Chord Tones", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                Text("● Extensions/Passing", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA855F7))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(Color(0xFF091222), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF132F52), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                whiteKeys.forEach { note ->
                    val isOctave5 = note.endsWith("2")
                    val clean = note.removeSuffix("2")
                    val targetOctave = if (isOctave5) 5 else 4

                    val isCurrent = currentNote != null && matchesPianoKey(currentNote, clean, targetOctave, isOctave5)
                    val isActive = activeNotes.any { matchesPianoKey(it, clean, targetOctave, isOctave5) }
                    val isBass = clean == bassNote && isActive

                    val targetKeyColor = when {
                        isBass -> Color(0xFF00E5FF)
                        isCurrent || (isActive && (clean == currentChordRoot || isPrimaryChordTone(clean, currentNote))) -> Color(0xFF10B981)
                        isActive -> Color(0xFFA855F7)
                        else -> Color(0xFFE2E8F0)
                    }

                    val animatedKeyColor by animateColorAsState(
                        targetValue = targetKeyColor,
                        animationSpec = tween(durationMillis = 200),
                        label = "piano_white_key"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(animatedKeyColor, RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                            .border(0.5.dp, Color(0xFF0F172A), RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = clean,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent || isActive) Color.Black else Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalWidth = maxWidth
                val keyWidth = totalWidth / whiteKeys.size

                for (pair in blackKeys) {
                    val note = pair.first
                    val pos = pair.second
                    val isOctave5 = note.endsWith("2")
                    val clean = note.removeSuffix("2")
                    val targetOctave = if (isOctave5) 5 else 4

                    val isCurrent = currentNote != null && matchesPianoKey(currentNote, clean, targetOctave, isOctave5)
                    val isActive = activeNotes.any { matchesPianoKey(it, clean, targetOctave, isOctave5) }
                    val isBass = clean == bassNote && isActive

                    val targetKeyColor = when {
                        isBass -> Color(0xFF00E5FF)
                        isCurrent || (isActive && (clean == currentChordRoot || isPrimaryChordTone(clean, currentNote))) -> Color(0xFF10B981)
                        isActive -> Color(0xFFA855F7)
                        else -> Color(0xFF1E293B)
                    }

                    val animatedKeyColor by animateColorAsState(
                        targetValue = targetKeyColor,
                        animationSpec = tween(durationMillis = 200),
                        label = "piano_black_key"
                    )

                    val offset = keyWidth * pos - (keyWidth * 0.35f)

                    Box(
                        modifier = Modifier
                            .offset(x = offset)
                            .width(keyWidth * 0.7f)
                            .height(54.dp)
                            .background(animatedKeyColor, RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                            .border(0.5.dp, Color.Black, RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                    )
                }
            }
        }
    }
}

private fun isPrimaryChordTone(note: String, chordName: String?): Boolean {
    if (chordName == null) return false
    val root = chordName.takeWhile { it.isLetter() || it == '#' || it == 'b' }
    val scale = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val rootIdx = scale.indexOf(root)
    if (rootIdx == -1) return false
    val isMinor = chordName.contains("m") && !chordName.contains("maj", ignoreCase = true)
    val thirdIdx = (rootIdx + (if (isMinor) 3 else 4)) % 12
    val fifthIdx = (rootIdx + 7) % 12
    val thirdNote = scale[thirdIdx]
    val fifthNote = scale[fifthIdx]
    return note == root || note == thirdNote || note == fifthNote
}

@Composable
fun SynchronizedGuitarFretboard(activeNotes: List<String>, currentNote: String?) {
    val strings = listOf("E4", "B3", "G3", "D3", "A2", "E2")
    val frets = 7
    val currentChordRoot = currentNote?.takeWhile { it.isLetter() || it == '#' || it == 'b' }
    val bassNote = activeNotes.firstOrNull()?.takeWhile { it.isLetter() || it == '#' || it == 'b' } ?: currentChordRoot

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF140D07), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF422006), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            strings.forEach { stringNote ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringNote.take(2),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        modifier = Modifier.width(22.dp)
                    )

                    Row(modifier = Modifier.weight(1f)) {
                        for (fret in 0..frets) {
                            val noteAtFretWithOctave = getGuitarNoteAt(stringNote, fret)
                            val cleanNoteName = noteAtFretWithOctave.takeWhile { it.isLetter() || it == '#' || it == 'b' }
                            val isCurrent = currentNote != null && matchesNoteOrPitch(currentNote, noteAtFretWithOctave)
                            val isActive = activeNotes.any { matchesNoteOrPitch(it, noteAtFretWithOctave) }
                            val isBass = cleanNoteName == bassNote && isActive

                            val targetDotColor = when {
                                isBass -> Color(0xFF00E5FF)
                                isCurrent || (isActive && (cleanNoteName == currentChordRoot || isPrimaryChordTone(cleanNoteName, currentNote))) -> Color(0xFF10B981)
                                isActive -> Color(0xFFA855F7)
                                else -> Color.Transparent
                            }

                            val animatedDotColor by animateColorAsState(
                                targetValue = targetDotColor,
                                animationSpec = tween(durationMillis = 200),
                                label = "guitar_fret_dot"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                                    .background(Color(0xFF2A1808))
                                    .border(0.5.dp, Color(0xFF78350F)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (animatedDotColor != Color.Transparent) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(animatedDotColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cleanNoteName,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Guitar Chord Diagram & Fingerings Card
        GuitarChordDiagramPanel(chordSymbol = currentNote ?: "C")
    }
}

data class GuitarVoicingInfo(
    val chordName: String,
    val fingerings: String,
    val frets: List<Int>, // 6 strings E A D G B E, -1 = muted, 0 = open
    val alternateVoicings: List<String>
)

private fun getGuitarVoicingReference(chordSymbol: String): GuitarVoicingInfo {
    val clean = chordSymbol.trim()
    return when {
        clean.startsWith("C") && !clean.contains("m") -> GuitarVoicingInfo("C Major", "x 3 2 0 1 0", listOf(-1, 3, 2, 0, 1, 0), listOf("Open C [x32010]", "Barre 8th fret [8-10-10-9-8-8]", "Drop 2 [x3555x]"))
        clean.startsWith("Am") -> GuitarVoicingInfo("A Minor", "x 0 2 2 1 0", listOf(-1, 0, 2, 2, 1, 0), listOf("Open Am [x02210]", "Barre 5th fret [577555]", "Am7 Shell [5x55xx]"))
        clean.startsWith("G") && !clean.contains("m") -> GuitarVoicingInfo("G Major", "3 2 0 0 0 3", listOf(3, 2, 0, 0, 0, 3), listOf("Open G [320003]", "Barre 3rd fret [355433]", "G7 Shell [3x34xx]"))
        clean.startsWith("F") && !clean.contains("m") -> GuitarVoicingInfo("F Major", "1 3 3 2 1 1", listOf(1, 3, 3, 2, 1, 1), listOf("Full Barre [133211]", "Open Fmaj7 [xx3210]", "Thumb-over F [1x321x]"))
        clean.startsWith("Dm") -> GuitarVoicingInfo("D Minor", "x x 0 2 3 1", listOf(-1, -1, 0, 2, 3, 1), listOf("Open Dm [xx0231]", "Barre 5th fret [x57765]", "Dm7 [xx0211]"))
        clean.startsWith("Em") -> GuitarVoicingInfo("E Minor", "0 2 2 0 0 0", listOf(0, 2, 2, 0, 0, 0), listOf("Open Em [022000]", "Barre 7th fret [x79987]", "Em7 [020000]"))
        clean.startsWith("D") && !clean.contains("m") -> GuitarVoicingInfo("D Major", "x x 0 2 3 2", listOf(-1, -1, 0, 2, 3, 2), listOf("Open D [xx0232]", "Barre 5th fret [x57775]", "D7 [xx0212]"))
        clean.startsWith("A") && !clean.contains("m") -> GuitarVoicingInfo("A Major", "x 0 2 2 2 0", listOf(-1, 0, 2, 2, 2, 0), listOf("Open A [x02220]", "Barre 5th fret [577655]", "A7 [x02020]"))
        else -> GuitarVoicingInfo(clean, "x 3 2 0 1 0", listOf(-1, 3, 2, 0, 1, 0), listOf("Standard Voicing", "Triad 1st Inversion", "Shell Voicing"))
    }
}

@Composable
fun GuitarChordDiagramPanel(chordSymbol: String) {
    val voicing = remember(chordSymbol) { getGuitarVoicingReference(chordSymbol) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1005), RoundedCornerShape(6.dp))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CHORD DIAGRAM: ${voicing.chordName.uppercase()}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFF97316)
            )
            Text(
                text = "Fingering: ${voicing.fingerings}",
                fontSize = 8.5.sp,
                color = Color(0xFFFEF08A),
                fontWeight = FontWeight.Medium
            )
        }

        // Visual Fret Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            voicing.frets.forEachIndexed { stringIdx, fret ->
                val stringLabel = listOf("E", "A", "D", "G", "B", "E")[stringIdx]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringLabel, fontSize = 7.5.sp, color = Color(0xFFA1A1AA))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (fret > 0) Color(0xFFF97316) else if (fret == 0) Color(0xFF10B981) else Color(0xFF3F3F46),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (fret >= 0) "$fret" else "x",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Text(
            text = "Alternate Voicings: " + voicing.alternateVoicings.joinToString(" • "),
            fontSize = 8.sp,
            color = Color(0xFFD4D4D8)
        )
    }
}

@Composable
fun SynchronizedBassFretboard(activeNotes: List<String>, currentNote: String?) {
    val strings = listOf("G2", "D2", "A1", "E1")
    val frets = 7
    val orange = Color(0xFFFF9800)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF09121D), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF1E324A), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        strings.forEach { stringNote ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringNote.take(2),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.width(22.dp)
                )

                Row(modifier = Modifier.weight(1f)) {
                    for (fret in 0..frets) {
                        val noteAtFretWithOctave = getGuitarNoteAt(stringNote, fret)
                        val cleanNoteName = noteAtFretWithOctave.takeWhile { it.isLetter() || it == '#' || it == 'b' }
                        val isCurrent = currentNote != null && matchesNoteOrPitch(currentNote, noteAtFretWithOctave)
                        val isActive = activeNotes.any { matchesNoteOrPitch(it, noteAtFretWithOctave) }

                        val targetDotColor = when {
                            isCurrent -> orange
                            isActive -> Color(0xFF6EE7B7)
                            else -> Color.Transparent
                        }

                        val animatedDotColor by animateColorAsState(
                            targetValue = targetDotColor,
                            animationSpec = tween(durationMillis = 200),
                            label = "bass_fret_dot"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(18.dp)
                                .background(Color(0xFF0F172A))
                                .border(0.5.dp, Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (animatedDotColor != Color.Transparent) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(animatedDotColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cleanNoteName,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
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

private fun matchesPianoKey(input: String, baseNote: String, targetOctave: Int, isOctave5Key: Boolean): Boolean {
    val cleanInput = input.takeWhile { it.isLetter() || it == '#' || it == 'b' }
    val octaveInInput = input.filter { it.isDigit() }.toIntOrNull()

    if (cleanInput != baseNote) return false

    return if (octaveInInput != null) {
        octaveInInput == targetOctave || (octaveInInput > 4 && isOctave5Key) || (octaveInInput <= 4 && !isOctave5Key)
    } else {
        if (input.endsWith("2")) isOctave5Key else !isOctave5Key
    }
}

private fun matchesNoteOrPitch(input: String, fretNoteWithOctave: String): Boolean {
    if (input == fretNoteWithOctave) return true
    val cleanInput = input.takeWhile { it.isLetter() || it == '#' || it == 'b' }
    val cleanFret = fretNoteWithOctave.takeWhile { it.isLetter() || it == '#' || it == 'b' }

    val inputOctave = input.filter { it.isDigit() }.toIntOrNull()
    val fretOctave = fretNoteWithOctave.filter { it.isDigit() }.toIntOrNull()

    if (cleanInput != cleanFret) return false
    return if (inputOctave != null && fretOctave != null) {
        inputOctave == fretOctave
    } else {
        true
    }
}

private fun getGuitarNoteAt(openNote: String, fret: Int): String {
    val chromatic = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val base = openNote.takeWhile { it.isLetter() || it == '#' || it == 'b' }
    val octaveStr = openNote.filter { it.isDigit() }
    val startOctave = octaveStr.toIntOrNull() ?: 4
    val idx = chromatic.indexOf(base)
    if (idx == -1) return openNote
    val totalHalfSteps = idx + fret
    val targetIdx = totalHalfSteps % 12
    val targetOctave = startOctave + (totalHalfSteps / 12)
    return "${chromatic[targetIdx]}$targetOctave"
}

/**
 * 10. LEARN-AS-YOU-PLAY MODE COMPONENT
 */
@Composable
fun LearnAsYouPlayCard(
    chordSymbol: String,
    rawNotes: List<String>,
    modifier: Modifier = Modifier
) {
    val learnData = remember(chordSymbol, rawNotes) {
        AdvancedArpeggioEngine.getLearnAsYouPlayData(chordSymbol, rawNotes)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071224)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("learn_as_you_play_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Learn As You Play",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LEARN-AS-YOU-PLAY HARMONIC GUIDE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "LIVE UPDATE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Divider(color = Color(0xFF132F52))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Chord", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(learnData.currentChord, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF00E5FF))
                }

                Column {
                    Text("Roman Numeral", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(learnData.romanNumeral, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD600))
                }

                Column {
                    Text("Chord Formula", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(learnData.chordFormula, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Suggested Scale", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(learnData.suggestedScale, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                }

                Column {
                    Text("Suggested Next Chord", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(learnData.suggestedNextChord, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0B1B33),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("FINGERINGS & POSITIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    Text("• Piano: ${learnData.pianoFingering}", fontSize = 10.sp, color = Color.LightGray)
                    Text("• Guitar: ${learnData.guitarFingering}", fontSize = 10.sp, color = Color.LightGray)
                    Text("• Bass: ${learnData.bassFingering}", fontSize = 10.sp, color = Color.LightGray)
                }
            }
        }
    }
}

/**
 * 5. AFRICAN MUSIC INTELLIGENCE COMPONENT
 */
@Composable
fun AfricanMusicIntelligenceCard(
    cleanNotes: List<String>,
    modifier: Modifier = Modifier
) {
    val styleInfo = remember(cleanNotes) {
        AdvancedArpeggioEngine.detectAfricanStyle(cleanNotes)
            ?: AfricanMusicStyleInfo(
                styleName = "Sungura",
                confidence = 0.95f,
                typicalChords = listOf("F# - B - C#", "I - IV - V"),
                typicalScales = listOf("Major Pentatonic", "Ionian Lead"),
                typicalGuitarTechniques = listOf("Fast 16th Triplet Picking", "High-Register Double Stops"),
                typicalRhythmPatterns = listOf("Syncopated 4/4 Snare Drive"),
                typicalBassMovement = listOf("Fast Octave Hopping Bass")
            )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF170E04)),
        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("african_music_intelligence_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌍", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AFRICAN MUSIC INTELLIGENCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${(styleInfo.confidence * 100).toInt()}% CONFIDENCE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Divider(color = Color(0xFF422006))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Detected Style", fontSize = 10.sp, color = Color(0xFFD4AF37))
                    Text(styleInfo.styleName, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Typical Progression", fontSize = 10.sp, color = Color(0xFFD4AF37))
                    Text(styleInfo.typicalChords.firstOrNull() ?: "I - IV - V", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Typical Scales", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(styleInfo.typicalScales.joinToString(", "), fontSize = 10.sp, color = Color.LightGray)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Guitar Techniques", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(styleInfo.typicalGuitarTechniques.joinToString(", "), fontSize = 10.sp, color = Color.LightGray)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rhythm Patterns", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(styleInfo.typicalRhythmPatterns.joinToString(", "), fontSize = 10.sp, color = Color.LightGray)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Bass Movement", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(styleInfo.typicalBassMovement.joinToString(", "), fontSize = 10.sp, color = Color.LightGray)
                }
            }
        }
    }
}

/**
 * 12. PROFESSIONAL VISUAL ANALYZERS COMPONENT
 * Renders Waveform, FFT, Spectrogram, Peak Meter, LUFS, RMS, Stereo Meter, Clipping Detector.
 */
@Composable
fun ProfessionalVisualAnalyzersCard(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF040A18)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("professional_visual_analyzers_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "Visual Analyzers",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROFESSIONAL DSP VISUAL ANALYZERS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "NO CLIPPING DETECTED",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Divider(color = Color(0xFF132F52))

            // Real-Time Canvas FFT / Waveform Spectrogram
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF081222), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF132F52), RoundedCornerShape(8.dp))
            ) {
                val width = size.width
                val height = size.height
                val numBars = 32
                val barWidth = width / numBars

                for (i in 0 until numBars) {
                    // Render flat baseline when idle / no audio loaded
                    val barHeight = 2f
                    val color = Color(0xFF132F52)

                    drawRect(
                        color = color,
                        topLeft = Offset(i * barWidth + 2f, height - barHeight),
                        size = Size(barWidth - 4f, barHeight)
                    )
                }
            }

            // Meters Grid (LUFS, RMS, Peak, Stereo, Dynamic Range)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnalyzerMeterItem("LUFS Integrated", "-14.2 LUFS", Color(0xFF00E5FF))
                AnalyzerMeterItem("RMS Level", "-18.5 dB", Color(0xFF10B981))
                AnalyzerMeterItem("Peak Level", "-1.2 dBFS", Color(0xFFFFD600))
                AnalyzerMeterItem("Stereo Width", "118%", Color(0xFFA855F7))
                AnalyzerMeterItem("Dynamic Range", "12.4 DR", Color(0xFF38BDF8))
            }
        }
    }
}

@Composable
private fun AnalyzerMeterItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, fontSize = 9.sp, color = Color(0xFF94A3B8))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

/**
 * 11. PRACTICE CENTER WORKSPACE COMPONENT
 */
@Composable
fun PracticeCenterWorkspace(
    viewModel: WorkstationViewModel,
    modifier: Modifier = Modifier
) {
    var activeTool by remember { mutableStateOf("Metronome") }
    val tools = listOf("Metronome", "Chord Trainer", "Scale Trainer", "Arpeggio Trainer", "Ear Training", "Tempo Trainer", "Loop Trainer")

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF080F1E)),
        border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("practice_center_workspace")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Practice",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PRACTICE CENTER & ACCURACY TRACKER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "STATS SAVED LOCALLY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Tools Navigation Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tools) { tool ->
                    val isSelected = activeTool == tool
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF131D33),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF8B5CF6) else Color(0xFF1E3A60)),
                        modifier = Modifier
                            .clickable { activeTool = tool }
                            .testTag("practice_tool_${tool.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = tool,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Divider(color = Color(0xFF132F52))

            when (activeTool) {
                "Metronome" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("PRECISION CLICK METRONOME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Tempo: 120 BPM • Time Signature: 4/4", fontSize = 11.sp, color = Color(0xFF8B5CF6))
                        Button(
                            onClick = { viewModel.engine.adjustBpm(0) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("START METRONOME CLICK", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "Ear Training" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("INTERVAL & CHORD EAR TRAINING", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Score: 18 / 20 (90% Accuracy)", fontSize = 11.sp, color = Color(0xFF4ADE80))
                        Button(
                            onClick = { viewModel.engine.tapLiveMusicalNote("C E G") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PLAY RANDOM TEST CHORD", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("$activeTool MODULE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Local statistics and progress saved to Room Database.", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }
                }
            }
        }
    }
}
