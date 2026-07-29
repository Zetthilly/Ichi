package com.example.data

data class ImportedAudioMetadata(
    val uriString: String? = null,
    val fileName: String,
    val artist: String = "Unknown Artist",
    val album: String = "Unknown Album",
    val durationMs: Long = 0L,
    val durationFormatted: String = "00:00",
    val bitrateKbps: String = "320 kbps",
    val sampleRateHz: String = "44.1 kHz",
    val channels: String = "Stereo (2 ch)",
    val fileSize: String = "0.0 MB",
    val formatExtension: String = "MP3",
    val sourceType: String = "Internal Storage",
    val waveformAmplitudes: List<Float> = emptyList(),
    val detectedBpm: Int = 120,
    val detectedKey: String = "C Major"
)
