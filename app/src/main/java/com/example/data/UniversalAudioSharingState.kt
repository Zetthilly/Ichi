package com.example.data

data class SharingModuleStage(
    val id: String,
    val name: String,
    val description: String,
    val isBypassed: Boolean = false,
    val syncLatencyMs: Long = 0L,
    val statusMessage: String = "Direct Shared Stream Active",
    val gainDb: Float = 0.0f
)

data class UniversalAudioSharingState(
    val activeProjectId: String = "PROJECT_INIT_01",
    val activeProjectTitle: String = "HZ Chord AI Universal Session",
    val audioSourceFileName: String = "sungura_master_lead.flac",
    val audioDurationMs: Long = 258000L,
    val currentPlayheadMs: Long = 0L,
    val isPlaying: Boolean = false,
    val sampleRateHz: Int = 48000,
    val channels: Int = 2,
    val bitrateKbps: Int = 1412,
    val sharedBufferMemoryRef: String = "0x7F9B1000 (Zero-Copy Unified Pointer)",
    val duplicateFilesCreated: Int = 0,
    val oboeBackendName: String = "Oboe AAudio Low-Latency (C++)",
    val oboeLatencyMs: Float = 4.2f,
    val oboeEngineRunning: Boolean = true,
    val modulesChain: List<SharingModuleStage> = listOf(
        SharingModuleStage("import", "Import", "Ingest internal/external storage audio & metadata without file dups"),
        SharingModuleStage("audio_player", "Audio Player", "Synchronized multi-rate playback engine"),
        SharingModuleStage("chord_detection", "Chord Detection", "Harmonic pitch-class profile correlation & triad inference"),
        SharingModuleStage("arpeggio_intelligence", "Arpeggio Intelligence", "Sub-beat arpeggio pattern & scale note decoder"),
        SharingModuleStage("phrase_recognition", "Phrase Recognition", "African Guitar, Lick & motif phrasing extractor"),
        SharingModuleStage("bpm_studio", "BPM Studio", "Spectral flux transient onset & tempo preservation lock"),
        SharingModuleStage("key_detection", "Key Detection", "Global 12-chroma tonic-dominant key signature solver"),
        SharingModuleStage("stem_separation", "Stem Separation", "Vocal, Melody, Bass, Drums stem isolation matrix"),
        SharingModuleStage("audio_restoration", "Audio Restoration", "Spectral noise reduction, hum filter & clipping repair"),
        SharingModuleStage("recorder", "Recorder", "Zero-latency overdub & reference audio monitor layer"),
        SharingModuleStage("visualization", "Visualization", "Realtime FFT spectrum, waveform & 3D chroma scope"),
        SharingModuleStage("export", "Export", "Single-pass non-destructive bounce & project archive")
    )
)
