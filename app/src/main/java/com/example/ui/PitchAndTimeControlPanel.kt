package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MusicTheoryUtils
import com.example.viewmodel.WorkstationViewModel
import kotlin.math.roundToInt

@Composable
fun PitchAndTimeControlPanel(
    viewModel: WorkstationViewModel,
    modifier: Modifier = Modifier,
    onOpenSendToDialog: (sourceName: String) -> Unit = {}
) {
    val pitchShift by viewModel.pitchShiftSemitones.collectAsStateWithLifecycle()
    val speedMultiplier by viewModel.tempoPreservedMultiplier.collectAsStateWithLifecycle()
    val globalKey by viewModel.globalKeySignature.collectAsStateWithLifecycle()
    val transposedKey by viewModel.transposedKeySignature.collectAsStateWithLifecycle()
    val bpm by viewModel.bpm.collectAsStateWithLifecycle()
    val isLearningSpeedMode by viewModel.isLearningSpeedMode.collectAsStateWithLifecycle()
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()

    val originalKeyLabel = globalKey ?: "C Major"
    val intervalLabel = MusicTheoryUtils.getIntervalName(pitchShift)
    val effectiveBpm = (bpm * speedMultiplier).roundToInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pitch_and_time_control_panel"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -------------------------------------------------------------
        // 1. PITCH TRANSPOSE SYSTEM™
        // -------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091428)),
            border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pitch_transpose_system_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFA855F7).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFA855F7)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Pitch Transpose",
                                    tint = Color(0xFFA855F7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "PITCH TRANSPOSE SYSTEM™",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "-12 to +12 Semitones Real-Time DSP Pitch Shifting",
                                fontSize = 10.sp,
                                color = Color(0xFFC084FC)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFA855F7).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFFA855F7))
                    ) {
                        Text(
                            text = if (pitchShift == 0) "ORIGINAL PITCH" else "${if (pitchShift > 0) "+$pitchShift" else pitchShift} ST",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA855F7),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transpose Key Summary Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF040A18), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF132A4A), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Original Key
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("ORIGINAL KEY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(originalKeyLabel, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    // Arrow
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFA855F7),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(18.dp)
                    )

                    // Transposed Key
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CURRENT KEY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA855F7))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = transposedKey,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE9D5FF)
                        )
                    }

                    // Shift Interval
                    Column(horizontalAlignment = Alignment.End) {
                        Text("INTERVAL SHIFT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = intervalLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Master Transpose Control Row (-1, RESET, +1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.transposeDown() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                        border = BorderStroke(1.dp, Color(0xFFA855F7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("transpose_down_btn")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Transpose Down", tint = Color(0xFFE9D5FF))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DOWN (-1 ST)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE9D5FF))
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetTranspose() },
                        border = BorderStroke(1.dp, if (pitchShift == 0) Color(0xFF64748B) else Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("reset_transpose_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Transpose",
                            tint = if (pitchShift == 0) Color(0xFF94A3B8) else Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RESET (0)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pitchShift == 0) Color(0xFF94A3B8) else Color(0xFF10B981)
                        )
                    }

                    Button(
                        onClick = { viewModel.transposeUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                        border = BorderStroke(1.dp, Color(0xFFA855F7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("transpose_up_btn")
                    ) {
                        Text("UP (+1 ST)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE9D5FF))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Add, contentDescription = "Transpose Up", tint = Color(0xFFE9D5FF))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Semitone Quick Selector Row
                Text("QUICK SEMITONE PRESETS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(6.dp))

                val semitonePresets = listOf(-12, -7, -5, -2, -1, 0, 1, 2, 5, 7, 12)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    semitonePresets.forEach { preset ->
                        val isSelected = pitchShift == preset
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Color(0xFFA855F7) else Color(0xFF0F1B2E),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color.White else Color(0xFF1E293B)
                            ),
                            onClick = { viewModel.setPitchShift(preset) },
                            modifier = Modifier.testTag("semitone_preset_$preset")
                        ) {
                            Text(
                                text = if (preset > 0) "+$preset" else "$preset",
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fine Transpose Slider (-12 to +12)
                Slider(
                    value = pitchShift.toFloat(),
                    onValueChange = { viewModel.setPitchShift(it.roundToInt()) },
                    valueRange = -12f..12f,
                    steps = 23,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFA855F7),
                        activeTrackColor = Color(0xFFA855F7),
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("pitch_transpose_slider")
                )

                // Transposition Live Target Modules Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("✓ Synchronized Targets:", fontSize = 9.sp, color = Color(0xFF10B981))
                    Text("Chords • Numerals • Piano • Guitar • MIDI • Lyrics", fontSize = 9.sp, color = Color(0xFF94A3B8))
                }
            }
        }

        // -------------------------------------------------------------
        // 2. TIME STRETCH SYSTEM™
        // -------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF071B19)),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("time_stretch_system_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Time Stretch",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "TIME STRETCH SYSTEM™",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "25% to 200% Independent Speed Control (Pitch Preserved)",
                                fontSize = 10.sp,
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Text(
                            text = "${(speedMultiplier * 100).roundToInt()}% SPEED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Speed & BPM Summary Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF031410), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF0F3D32), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Original BPM
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("ORIGINAL BPM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$bpm BPM", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    // Effective Practice BPM
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EFFECTIVE PRACTICE BPM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$effectiveBpm BPM",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6EE7B7)
                        )
                    }

                    // Speed Multiplier
                    Column(horizontalAlignment = Alignment.End) {
                        Text("SPEED MULTIPLIER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%.2fx", speedMultiplier),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Speed Control Row (Slow Down, Normal 100%, Speed Up)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.slowDown() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("slow_down_speed_btn")
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Slow Down", tint = Color(0xFFA7F3D0))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SLOW DOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7F3D0))
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetSpeed() },
                        border = BorderStroke(1.dp, if (speedMultiplier == 1.0f) Color(0xFF64748B) else Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("reset_speed_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Normal Speed",
                            tint = if (speedMultiplier == 1.0f) Color(0xFF94A3B8) else Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "NORMAL (100%)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (speedMultiplier == 1.0f) Color(0xFF94A3B8) else Color(0xFF10B981)
                        )
                    }

                    Button(
                        onClick = { viewModel.speedUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("speed_up_btn")
                    ) {
                        Text("SPEED UP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7F3D0))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.FastForward, contentDescription = "Speed Up", tint = Color(0xFFA7F3D0))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Playback Speed Preset Row (25%, 50%, 75%, 100%, 125%, 150%, 200%)
                Text("EXACT PLAYBACK SPEED PRESETS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(6.dp))

                val speedPresets = listOf(0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f, 2.00f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    speedPresets.forEach { preset ->
                        val pct = (preset * 100).roundToInt()
                        val isSelected = (speedMultiplier - preset).let { kotlin.math.abs(it) < 0.02f }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Color(0xFF10B981) else Color(0xFF062B22),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color.White else Color(0xFF0F5241)
                            ),
                            onClick = { viewModel.setSpeedMultiplier(preset) },
                            modifier = Modifier.testTag("speed_preset_$pct")
                        ) {
                            Text(
                                text = "$pct%",
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF022C22) else Color(0xFFA7F3D0),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Speed Slider (0.25x to 2.00x)
                Slider(
                    value = speedMultiplier,
                    onValueChange = { viewModel.setSpeedMultiplier(it) },
                    valueRange = 0.25f..2.00f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF10B981),
                        activeTrackColor = Color(0xFF10B981),
                        inactiveTrackColor = Color(0xFF0F3D32)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("time_stretch_speed_slider")
                )
            }
        }

        // -------------------------------------------------------------
        // 3. PRACTICE MODE / LEARNING SPEED MODE
        // -------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1704)),
            border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("learning_speed_practice_mode_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD4AF37).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Practice Mode",
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "PRACTICE & LEARNING SPEED MODE™",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Slow Down Audio While Preserving Note & Grace Note Precision",
                                fontSize = 10.sp,
                                color = Color(0xFFFDE047)
                            )
                        }
                    }

                    Switch(
                        checked = isLearningSpeedMode,
                        onCheckedChange = { viewModel.toggleLearningSpeedMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFD4AF37),
                            checkedTrackColor = Color(0xFF544208),
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("learning_speed_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(visible = isLearningSpeedMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0B02), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF544208), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "✓ Sungura Guitar & Fast Solo Precision Detection Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "When slowed down (e.g. at 50% speed), every individual note (F# → A# → C# → F#), grace note, passing tone, and arpeggio run is tracked and highlighted with zero timing drift across piano, fretboard, and chord timeline.",
                            fontSize = 10.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.setSpeedMultiplier(0.50f) },
                                border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("practice_50_preset_btn")
                            ) {
                                Text("50% Slow-Mo", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
                            }

                            OutlinedButton(
                                onClick = { viewModel.setSpeedMultiplier(0.75f) },
                                border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("practice_75_preset_btn")
                            ) {
                                Text("75% Learning Pace", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
                            }

                            Button(
                                onClick = { viewModel.resetSpeed() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF544208)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("practice_100_preset_btn")
                            ) {
                                Text("100% Full Speed", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. STEM SYNCHRONIZATION TELEMETRY BANNER
        // -------------------------------------------------------------
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF070F1E),
            border = BorderStroke(1.dp, Color(0xFF1E3A8A)),
            modifier = Modifier.fillMaxWidth().testTag("stem_synchronization_telemetry_banner")
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Stems Sync",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "STEM PITCH & TEMPO SYNCHRONIZATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Transposition (${pitchShift} ST) & Speed (${(speedMultiplier * 100).roundToInt()}%) apply identically to Full Mix, Vocals, Guitar, Bass, Piano, and Drums.",
                            fontSize = 9.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }

                UniversalSendToButton(
                    sourceName = "Pitch Transposed & Time Stretched Audio Engine (${transposedKey}, ${(speedMultiplier * 100).roundToInt()}%)",
                    buttonText = "EXPORT AUDIO",
                    accentColor = Color(0xFFA855F7),
                    onOpenSendToDialog = onOpenSendToDialog
                )
            }
        }
    }
}
