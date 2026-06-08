package com.example.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin
import kotlin.random.Random
import com.squareup.moshi.JsonClass

// Sealed state for AI Stem Separation
sealed class StemSeparationState {
    object Idle : StemSeparationState()
    data class Processing(val progress: Float, val mode: String, val etaSeconds: Int) : StemSeparationState()
    data class Success(val vocalsVolume: Float, val melodyVolume: Float, val bassVolume: Float, val drumsVolume: Float) : StemSeparationState()
}

// Simulated tuner note info
data class TuningNote(
    val noteName: String,
    val targetFreq: Float,
    val currentFreq: Float,
    val deviationCents: Float, // -50 to +50
    val isTuned: Boolean
)

// Main Chord recognition entry
@JsonClass(generateAdapter = true)
data class DetectedChordInfo(
    val name: String,
    val root: String,
    val formula: String,
    val notes: List<String>,
    val confidence: Float,
    val frequency: Float,
    val type: String, // e.g., "Major", "Seventh", "Ext-Jazz", "Extended Voicing"
    val description: String,
    val suggestedSubstitutions: List<String>
)

// Real-time scrolling chord timeline item representing historical occurrences
@JsonClass(generateAdapter = true)
data class TimelineChordEntry(
    val id: String,
    val name: String,
    val root: String,
    val type: String,
    val timestamp: String,
    val confidence: Float,
    val notes: List<String>
)

class AudioWorkstationEngine {

    // Chord & Arpeggio Analyzer State
    private val _currentChord = MutableStateFlow<DetectedChordInfo?>(null)
    val currentChord: StateFlow<DetectedChordInfo?> = _currentChord.asStateFlow()

    private val _chordTimeline = MutableStateFlow<List<TimelineChordEntry>>(emptyList())
    val chordTimeline: StateFlow<List<TimelineChordEntry>> = _chordTimeline.asStateFlow()

    private var timelineStepCounter = 1

    private val _detectionMode = MutableStateFlow("Combined Analysis") // Exact, Inferred, Combined
    val detectionMode: StateFlow<String> = _detectionMode.asStateFlow()

    // Interactive Piano or Guitar tapped notes to trigger arpeggios
    private val _liveNotesBuffer = MutableStateFlow<List<String>>(emptyList())
    val liveNotesBuffer: StateFlow<List<String>> = _liveNotesBuffer.asStateFlow()

    private val _detectedArpeggio = MutableStateFlow<String?>(null)
    val detectedArpeggio: StateFlow<String?> = _detectedArpeggio.asStateFlow()

    // Specialized African Guitar styles
    private val _africanStyleLick = MutableStateFlow<String?>(null)
    val africanStyleLick: StateFlow<String?> = _africanStyleLick.asStateFlow()

    // Audio Tuner State
    private val _tunerState = MutableStateFlow(TuningNote("E2", 82.41f, 82.1f, -6f, false))
    val tunerState: StateFlow<TuningNote> = _tunerState.asStateFlow()

    // BPM Studio State
    private val _bpm = MutableStateFlow(120)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _tempoPreservedMultiplier = MutableStateFlow(1.0f) // 0.5x to 2.0x
    val tempoPreservedMultiplier: StateFlow<Float> = _tempoPreservedMultiplier.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingTimerSeconds = MutableStateFlow(0)
    val recordingTimerSeconds: StateFlow<Int> = _recordingTimerSeconds.asStateFlow()

    // Stem separation progress state
    private val _stemSeparation = MutableStateFlow<StemSeparationState>(StemSeparationState.Idle)
    val stemSeparation: StateFlow<StemSeparationState> = _stemSeparation.asStateFlow()

    private val _uploadedFileName = MutableStateFlow<String?>(null)
    val uploadedFileName: StateFlow<String?> = _uploadedFileName.asStateFlow()

    private val _uploadedFileSize = MutableStateFlow<String?>(null)
    val uploadedFileSize: StateFlow<String?> = _uploadedFileSize.asStateFlow()

    private val _aiAnalysisResult = MutableStateFlow<String?>(null)
    val aiAnalysisResult: StateFlow<String?> = _aiAnalysisResult.asStateFlow()

    private val _isStemPlaybackActive = MutableStateFlow(false)
    val isStemPlaybackActive: StateFlow<Boolean> = _isStemPlaybackActive.asStateFlow()

    // Audio Restoration effects
    private val _noiseReductionEnabled = MutableStateFlow(false)
    val noiseReductionEnabled: StateFlow<Boolean> = _noiseReductionEnabled.asStateFlow()

    private val _humRemovalEnabled = MutableStateFlow(false)
    val humRemovalEnabled: StateFlow<Boolean> = _humRemovalEnabled.asStateFlow()

    private val _clippingRepairEnabled = MutableStateFlow(false)
    val clippingRepairEnabled: StateFlow<Boolean> = _clippingRepairEnabled.asStateFlow()

    private val _vocalEnhancementEnabled = MutableStateFlow(false)
    val vocalEnhancementEnabled: StateFlow<Boolean> = _vocalEnhancementEnabled.asStateFlow()

    // Real-time Waveform visualizer synthesizer seeds
    private var timeSeed = 0.0f

    init {
        // Set an initial active chord C Major
        selectChord("C")
    }

    fun setDetectionMode(mode: String) {
        _detectionMode.value = mode
    }

    fun selectChord(chordSymbol: String) {
        val info = buildChordInfo(chordSymbol)
        _currentChord.value = info
        addToTimeline(info)
    }

    private fun addToTimeline(info: DetectedChordInfo) {
        val currentList = _chordTimeline.value.toMutableList()
        // Compute timing label
        val seconds = (timelineStepCounter * 2) % 60
        val minutes = (timelineStepCounter * 2) / 60
        val timestamp = String.format("%02d:%02d", minutes, seconds)
        
        val newEntry = TimelineChordEntry(
            id = java.util.UUID.randomUUID().toString(),
            name = info.name,
            root = info.root,
            type = info.type,
            timestamp = timestamp,
            confidence = info.confidence,
            notes = info.notes
        )
        // Keep list bounded for smooth scrolling
        if (currentList.size >= 20) {
            currentList.removeAt(0)
        }
        currentList.add(newEntry)
        _chordTimeline.value = currentList
        timelineStepCounter++
    }

    fun clearTimeline() {
        _chordTimeline.value = emptyList()
        timelineStepCounter = 1
    }

    fun simulateProgression(progression: List<String>) {
        for (symbol in progression) {
            selectChord(symbol)
        }
    }

    fun toggleRecording() {
        _isRecording.value = !_isRecording.value
        if (!_isRecording.value) {
            _recordingTimerSeconds.value = 0
        }
    }

    fun tickRecordingTimer() {
        if (_isRecording.value) {
            _recordingTimerSeconds.value += 1
        }
    }

    fun tapTempo() {
        // Simple tap tempo simulator adds or scales BPM slightly
        val newBpm = _bpm.value + 1
        _bpm.value = if (newBpm > 240) 60 else newBpm
    }

    fun setBpm(value: Int) {
        _bpm.value = value.coerceIn(40, 300)
    }

    fun adjustBpm(delta: Int) {
        val calculated = _bpm.value + delta
        _bpm.value = calculated.coerceIn(40, 300)
    }

    fun adjustSpeedMultiplier(multiplier: Float) {
        _tempoPreservedMultiplier.value = multiplier.coerceIn(0.5f, 2.0f)
    }

    fun toggleNoiseReduction() { _noiseReductionEnabled.value = !_noiseReductionEnabled.value }
    fun toggleHumRemoval() { _humRemovalEnabled.value = !_humRemovalEnabled.value }
    fun toggleClippingRepair() { _clippingRepairEnabled.value = !_clippingRepairEnabled.value }
    fun toggleVocalEnhancement() { _vocalEnhancementEnabled.value = !_vocalEnhancementEnabled.value }

    fun setNoiseReductionEnabled(enabled: Boolean) { _noiseReductionEnabled.value = enabled }
    fun setHumRemovalEnabled(enabled: Boolean) { _humRemovalEnabled.value = enabled }
    fun setClippingRepairEnabled(enabled: Boolean) { _clippingRepairEnabled.value = enabled }
    fun setVocalEnhancementEnabled(enabled: Boolean) { _vocalEnhancementEnabled.value = enabled }
    fun setStemSeparationState(state: StemSeparationState) { _stemSeparation.value = state }
    fun setCurrentChord(chord: DetectedChordInfo?) { _currentChord.value = chord }
    fun setChordTimeline(timeline: List<TimelineChordEntry>) {
        _chordTimeline.value = timeline
        timelineStepCounter = (timeline.size + 1).coerceAtLeast(1)
    }

    // Simulates an offline guitar tuner note tuner alignment
    fun selectTunerBaseNote(note: String) {
        val targetFreq = when(note) {
            "E2" -> 82.41f
            "A2" -> 110.00f
            "D3" -> 146.83f
            "G3" -> 196.00f
            "B3" -> 246.94f
            "E4" -> 329.63f
            else -> 440.00f // A4
        }
        val dev = Random.nextInt(-40, 40).toFloat()
        val current = targetFreq + (dev * 0.1f)
        _tunerState.value = TuningNote(
            noteName = note,
            targetFreq = targetFreq,
            currentFreq = current,
            deviationCents = dev,
            isTuned = kotlin.math.abs(dev) < 3
        )
    }

    fun autoTuneTuner() {
        val currentInfo = _tunerState.value
        _tunerState.value = currentInfo.copy(
            currentFreq = currentInfo.targetFreq,
            deviationCents = 0.0f,
            isTuned = true
        )
    }

    // Interactive notes additions (Arpeggio buffer)
    fun tapLiveMusicalNote(note: String) {
        val currentList = _liveNotesBuffer.value.toMutableList()
        if (currentList.size >= 8) {
            currentList.removeAt(0)
        }
        currentList.add(note)
        _liveNotesBuffer.value = currentList

        // Match patterns to identify arpeggios
        val normalizedNotes = currentList.map { it.filter { char -> char.isLetter() } }.toSet()
        val hasC = normalizedNotes.contains("C")
        val hasE = normalizedNotes.contains("E")
        val hasG = normalizedNotes.contains("G")
        val hasB = normalizedNotes.contains("B")
        val hasA = normalizedNotes.contains("A")
        val hasD = normalizedNotes.contains("D")

        // Melodic triggers
        if (hasC && hasE && hasG) {
            if (hasB) {
                _detectedArpeggio.value = "C Major 7th Arpeggio (C-E-G-B)"
                selectChord("Cmaj7")
            } else {
                _detectedArpeggio.value = "C Major Arpeggio (C-E-G)"
                selectChord("C")
            }
        } else if (hasA && hasC && hasE) {
            _detectedArpeggio.value = "A Minor Arpeggio (A-C-E)"
            selectChord("Am")
        } else if (hasG && hasB && hasD) {
            _detectedArpeggio.value = "G Major Arpeggio (G-B-D)"
            selectChord("G")
        } else if (hasD && hasF(currentList) && hasA) {
            _detectedArpeggio.value = "D Minor Arpeggio (D-F-A)"
            selectChord("Dm")
        } else {
            _detectedArpeggio.value = null
        }

        // African Guitar Styles Trigger matching note counts
        // African guitar runs are usually fast, high-triplet lines:
        if (currentList.size >= 4) {
            val randomVal = Random.nextInt(0, 6)
            _africanStyleLick.value = when(randomVal) {
                0 -> "Sungura Lead Hook (High-pitch fast triplets in A major)"
                1 -> "Soukous Double-Stop Solo Pattern (G-C run)"
                2 -> "Gbema/Jit Melodic Bass Walk (Syncopated pentatonic sliding)"
                3 -> "Rhumba Seben Rhythm progression (Arpeggiated I-IV-V-IV)"
                4 -> "Afro-Jazz Melodic Cadence (Dorian scale hybrid)"
                5 -> "Gospel Lead Guitar Arpeggio Run (Sweet cascading harmony)"
                else -> null
            }
        }
    }

    private fun hasF(notes: List<String>): Boolean {
        return notes.any { it.contains("F") || it.contains("F#") }
    }

    fun clearLiveNotes() {
        _liveNotesBuffer.value = emptyList()
        _detectedArpeggio.value = null
        _africanStyleLick.value = null
    }

    // Simulated offline high-fidelity neural model separation progress
    fun startStemSeparation(mode: String) {
        _stemSeparation.value = StemSeparationState.Processing(0.01f, mode, 8)
    }

    fun updateStemProgress(progress: Float) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Processing) {
            if (progress >= 1.0f) {
                _stemSeparation.value = StemSeparationState.Success(1.0f, 1.0f, 0.8f, 0.8f)
            } else {
                val eta = ((1.0f - progress) * 8).toInt()
                _stemSeparation.value = StemSeparationState.Processing(progress, currentState.mode, eta)
            }
        }
    }

    fun adjustStemVolume(stem: String, volume: Float) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            _stemSeparation.value = when(stem) {
                "vocals" -> currentState.copy(vocalsVolume = volume)
                "melody" -> currentState.copy(melodyVolume = volume)
                "bass" -> currentState.copy(bassVolume = volume)
                "drums" -> currentState.copy(drumsVolume = volume)
                else -> currentState
            }
        }
    }

    fun resetStemSeparation() {
        _stemSeparation.value = StemSeparationState.Idle
        _isStemPlaybackActive.value = false
    }

    fun setUploadedFile(name: String?, size: String?) {
        _uploadedFileName.value = name
        _uploadedFileSize.value = size
        _isStemPlaybackActive.value = false
        if (name == null) {
            _aiAnalysisResult.value = null
            _stemSeparation.value = StemSeparationState.Idle
        }
    }

    fun toggleStemPlayback() {
        _isStemPlaybackActive.value = !_isStemPlaybackActive.value
    }

    fun setStemPlayback(active: Boolean) {
        _isStemPlaybackActive.value = active
    }

    fun setAiAnalysisResult(result: String?) {
        _aiAnalysisResult.value = result
    }

    // Synthesizes raw points mapping real-time frequency spectrum graphs
    fun generateRealtimeFFTAmplitudes(binCount: Int): FloatArray {
        timeSeed += 0.2f
        val result = FloatArray(binCount)
        val chordInfo = _currentChord.value
        val baseFreq = chordInfo?.frequency ?: 440f
        
        // Base sine wave modulation to simulate dynamic mic or playback frequency peaks
        for (i in 0 until binCount) {
            // Background white-noise floor
            var amp = 0.05f + Random.nextFloat() * 0.06f
            
            // Map frequencies to bin indexes
            // Let's create sharp peaks corresponding to chord notes
            val frequencyMultiplier = baseFreq / 20.0f
            val notePeakBin = ((frequencyMultiplier % binCount).toInt() + i) % binCount
            
            if (i == notePeakBin || i == (notePeakBin * 2) % binCount || i == (notePeakBin * 3) % binCount) {
                amp += 0.8f * (0.6f + 0.4f * sin(timeSeed + i))
            }

            // High frequency roll-off
            val rollOff = 1.0f - (i.toFloat() / binCount) * 0.5f
            result[i] = (amp * rollOff).coerceIn(0.0f, 1.0f)
        }
        return result
    }

    fun generateWaveformSamples(sampleCount: Int): FloatArray {
        val result = FloatArray(sampleCount)
        timeSeed += 0.05f
        for (i in 0 until sampleCount) {
            val theta = i.toFloat() / sampleCount * 4.0f * Math.PI.toFloat()
            // Multiple sinusoids + random hum
            var value = sin(theta * 2f + timeSeed) * 0.5f + sin(theta * 5.3f - timeSeed * 0.5f) * 0.3f
            if (Random.nextInt(100) > 97) value += Random.nextFloat() * 0.2f // simulation spike clipping
            result[i] = value.coerceIn(-1.0f, 1.0f)
        }
        return result
    }

    // Static Dictionary Builder for Extended/Slash/Hybrid chord details
    private fun buildChordInfo(symbol: String): DetectedChordInfo {
        return when (symbol) {
            "C" -> DetectedChordInfo(
                "C Major", "C", "1 - 3 - 5", listOf("C", "E", "G"),
                0.98f, 261.63f, "Major Triad", "Basic major harmony found across all music genres.",
                listOf("Cmaj7", "Am", "F")
            )
            "G" -> DetectedChordInfo(
                "G Major", "G", "1 - 3 - 5", listOf("G", "B", "D"),
                0.97f, 392.00f, "Major Triad", "Dominant pillar chord in G Major scales.",
                listOf("G7", "Em", "C")
            )
            "D" -> DetectedChordInfo(
                "D Major", "D", "1 - 3 - 5", listOf("D", "F#", "A"),
                0.96f, 293.66f, "Major Triad", "Bright resonant key signature support.",
                listOf("D7", "Bm", "G")
            )
            "Am" -> DetectedChordInfo(
                "A Minor", "A", "1 - b3 - 5", listOf("A", "C", "E"),
                0.98f, 220.00f, "Minor Triad", "Natural minor root chord, highly warm.",
                listOf("Am7", "C", "F")
            )
            "Em" -> DetectedChordInfo(
                "E Minor", "E", "1 - b3 - 5", listOf("E", "G", "B"),
                0.97f, 164.81f, "Minor Triad", "Deep low voicing, great for lead walks.",
                listOf("Em7", "G", "C")
            )
            "F" -> DetectedChordInfo(
                "F Major", "F", "1 - 3 - 5", listOf("F", "A", "C"),
                0.95f, 349.23f, "Subdominant Major", "Subdominant support in key of C.",
                listOf("Fmaj7", "Dm", "G")
            )
            "Dm" -> DetectedChordInfo(
                "D Minor", "D", "1 - b3 - 5", listOf("D", "F", "A"),
                0.94f, 293.66f, "Minor Triad", "Sorrowful minor harmony.",
                listOf("Dm7", "F", "G")
            )
            "Cmaj7" -> DetectedChordInfo(
                "C Major 7th", "C", "1 - 3 - 5 - 7", listOf("C", "E", "G", "B"),
                0.92f, 261.63f, "Major Seventh", "Rich jazz voicing, adds beautiful airy color.",
                listOf("C9", "Am9", "Em7")
            )
            "Am7" -> DetectedChordInfo(
                "A Minor 7th", "A", "1 - b3 - 5 - b7", listOf("A", "C", "E", "G"),
                0.91f, 220.00f, "Minor Seventh", "Jazzy soft minor voicing.",
                listOf("Am9", "Cmaj7", "Dm7")
            )
            "G7" -> DetectedChordInfo(
                "G Dominant 7th", "G", "1 - 3 - 5 - b7", listOf("G", "B", "D", "F"),
                0.93f, 392.00f, "Dominant Seventh", "Tension-filled bluesy chord leading to root C.",
                listOf("G9", "G13", "Bdim")
            )
            "Am9" -> DetectedChordInfo(
                "A Minor 9th", "A", "1 - b3 - 5 - b7 - 9", listOf("A", "C", "E", "G", "B"),
                0.89f, 220.00f, "Extended Jazz", "Sophisticated extensions in contemporary R&B and jazz.",
                listOf("Am11", "D9", "Em9")
            )
            "C/G" -> DetectedChordInfo(
                "C Major / G Bass", "G", "Slash chord", listOf("G", "C", "E"),
                0.88f, 196.00f, "Inversion / Slash Chord", "C Major played with alternative G root bass note.",
                listOf("Am7", "C", "F/G")
            )
            "G13" -> DetectedChordInfo(
                "G Dominant 13th", "G", "1-3-5-b7-9-13", listOf("G", "B", "D", "F", "A", "E"),
                0.85f, 392.00f, "Jazz Extended Voicing", "Full-spectrum jazz Dominant harmony.",
                listOf("G9", "Cmaj9", "Abdim")
            )
            "Sungura A" -> DetectedChordInfo(
                "A Major (Sungura)", "A", "Fast Triplet Voicing", listOf("A", "C#", "E"),
                0.96f, 440.00f, "African Guitar Style", "characteristic bright Sungura backing chord.",
                listOf("D", "E7", "F#m")
            )
            else -> DetectedChordInfo(
                "$symbol Major", symbol, "1 - 3 - 5", listOf(symbol, "unknown", "unknown"),
                0.90f, 440.00f, "Major Triad", "Standard chord alignment.",
                listOf("C", "G", "F")
            )
        }
    }
}
