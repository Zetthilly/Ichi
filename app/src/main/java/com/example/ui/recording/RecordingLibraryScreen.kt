package com.example.ui.recording

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RecordingAssetEntity
import com.example.ui.navigation.AppModuleRegistry
import com.example.viewmodel.WorkstationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingLibraryScreen(
    viewModel: WorkstationViewModel,
    modifier: Modifier = Modifier
) {
    val recordings by viewModel.allRecordings.collectAsStateWithLifecycle()
    val isRecordingState by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingTimerSeconds by viewModel.recordingTimerSeconds.collectAsStateWithLifecycle()
    val searchQuery by viewModel.recordingSearchQuery.collectAsStateWithLifecycle()
    val sortOrder by viewModel.recordingSortOrder.collectAsStateWithLifecycle()
    val favoriteOnlyFilter by viewModel.recordingFavoriteFilter.collectAsStateWithLifecycle()

    var activeRecordingName by remember { mutableStateOf("New Session Recording") }
    var selectedFormat by remember { mutableStateOf("WAV") }
    var activeNotes by remember { mutableStateOf("") }

    var selectedRecordingForModule by remember { mutableStateOf<RecordingAssetEntity?>(null) }
    var selectedRecordingForEdit by remember { mutableStateOf<RecordingAssetEntity?>(null) }
    var selectedRecordingForRename by remember { mutableStateOf<RecordingAssetEntity?>(null) }
    var selectedRecordingForNotes by remember { mutableStateOf<RecordingAssetEntity?>(null) }

    val filteredRecordings = remember(recordings, searchQuery, sortOrder, favoriteOnlyFilter) {
        recordings.filter { rec ->
            val matchesQuery = searchQuery.isBlank() ||
                    rec.recordingName.contains(searchQuery, ignoreCase = true) ||
                    (rec.userNotes?.contains(searchQuery, ignoreCase = true) == true)
            val matchesFav = !favoriteOnlyFilter || rec.isFavorite
            matchesQuery && matchesFav
        }.sortedWith { a, b ->
            when (sortOrder) {
                "DATE_ASC" -> a.dateCreated.compareTo(b.dateCreated)
                "NAME_ASC" -> a.recordingName.lowercase().compareTo(b.recordingName.lowercase())
                "DURATION_DESC" -> b.durationMs.compareTo(a.durationMs)
                else -> b.dateCreated.compareTo(a.dateCreated) // DATE_DESC
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030814))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .testTag("recording_library_container"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Mic Recorder Studio Card
            item {
                LiveMicRecorderCard(
                    isRecording = isRecordingState,
                    timerSeconds = recordingTimerSeconds,
                    recordingName = activeRecordingName,
                    selectedFormat = selectedFormat,
                    userNotes = activeNotes,
                    onNameChange = { activeRecordingName = it },
                    onFormatChange = { selectedFormat = it },
                    onNotesChange = { activeNotes = it },
                    onToggleRecord = {
                        if (isRecordingState) {
                            viewModel.toggleRecording(
                                name = activeRecordingName,
                                format = selectedFormat,
                                notes = activeNotes
                            )
                        } else {
                            viewModel.toggleRecording()
                        }
                    }
                )
            }

            // Search, Filter & Sort Controls
            item {
                RecordingLibraryControls(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setRecordingSearchQuery(it) },
                    sortOrder = sortOrder,
                    onSortOrderChange = { viewModel.setRecordingSortOrder(it) },
                    favoriteOnlyFilter = favoriteOnlyFilter,
                    onToggleFavoriteFilter = { viewModel.toggleRecordingFavoriteFilter() },
                    totalCount = filteredRecordings.size
                )
            }

            // Recording Assets List
            if (filteredRecordings.isEmpty()) {
                item {
                    EmptyRecordingsCard()
                }
            } else {
                items(filteredRecordings, key = { it.id }) { recording ->
                    RecordingAssetItemCard(
                        recording = recording,
                        onToggleFavorite = { viewModel.toggleRecordingFavorite(recording.id) },
                        onSendToModule = { selectedRecordingForModule = recording },
                        onEditRecording = { selectedRecordingForEdit = recording },
                        onRename = { selectedRecordingForRename = recording },
                        onEditNotes = { selectedRecordingForNotes = recording },
                        onDuplicate = { viewModel.duplicateRecordingAsset(recording) },
                        onDelete = { viewModel.deleteRecordingAsset(recording.id) }
                    )
                }
            }
        }

        // Send to Module BottomSheet Dialog
        selectedRecordingForModule?.let { rec ->
            SendToModuleBottomSheet(
                recording = rec,
                onDismiss = { selectedRecordingForModule = null },
                onSelectModule = { moduleId ->
                    viewModel.sendRecordingToModule(rec, moduleId)
                    selectedRecordingForModule = null
                }
            )
        }

        // Recording Editing Modal
        selectedRecordingForEdit?.let { rec ->
            RecordingEditingDialog(
                recording = rec,
                onDismiss = { selectedRecordingForEdit = null },
                onApplyEdit = { editType, p1, p2 ->
                    viewModel.applyEditToRecording(rec, editType, p1, p2)
                    selectedRecordingForEdit = null
                }
            )
        }

        // Rename Recording Modal
        selectedRecordingForRename?.let { rec ->
            RenameRecordingDialog(
                initialName = rec.recordingName,
                onDismiss = { selectedRecordingForRename = null },
                onConfirm = { newName ->
                    viewModel.renameRecordingAsset(rec.id, newName)
                    selectedRecordingForRename = null
                }
            )
        }

        // Edit Notes Modal
        selectedRecordingForNotes?.let { rec ->
            EditNotesDialog(
                initialNotes = rec.userNotes ?: "",
                onDismiss = { selectedRecordingForNotes = null },
                onConfirm = { notes ->
                    viewModel.updateRecordingNotes(rec.id, notes)
                    selectedRecordingForNotes = null
                }
            )
        }
    }
}

@Composable
fun LiveMicRecorderCard(
    isRecording: Boolean,
    timerSeconds: Int,
    recordingName: String,
    selectedFormat: String,
    userNotes: String,
    onNameChange: (String) -> Unit,
    onFormatChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onToggleRecord: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0B172B),
        border = BorderStroke(1.dp, if (isRecording) Color(0xFFEF4444) else Color(0xFF00E5FF).copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_mic_recorder_card")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isRecording) Color(0x33EF4444) else Color(0x2200E5FF),
                            Color(0xFF071122)
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color(0xFFEF4444) else Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRecording) "LIVE BACKGROUND RECORDING" else "PROFESSIONAL MIC RECORDER™",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording) Color(0xFFEF4444) else Color(0xFF00E5FF),
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(0.5.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = "BACKGROUND SYNC ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Recording Name & Format Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = recordingName,
                    onValueChange = onNameChange,
                    label = { Text("Recording Name", fontSize = 11.sp, color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Format Pills
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("FORMAT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("WAV", "FLAC", "AAC", "MP3").forEach { fmt ->
                            val isSel = selectedFormat.equals(fmt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF00E5FF) else Color(0xFF1E293B),
                                border = BorderStroke(0.5.dp, if (isSel) Color(0xFF00E5FF) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable { onFormatChange(fmt) }
                                    .testTag("format_pill_$fmt")
                            ) {
                                Text(
                                    text = fmt,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Waveform & Timer Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF030712))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Waveform Bouncing Bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(32.dp)
                ) {
                    repeat(24) { i ->
                        val barHeight = if (isRecording) {
                            (Math.sin((i * 0.5) + (timerSeconds * 2)) * 14 + 16).dp
                        } else {
                            (Math.sin(i * 0.4) * 8 + 12).dp
                        }
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isRecording) Color(0xFFEF4444) else Color(0xFF00E5FF).copy(alpha = 0.6f)
                                )
                        )
                    }
                }

                // Timer Display
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatSecondsToTime(timerSeconds),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isRecording) Color(0xFFEF4444) else Color.White
                    )
                    Text(
                        text = if (isRecording) "RECORDING IN PROGRESS..." else "READY TO RECORD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Big Record Toggle Button
            Button(
                onClick = onToggleRecord,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFFEF4444) else Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("big_record_button")
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Record Toggle",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRecording) "STOP & AUTO-SAVE RECORDING ASSET" else "START PROFESSIONAL RECORDING",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecordingLibraryControls(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOrder: String,
    onSortOrderChange: (String) -> Unit,
    favoriteOnlyFilter: Boolean,
    onToggleFavoriteFilter: () -> Unit,
    totalCount: Int
) {
    var sortExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECORDING ASSETS LIBRARY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(0.5.dp, Color(0xFF1E293B))
            ) {
                Text(
                    text = "$totalCount Saved Assets",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search recordings by name or notes...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00E5FF)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("recording_search_input")
            )

            // Favorite Filter Toggle
            IconButton(
                onClick = onToggleFavoriteFilter,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (favoriteOnlyFilter) Color(0xFFEAB308).copy(alpha = 0.2f) else Color(0xFF0B172B))
                    .border(1.dp, if (favoriteOnlyFilter) Color(0xFFEAB308) else Color(0xFF1E293B), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = if (favoriteOnlyFilter) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite Filter",
                    tint = if (favoriteOnlyFilter) Color(0xFFEAB308) else Color(0xFF64748B)
                )
            }

            // Sort Dropdown Button
            Box {
                IconButton(
                    onClick = { sortExpanded = true },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0B172B))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort", tint = Color(0xFF00E5FF))
                }

                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false },
                    modifier = Modifier.background(Color(0xFF0B172B))
                ) {
                    DropdownMenuItem(
                        text = { Text("Newest First", color = Color.White, fontSize = 12.sp) },
                        onClick = { onSortOrderChange("DATE_DESC"); sortExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest First", color = Color.White, fontSize = 12.sp) },
                        onClick = { onSortOrderChange("DATE_ASC"); sortExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Name", color = Color.White, fontSize = 12.sp) },
                        onClick = { onSortOrderChange("NAME_ASC"); sortExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Duration", color = Color.White, fontSize = 12.sp) },
                        onClick = { onSortOrderChange("DURATION_DESC"); sortExpanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun RecordingAssetItemCard(
    recording: RecordingAssetEntity,
    onToggleFavorite: () -> Unit,
    onSendToModule: () -> Unit,
    onEditRecording: () -> Unit,
    onRename: () -> Unit,
    onEditNotes: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var isPlayingPreview by remember { mutableStateOf(false) }

    val formattedDate = remember(recording.dateCreated) {
        SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(recording.dateCreated))
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF091428),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recording_item_card_${recording.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Title & Format Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Play / Pause Icon
                    IconButton(
                        onClick = { isPlayingPreview = !isPlayingPreview },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isPlayingPreview) Color(0xFF10B981) else Color(0xFF00E5FF).copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (isPlayingPreview) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play Preview",
                            tint = if (isPlayingPreview) Color.Black else Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recording.recordingName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$formattedDate • ${recording.getFormattedSize()}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Format Tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = recording.fileFormat,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Favorite Star
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (recording.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (recording.isFavorite) Color(0xFFEAB308) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // More Actions Dropdown
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(Color(0xFF0B172B))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open as Project / Send to Module", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF00E5FF)) },
                                onClick = { onSendToModule(); menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Recording (Trim / Fade / Denoise)", color = Color.White, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White) },
                                onClick = { onEditRecording(); menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Rename", color = Color.White, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, tint = Color.White) },
                                onClick = { onRename(); menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit User Notes", color = Color.White, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = Color.White) },
                                onClick = { onEditNotes(); menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate Asset", color = Color.White, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White) },
                                onClick = { onDuplicate(); menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Asset", color = Color(0xFFEF4444), fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                                onClick = { onDelete(); menuExpanded = false }
                            )
                        }
                    }
                }
            }

            // Audio Metadata Chips (Sample rate, bit depth, duration, key, bpm)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetadataBadge("DUR: ${recording.getFormattedDuration()}")
                MetadataBadge("${recording.sampleRate / 1000}kHz")
                MetadataBadge("${recording.bitDepth}-bit")
                MetadataBadge(if (recording.channels == 2) "Stereo" else "Mono")
                MetadataBadge("BPM: ${recording.detectedBpm}")
                MetadataBadge("KEY: ${recording.detectedKey}")
            }

            // Waveform Preview Bar
            val peaks = remember(recording.waveformAmplitudesCsv) { recording.getWaveformPeaksList() }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF030712))
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                val step = size.width / peaks.size
                peaks.forEachIndexed { i, peak ->
                    val x = i * step + (step / 2)
                    val h = peak * size.height
                    val top = (size.height - h) / 2
                    drawLine(
                        color = if (isPlayingPreview) Color(0xFF10B981) else Color(0xFF00E5FF).copy(alpha = 0.7f),
                        start = Offset(x, top),
                        end = Offset(x, top + h),
                        strokeWidth = step * 0.6f
                    )
                }
            }

            // User Notes (if present)
            if (!recording.userNotes.isNullOrBlank()) {
                Text(
                    text = "Notes: ${recording.userNotes}",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Row: Send To Module Button
            Button(
                onClick = onSendToModule,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Transform, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("REUSE ASSET: SEND TO ANY MODULE / OPEN PROJECT", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun MetadataBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(0.5.dp, Color(0xFF1E293B))
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyRecordingsCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF071122),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(48.dp))
            Text("No Recording Assets Found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                "Use the Professional Mic Recorder above to capture high-fidelity audio. Every recording is automatically stored as a reusable asset for all 12 modules.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendToModuleBottomSheet(
    recording: RecordingAssetEntity,
    onDismiss: () -> Unit,
    onSelectModule: (String) -> Unit
) {
    val modules = remember {
        AppModuleRegistry.ALL_MODULES.filter {
            it.id in listOf(
                "audio_player", "analyzer", "arpeggio", "phrase_rec",
                "bpm", "key_detection", "stems", "dsp",
                "piano", "guitar", "midi", "export"
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF071122),
        scrimColor = Color.Black.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SEND TO MODULE / OPEN AS PROJECT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    Text("Asset: ${recording.recordingName}", fontSize = 12.sp, color = Color.White)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Text("Select target module to seamlessly open audio asset and restore analysis state:", fontSize = 11.sp, color = Color(0xFF94A3B8))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 420.dp)
            ) {
                items(modules) { module ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0B172B),
                        border = BorderStroke(1.dp, module.color.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectModule(module.id) }
                            .testTag("send_to_module_${module.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = module.color.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(module.emoji, fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(module.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(module.subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8), maxLines = 1)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = module.color)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordingEditingDialog(
    recording: RecordingAssetEntity,
    onDismiss: () -> Unit,
    onApplyEdit: (type: String, p1: Long, p2: Long) -> Unit
) {
    var trimStartMs by remember { mutableStateOf(0L) }
    var trimEndMs by remember { mutableStateOf(recording.durationMs.coerceAtLeast(1000L)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF071122),
        title = {
            Text("DSP RECORDING EDITOR", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Editing: ${recording.recordingName}", fontSize = 12.sp, color = Color.White)

                // Trim Sliders
                Column {
                    Text("Trim Range (${formatSecondsToTime((trimStartMs / 1000).toInt())} - ${formatSecondsToTime((trimEndMs / 1000).toInt())})", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    RangeSlider(
                        value = (trimStartMs.toFloat())..(trimEndMs.toFloat()),
                        onValueChange = { range ->
                            trimStartMs = range.start.toLong()
                            trimEndMs = range.endInclusive.toLong()
                        },
                        valueRange = 0f..(recording.durationMs.coerceAtLeast(1000L).toFloat())
                    )
                }

                // Action Buttons
                Button(
                    onClick = { onApplyEdit("TRIM", trimStartMs, trimEndMs) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("APPLY TRIM", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onApplyEdit("FADE", 1000L, 1000L) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fade In/Out", fontSize = 11.sp, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = { onApplyEdit("NORMALIZE", 0L, 0L) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Normalize", fontSize = 11.sp, color = Color.White)
                    }
                }

                OutlinedButton(
                    onClick = { onApplyEdit("DENOISE", 0L, 0L) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Noise Reduction Gate", fontSize = 11.sp, color = Color.White)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun RenameRecordingDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF071122),
        title = { Text("Rename Recording Asset", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00E5FF)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
            ) {
                Text("Save", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun EditNotesDialog(
    initialNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var notes by remember { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF071122),
        title = { Text("Edit User Notes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)) },
        text = {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00E5FF)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(notes) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
            ) {
                Text("Save Notes", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

fun formatSecondsToTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
