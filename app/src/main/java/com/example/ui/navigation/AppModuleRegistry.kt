package com.example.ui.navigation

import androidx.compose.ui.graphics.Color

data class AppModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val moduleTag: String,
    val color: Color,
    val category: String = "CORE WORKSPACES"
)

object AppModuleRegistry {
    val LAUNCHER_MODULE = AppModule(
        id = "launcher",
        title = "Main Workspace Launcher",
        subtitle = "Full-Screen Workspace Hub & Telemetry",
        emoji = "🎛️",
        moduleTag = "LAUNCHER",
        color = Color(0xFF00E5FF),
        category = "NAVIGATION"
    )

    val ALL_MODULES = listOf(
        LAUNCHER_MODULE,
        AppModule(
            id = "dashboard",
            title = "Dashboard",
            subtitle = "Main Workstation Overview & Telemetry",
            emoji = "🏠",
            moduleTag = "CORE 01",
            color = Color(0xFF00E5FF),
            category = "CORE WORKSPACES"
        ),
        AppModule(
            id = "audio_player",
            title = "Audio Player",
            subtitle = "Synchronized Multi-Format Stems Player",
            emoji = "🎵",
            moduleTag = "CORE 02",
            color = Color(0xFF3B82F6),
            category = "CORE WORKSPACES"
        ),
        AppModule(
            id = "analyzer",
            title = "Chord Analyzer",
            subtitle = "Realtime Frequency Spectrum & Harmony Timeline",
            emoji = "🎼",
            moduleTag = "ANALYSIS",
            color = Color(0xFF00E5FF),
            category = "ANALYSIS & HARMONY"
        ),
        AppModule(
            id = "arpeggio",
            title = "Arpeggio Intelligence",
            subtitle = "Smart Arpeggio & Dynamic Pattern Engine",
            emoji = "🎸",
            moduleTag = "ANALYSIS",
            color = Color(0xFFD4AF37),
            category = "ANALYSIS & HARMONY"
        ),
        AppModule(
            id = "piano",
            title = "Piano Viewer",
            subtitle = "Interactive 3D Piano Voicings & Chord Roll",
            emoji = "🎹",
            moduleTag = "VOICINGS",
            color = Color(0xFFEAB308),
            category = "INSTRUMENTS & VOICINGS"
        ),
        AppModule(
            id = "guitar",
            title = "Guitar Viewer",
            subtitle = "Polyphonic Guitar Fretboard & Custom Tunings",
            emoji = "🎸",
            moduleTag = "VOICINGS",
            color = Color(0xFFF97316),
            category = "INSTRUMENTS & VOICINGS"
        ),
        AppModule(
            id = "stems",
            title = "Stem Separation",
            subtitle = "AI 4-Stem Vocal, Guitar, Bass, Drums Studio",
            emoji = "🥁",
            moduleTag = "PROCESSING",
            color = Color(0xFF10B981),
            category = "STEMS & DSP"
        ),
        AppModule(
            id = "recorder",
            title = "Recorder",
            subtitle = "Low-Latency Mic Studio & Multi-Take History",
            emoji = "🎙",
            moduleTag = "AUDIO IN",
            color = Color(0xFFEF4444),
            category = "RECORDING & SESSIONS"
        ),
        AppModule(
            id = "bpm",
            title = "BPM Studio",
            subtitle = "Dynamic Tempo Pitch-Shift & Precision Metronome",
            emoji = "🎚",
            moduleTag = "TIMING",
            color = Color(0xFF8B5CF6),
            category = "TIMING & THEORY"
        ),
        AppModule(
            id = "key_detection",
            title = "Key Detection",
            subtitle = "Harmonic Key Signature & Circle of 5ths Engine",
            emoji = "🎼",
            moduleTag = "HARMONY",
            color = Color(0xFF06B6D4),
            category = "ANALYSIS & HARMONY"
        ),
        AppModule(
            id = "phrase_rec",
            title = "Phrase Recognition",
            subtitle = "Melodic Contour & Acoustic Lick Extractor",
            emoji = "🎵",
            moduleTag = "AI HARMONY",
            color = Color(0xFF3B82F6),
            category = "ANALYSIS & HARMONY"
        ),
        AppModule(
            id = "african_music",
            title = "African Music Intelligence",
            subtitle = "Afrobeat, Soukous, Amapiano & Modal Rhythms",
            emoji = "🌍",
            moduleTag = "AFRO MUSIC",
            color = Color(0xFFF59E0B),
            category = "SPECIALIZED INTELLIGENCE"
        ),
        AppModule(
            id = "dsp",
            title = "Audio Restoration",
            subtitle = "Spectral Denoise, Dehum & Declip Repair Engine",
            emoji = "🎛",
            moduleTag = "DSP",
            color = Color(0xFF10B981),
            category = "STEMS & DSP"
        ),
        AppModule(
            id = "midi",
            title = "MIDI Studio",
            subtitle = "Polyphonic MIDI Conversion & Sequence Export",
            emoji = "🎼",
            moduleTag = "MIDI",
            color = Color(0xFFEC4899),
            category = "MIDI & EXPORT"
        ),
        AppModule(
            id = "theory",
            title = "Music Theory",
            subtitle = "Modal Scales, Inversions & Cadence Guide",
            emoji = "📚",
            moduleTag = "THEORY",
            color = Color(0xFFA855F7),
            category = "TIMING & THEORY"
        ),
        AppModule(
            id = "quiz",
            title = "Practice Center",
            subtitle = "Ear Training Quizzes & Synced Karaoke Lyrics",
            emoji = "🎓",
            moduleTag = "TRAINING",
            color = Color(0xFF8B5CF6),
            category = "PRACTICE & EDUCATION"
        ),
        AppModule(
            id = "library",
            title = "Sessions",
            subtitle = "Saved Workspaces & Recorded Audio Stems",
            emoji = "📁",
            moduleTag = "STORAGE",
            color = Color(0xFF3B82F6),
            category = "RECORDING & SESSIONS"
        ),
        AppModule(
            id = "export",
            title = "Export Center",
            subtitle = "Multi-Format WAV, MP3, MIDI & Stems Export",
            emoji = "📤",
            moduleTag = "OUTPUT",
            color = Color(0xFF10B981),
            category = "MIDI & EXPORT"
        ),
        AppModule(
            id = "settings",
            title = "Settings",
            subtitle = "Audio Engine Latency, Buffers & System Options",
            emoji = "⚙",
            moduleTag = "SYSTEM",
            color = Color(0xFF64748B),
            category = "SYSTEM & HELP"
        ),
        AppModule(
            id = "about",
            title = "About",
            subtitle = "HZ CHORD AI Console Info, Build & License",
            emoji = "ℹ️",
            moduleTag = "INFO",
            color = Color(0xFF64748B),
            category = "SYSTEM & HELP"
        ),
        AppModule(
            id = "help",
            title = "Help",
            subtitle = "User Guide, Keyboard Shortcuts & Engine Diagnostics",
            emoji = "❓",
            moduleTag = "HELP",
            color = Color(0xFF00E5FF),
            category = "SYSTEM & HELP"
        )
    )

    fun searchModules(query: String): List<AppModule> {
        if (query.isBlank()) return ALL_MODULES
        val q = query.trim().lowercase()
        return ALL_MODULES.filter {
            it.title.lowercase().contains(q) ||
            it.subtitle.lowercase().contains(q) ||
            it.moduleTag.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.emoji.contains(q)
        }
    }

    fun findModule(id: String): AppModule {
        return ALL_MODULES.find { it.id.equals(id, ignoreCase = true) }
            ?: ALL_MODULES.find { it.id == "dashboard" }
            ?: LAUNCHER_MODULE
    }
}
