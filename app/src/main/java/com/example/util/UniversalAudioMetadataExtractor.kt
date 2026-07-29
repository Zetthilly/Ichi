package com.example.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.data.ImportedAudioMetadata
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

object UniversalAudioMetadataExtractor {

    fun extractMetadataFromUri(
        context: Context,
        uri: Uri,
        sourceType: String = "File Manager"
    ): ImportedAudioMetadata {
        var fileName = "imported_audio.mp3"
        var fileSizeByte = 0L

        // Query ContentResolver for display name and size
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIdx != -1) {
                        val nameStr = cursor.getString(nameIdx)
                        if (!nameStr.isNullOrEmpty()) fileName = nameStr
                    }
                    if (sizeIdx != -1) {
                        fileSizeByte = cursor.getLong(sizeIdx)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val formatExt = getFormatFromFileName(fileName)

        var artist = "Unknown Artist"
        var album = "Unknown Album"
        var durationMs = 180000L
        var bitrate = "320 kbps"
        var sampleRate = "44.1 kHz"
        var channels = "Stereo (2 ch)"

        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(context, uri)
            val extractedTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val extractedArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val extractedAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val extractedDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val extractedBitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)

            if (!extractedArtist.isNullOrBlank()) artist = extractedArtist
            if (!extractedAlbum.isNullOrBlank()) album = extractedAlbum
            if (!extractedTitle.isNullOrBlank() && fileName == "imported_audio.mp3") {
                fileName = "$extractedTitle.$formatExt"
            }

            extractedDuration?.toLongOrNull()?.let {
                if (it > 0) durationMs = it
            }

            extractedBitrate?.toIntOrNull()?.let {
                if (it > 0) bitrate = "${it / 1000} kbps"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try { mmr.release() } catch (_: Exception) {}
        }

        val fileSizeFormatted = formatFileSize(fileSizeByte, durationMs)
        val durationFormatted = formatDurationMs(durationMs)
        val waveform = generateWaveformPreview(fileName, 48)

        val (detectedBpm, detectedKey) = inferKeyAndBpmFromFilename(fileName)

        return ImportedAudioMetadata(
            uriString = uri.toString(),
            fileName = fileName,
            artist = artist,
            album = album,
            durationMs = durationMs,
            durationFormatted = durationFormatted,
            bitrateKbps = bitrate,
            sampleRateHz = sampleRate,
            channels = channels,
            fileSize = fileSizeFormatted,
            formatExtension = formatExt.uppercase(),
            sourceType = sourceType,
            waveformAmplitudes = waveform,
            detectedBpm = detectedBpm,
            detectedKey = detectedKey
        )
    }

    fun createPresetMetadata(
        fileName: String,
        artist: String,
        album: String,
        durationFormatted: String,
        bitrateKbps: String,
        sampleRateHz: String,
        channels: String,
        fileSize: String,
        formatExtension: String,
        sourceType: String,
        bpm: Int,
        key: String
    ): ImportedAudioMetadata {
        val durationParts = durationFormatted.split(":")
        val durationMs = if (durationParts.size == 2) {
            ((durationParts[0].toLongOrNull() ?: 3) * 60 + (durationParts[1].toLongOrNull() ?: 0)) * 1000L
        } else 180000L

        return ImportedAudioMetadata(
            uriString = null,
            fileName = fileName,
            artist = artist,
            album = album,
            durationMs = durationMs,
            durationFormatted = durationFormatted,
            bitrateKbps = bitrateKbps,
            sampleRateHz = sampleRateHz,
            channels = channels,
            fileSize = fileSize,
            formatExtension = formatExtension.uppercase(),
            sourceType = sourceType,
            waveformAmplitudes = generateWaveformPreview(fileName, 48),
            detectedBpm = bpm,
            detectedKey = key
        )
    }

    private fun getFormatFromFileName(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "mp3" -> "MP3"
            "wav" -> "WAV"
            "flac" -> "FLAC"
            "aac" -> "AAC"
            "ogg" -> "OGG"
            "m4a" -> "M4A"
            "aiff", "aif" -> "AIFF"
            "wma" -> "WMA"
            else -> if (ext.isNotEmpty()) ext.uppercase() else "MP3"
        }
    }

    private fun formatFileSize(sizeInBytes: Long, fallbackDurationMs: Long): String {
        if (sizeInBytes > 0) {
            val mb = sizeInBytes.toDouble() / (1024 * 1024)
            return String.format("%.1f MB", mb)
        }
        val estimatedMb = (fallbackDurationMs / 1000L) * 0.04
        return String.format("%.1f MB", estimatedMb.coerceAtLeast(1.2))
    }

    private fun formatDurationMs(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun generateWaveformPreview(seedStr: String, count: Int = 48): List<Float> {
        val hash = abs(seedStr.hashCode())
        val random = Random(hash)
        val list = mutableListOf<Float>()
        for (i in 0 until count) {
            val base = sin(i * 0.25f) * 0.3f + 0.5f
            val noise = random.nextFloat() * 0.4f
            val amp = (base + noise).coerceIn(0.15f, 0.98f)
            list.add(amp)
        }
        return list
    }

    private fun inferKeyAndBpmFromFilename(fileName: String): Pair<Int, String> {
        val lower = fileName.lowercase()
        val bpmRegex = "\\b(\\d{2,3})\\b".toRegex()
        val match = bpmRegex.find(fileName)
        val bpm = match?.value?.toIntOrNull()?.takeIf { it in 60..200 } ?: when {
            lower.contains("seben") || lower.contains("sungura") -> 135
            lower.contains("rhumba") || lower.contains("soukous") -> 124
            lower.contains("jazz") || lower.contains("blues") -> 108
            lower.contains("gospel") -> 116
            else -> 120
        }

        val key = when {
            lower.contains("f#") || lower.contains("fsharp") -> "F# Major"
            lower.contains("d#m") || lower.contains("ebm") -> "D# Minor"
            lower.contains("am") -> "A Minor"
            lower.contains("a_major") || lower.contains("amaj") -> "A Major"
            lower.contains("g") -> "G Major"
            lower.contains("d") -> "D Major"
            else -> "C Major"
        }

        return Pair(bpm, key)
    }
}
