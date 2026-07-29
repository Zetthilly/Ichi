package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_module_states")
data class SmartModuleStateEntity(
    @PrimaryKey val moduleId: String,
    val projectTitle: String = "HZ CHORD AI Session",
    val audioFilePath: String? = null,
    val uploadedFileName: String? = null,
    val playbackPositionMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val pitchShiftSemitones: Int = 0,
    val loopStartMs: Long = 0L,
    val loopEndMs: Long = 0L,
    val isLoopEnabled: Boolean = false,
    val currentChord: String = "Cmaj7",
    val chordTimelineJson: String = "",
    val currentArpeggioPattern: String = "Ascending 16ths",
    val phraseDetectionResultsJson: String = "",
    val bpm: Int = 120,
    val detectedKey: String = "C Major",
    val detectedScale: String = "Ionian (Major)",
    val africanMusicStyle: String = "Afrobeat 3:2 Polyrhythm",
    val stemSeparationResultsJson: String = "",
    val mixerVolumesJson: String = "",
    val mixerSoloMuteJson: String = "",
    val equalizerSettingsJson: String = "",
    val waveformZoom: Float = 1.0f,
    val timelinePositionMs: Long = 0L,
    val selectedPianoView: String = "3D Grand Piano - Voicings",
    val selectedGuitarView: String = "Polyphonic Fretboard - Standard Tuning",
    val selectedInstrument: String = "Acoustic Guitar",
    val lyricsPositionMs: Long = 0L,
    val practiceProgressScore: Int = 85,
    val sessionNotes: String = "",
    val undoRedoHistoryJson: String = "",
    val modulePreferencesJson: String = "",
    val screenLayout: String = "Single Active Workspace",
    val themeSettings: String = "Cyber Off-Black Cyan",
    val lastActiveModuleId: String = "dashboard",
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_global_session")
data class AppGlobalSessionEntity(
    @PrimaryKey val id: Int = 1,
    val lastActiveModuleId: String = "dashboard",
    val activeProjectTitle: String = "HZ CHORD AI Session",
    val activeAudioFilePath: String? = null,
    val uploadedFileName: String? = null,
    val playbackPositionMs: Long = 0L,
    val bpm: Int = 120,
    val keySignature: String = "C Major",
    val hasUnsavedSession: Boolean = true,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)
