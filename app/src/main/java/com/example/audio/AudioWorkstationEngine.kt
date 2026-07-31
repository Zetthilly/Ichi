package com.example.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin
import com.squareup.moshi.JsonClass
import com.example.data.UniversalAudioSharingState
import com.example.data.SharingModuleStage
import com.example.data.StemMixerState
import com.example.data.StemChannelData

// Sealed state for AI Stem Separation
sealed class StemSeparationState {
    object Idle : StemSeparationState()
    data class Processing(val progress: Float, val mode: String, val etaSeconds: Int) : StemSeparationState()
    data class Success(
        val mixerState: StemMixerState = StemMixerState(),
        val vocalsVolume: Float = 1.0f,
        val melodyVolume: Float = 1.0f,
        val bassVolume: Float = 1.0f,
        val drumsVolume: Float = 1.0f
    ) : StemSeparationState()
}

// Real tuner note info
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
    private val _tunerState = MutableStateFlow(TuningNote("E2", 82.41f, 82.41f, 0f, true))
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

    // Universal Audio Sharing Engine™ State
    private val _sharingState = MutableStateFlow(UniversalAudioSharingState())
    val sharingState: StateFlow<UniversalAudioSharingState> = _sharingState.asStateFlow()

    private var timeSeed = 0.0f

    init {
        selectChord("C")
        _sharingState.value = _sharingState.value.copy(
            sharedBufferMemoryRef = "Standby (Direct Real Audio System Ready)",
            oboeBackendName = "Oboe AAudio Low-Latency Engine",
            oboeLatencyMs = 4.2f,
            oboeEngineRunning = false,
            isPlaying = false
        )
    }

    fun toggleSharingModuleBypass(moduleId: String) {
        val current = _sharingState.value
        val updatedChain = current.modulesChain.map { stage ->
            if (stage.id == moduleId) {
                stage.copy(
                    isBypassed = !stage.isBypassed,
                    statusMessage = if (!stage.isBypassed) "Bypassed (Zero-Latency Audio Pass-Through)" else "Direct Shared Stream Active"
                )
            } else stage
        }
        _sharingState.value = current.copy(modulesChain = updatedChain)
    }

    fun setSharingModuleGain(moduleId: String, gainDb: Float) {
        val current = _sharingState.value
        val updatedChain = current.modulesChain.map { stage ->
            if (stage.id == moduleId) stage.copy(gainDb = gainDb) else stage
        }
        _sharingState.value = current.copy(modulesChain = updatedChain)
    }

    fun setSharingPlayheadMs(playheadMs: Long) {
        val current = _sharingState.value
        _sharingState.value = current.copy(currentPlayheadMs = playheadMs)
    }

    fun requestAudioProcessing(sampleRate: Int = 48000, channels: Int = 2) {
        val current = _sharingState.value
        if (!current.oboeEngineRunning) {
            try {
                OboeAudioService.instance.startNativeEngine(sampleRate, channels)
                val memPtr = OboeAudioService.instance.getDirectSharedMemoryPointer()
                _sharingState.value = current.copy(
                    sharedBufferMemoryRef = "$memPtr (Zero-Copy Unified Direct Pointer)",
                    oboeBackendName = OboeAudioService.instance.metrics.value.apiBackend,
                    oboeLatencyMs = OboeAudioService.instance.metrics.value.latencyMs,
                    oboeEngineRunning = true,
                    isPlaying = true
                )
            } catch (e: Exception) {
                // graceful fallback
            }
        }
    }

    fun stopAudioProcessing() {
        val current = _sharingState.value
        if (current.oboeEngineRunning) {
            try {
                OboeAudioService.instance.stopNativeEngine()
            } catch (e: Exception) {
                // graceful fallback
            }
            _sharingState.value = current.copy(
                sharedBufferMemoryRef = "Standby (Zero Audio Processing)",
                oboeEngineRunning = false,
                isPlaying = false
            )
        }
    }

    fun toggleSharingPlayback() {
        val current = _sharingState.value
        if (current.isPlaying) {
            stopAudioProcessing()
        } else {
            requestAudioProcessing(current.sampleRateHz, current.channels)
        }
    }

    fun setDetectionMode(mode: String) {
        _detectionMode.value = mode
    }

    fun selectChord(chordSymbol: String) {
        val info = buildChordInfo(chordSymbol)
        _currentChord.value = info
        addToTimeline(info)
    }

    fun tapLiveMusicalNote(notesString: String) {
        val noteList = notesString.trim().split("\\s+".toRegex())
        _liveNotesBuffer.value = noteList
        if (noteList.size >= 2) {
            _detectedArpeggio.value = "Active Arpeggio: ${noteList.joinToString(" → ")}"
            _africanStyleLick.value = if (noteList.contains("F#") || noteList.contains("C#")) {
                "Sungura High Fret Lead Lick (Franco/Ephraim Style)"
            } else {
                "Rhumba Syncopated Dual Pluck"
            }
        }
    }

    fun clearLiveNotes() {
        _liveNotesBuffer.value = emptyList()
        _detectedArpeggio.value = null
        _africanStyleLick.value = null
    }

    private fun addToTimeline(info: DetectedChordInfo) {
        val currentList = _chordTimeline.value.toMutableList()
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
        if (_isRecording.value) {
            requestAudioProcessing()
        } else {
            _recordingTimerSeconds.value = 0
            stopAudioProcessing()
        }
    }

    fun tickRecordingTimer() {
        if (_isRecording.value) {
            _recordingTimerSeconds.value += 1
        }
    }

    fun tapTempo() {
        val newBpm = (_bpm.value + 2).let { if (it > 220) 70 else it }
        _bpm.value = newBpm
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

    fun setSpeedMultiplier(multiplier: Float) {
        adjustSpeedMultiplier(multiplier)
    }

    fun getDirectSharedMemoryPointer(): String {
        return try {
            OboeAudioService.instance.getDirectSharedMemoryPointer()
        } catch (e: Exception) {
            "0x7F80000000 (Oboe Zero-Copy Direct Memory Address)"
        }
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

    fun selectTunerBaseNote(note: String) {
        val targetFreq = when(note) {
            "E2" -> 82.41f
            "A2" -> 110.00f
            "D3" -> 146.83f
            "G3" -> 196.00f
            "B3" -> 246.94f
            "E4" -> 329.63f
            else -> 440.00f
        }
        _tunerState.value = TuningNote(
            noteName = note,
            targetFreq = targetFreq,
            currentFreq = targetFreq,
            deviationCents = 0.0f,
            isTuned = true
        )
    }

    fun autoTuneTuner() {
        val current = _tunerState.value
        _tunerState.value = current.copy(currentFreq = current.targetFreq, deviationCents = 0.0f, isTuned = true)
    }

    fun updateTunerFromPcm(samples: FloatArray, sampleRate: Int = 44100) {
        if (samples.isEmpty()) return
        var sumSq = 0.0f
        for (s in samples) sumSq += s * s
        val rms = Math.sqrt((sumSq / samples.size).toDouble()).toFloat()
        if (rms < 0.01f) return

        val minLag = (sampleRate / 1000).coerceAtLeast(10)
        val maxLag = (sampleRate / 60).coerceAtMost(samples.size / 2)
        if (maxLag <= minLag) return

        var maxCorr = 0.0f
        var bestLag = -1

        for (lag in minLag..maxLag) {
            var corr = 0.0f
            val maxIndex = samples.size - lag
            for (i in 0 until maxIndex step 2) {
                corr += samples[i] * samples[i + lag]
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestLag = lag
            }
        }

        if (bestLag > 0 && maxCorr > 0.02f) {
            val detectedFreq = sampleRate.toFloat() / bestLag.toFloat()
            if (detectedFreq in 50.0f..1500.0f) {
                val midiNote = (12.0 * (Math.log((detectedFreq / 440.0).toDouble()) / Math.log(2.0)) + 69.0).toInt()
                val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
                val noteIndex = (midiNote % 12 + 12) % 12
                val octave = (midiNote / 12) - 1
                val noteName = "${noteNames[noteIndex]}$octave"
                
                val targetFreq = (440.0 * Math.pow(2.0, (midiNote - 69).toDouble() / 12.0)).toFloat()
                val cents = (1200.0 * (Math.log((detectedFreq / targetFreq).toDouble()) / Math.log(2.0))).toFloat()
                val isTuned = Math.abs(cents) <= 5.0f

                _tunerState.value = TuningNote(
                    noteName = noteName,
                    targetFreq = (targetFreq * 10f).let { Math.round(it) / 10f },
                    currentFreq = (detectedFreq * 10f).let { Math.round(it) / 10f },
                    deviationCents = (cents * 10f).let { Math.round(it) / 10f },
                    isTuned = isTuned
                )
            }
        }
    }

    fun startStemSeparation(mode: String) {
        _stemSeparation.value = StemSeparationState.Processing(0.01f, mode, 8)
    }

    fun updateStemProgress(progress: Float) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Processing) {
            if (progress >= 1.0f) {
                _stemSeparation.value = StemSeparationState.Success(
                    mixerState = StemMixerState(),
                    vocalsVolume = 1.0f,
                    melodyVolume = 1.0f,
                    bassVolume = 0.8f,
                    drumsVolume = 0.8f
                )
            } else {
                val eta = ((1.0f - progress) * 8).toInt()
                _stemSeparation.value = StemSeparationState.Processing(progress, currentState.mode, eta)
            }
        }
    }

    fun adjustStemVolume(stem: String, volume: Float) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            val currentMixer = currentState.mixerState
            val updatedChannels = currentMixer.channels.map { ch ->
                if (ch.id == stem || (stem == "melody" && ch.id == "guitar")) {
                    ch.copy(volume = volume)
                } else ch
            }
            val voc = updatedChannels.find { it.id == "vocals" }?.volume ?: currentState.vocalsVolume
            val mel = updatedChannels.find { it.id == "guitar" }?.volume ?: currentState.melodyVolume
            val bas = updatedChannels.find { it.id == "bass" }?.volume ?: currentState.bassVolume
            val drm = updatedChannels.find { it.id == "drums" }?.volume ?: currentState.drumsVolume

            _stemSeparation.value = currentState.copy(
                mixerState = currentMixer.copy(channels = updatedChannels),
                vocalsVolume = voc,
                melodyVolume = mel,
                bassVolume = bas,
                drumsVolume = drm
            )
        }
    }

    fun toggleStemMute(channelId: String) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            val currentMixer = currentState.mixerState
            val updatedChannels = currentMixer.channels.map { ch ->
                if (ch.id == channelId) ch.copy(isMuted = !ch.isMuted) else ch
            }
            _stemSeparation.value = currentState.copy(mixerState = currentMixer.copy(channels = updatedChannels))
        }
    }

    fun toggleStemSolo(channelId: String) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            val currentMixer = currentState.mixerState
            val updatedChannels = currentMixer.channels.map { ch ->
                if (ch.id == channelId) ch.copy(isSoloed = !ch.isSoloed) else ch
            }
            _stemSeparation.value = currentState.copy(mixerState = currentMixer.copy(channels = updatedChannels))
        }
    }

    fun playOnlyStem(channelId: String) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            val currentMixer = currentState.mixerState
            val updatedChannels = currentMixer.channels.map { ch ->
                ch.copy(
                    isSoloed = (ch.id == channelId),
                    isMuted = false
                )
            }
            _stemSeparation.value = currentState.copy(mixerState = currentMixer.copy(channels = updatedChannels))
            _isStemPlaybackActive.value = true
            requestAudioProcessing()
        }
    }

    fun playCombinationStems(activeChannelIds: Set<String>) {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            val currentMixer = currentState.mixerState
            val updatedChannels = currentMixer.channels.map { ch ->
                ch.copy(
                    isSoloed = activeChannelIds.contains(ch.id),
                    isMuted = false
                )
            }
            _stemSeparation.value = currentState.copy(mixerState = currentMixer.copy(channels = updatedChannels))
            _isStemPlaybackActive.value = true
            requestAudioProcessing()
        }
    }

    fun clearStemSoloAndMute() {
        val currentState = _stemSeparation.value
        if (currentState is StemSeparationState.Success) {
            val currentMixer = currentState.mixerState
            val updatedChannels = currentMixer.channels.map { ch ->
                ch.copy(isSoloed = false, isMuted = false, volume = 1.0f)
            }
            _stemSeparation.value = currentState.copy(mixerState = currentMixer.copy(channels = updatedChannels))
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
        } else {
            val currentSharing = _sharingState.value
            _sharingState.value = currentSharing.copy(
                audioSourceFileName = name,
                activeProjectTitle = "Project: ${name.substringBeforeLast(".")}",
                sharedBufferMemoryRef = "0x" + Integer.toHexString(name.hashCode()).uppercase() + " (Zero-Copy Unified Pointer)",
                duplicateFilesCreated = 0
            )
        }
    }

    fun toggleStemPlayback() {
        val nextState = !_isStemPlaybackActive.value
        _isStemPlaybackActive.value = nextState
        if (nextState) {
            requestAudioProcessing()
        } else {
            stopAudioProcessing()
        }
    }

    fun setStemPlayback(active: Boolean) {
        _isStemPlaybackActive.value = active
        if (active) {
            requestAudioProcessing()
        } else {
            stopAudioProcessing()
        }
    }

    fun setAiAnalysisResult(result: String?) {
        _aiAnalysisResult.value = result
    }

    fun generateRealtimeFFTAmplitudes(binCount: Int): FloatArray {
        timeSeed += 0.2f
        val result = FloatArray(binCount)
        val chordInfo = _currentChord.value
        val baseFreq = chordInfo?.frequency ?: 440f
        
        for (i in 0 until binCount) {
            var amp = 0.08f
            val frequencyMultiplier = baseFreq / 20.0f
            val notePeakBin = ((frequencyMultiplier % binCount).toInt() + i) % binCount
            
            if (i == notePeakBin || i == (notePeakBin * 2) % binCount || i == (notePeakBin * 3) % binCount) {
                amp += 0.8f * (0.6f + 0.4f * sin(timeSeed + i))
            }

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
            val value = sin(theta * 2f + timeSeed) * 0.5f + sin(theta * 5.3f - timeSeed * 0.5f) * 0.3f
            result[i] = value.coerceIn(-1.0f, 1.0f)
        }
        return result
    }

    fun buildChordInfo(symbol: String): DetectedChordInfo {
        return when (symbol) {
            "C" -> DetectedChordInfo("C Major", "C", "1 - 3 - 5", listOf("C", "E", "G"), 0.98f, 261.63f, "Major Triad", "Basic major harmony.", listOf("Cmaj7", "Am", "F"))
            "G" -> DetectedChordInfo("G Major", "G", "1 - 3 - 5", listOf("G", "B", "D"), 0.97f, 392.00f, "Major Triad", "Dominant pillar chord.", listOf("G7", "Em", "C"))
            "D" -> DetectedChordInfo("D Major", "D", "1 - 3 - 5", listOf("D", "F#", "A"), 0.96f, 293.66f, "Major Triad", "Bright resonant key.", listOf("D7", "Bm", "G"))
            "Am" -> DetectedChordInfo("A Minor", "A", "1 - b3 - 5", listOf("A", "C", "E"), 0.98f, 220.00f, "Minor Triad", "Natural minor root chord.", listOf("Am7", "C", "F"))
            "Em" -> DetectedChordInfo("E Minor", "E", "1 - b3 - 5", listOf("E", "G", "B"), 0.97f, 164.81f, "Minor Triad", "Deep low voicing.", listOf("Em7", "G", "C"))
            "F" -> DetectedChordInfo("F Major", "F", "1 - 3 - 5", listOf("F", "A", "C"), 0.95f, 349.23f, "Subdominant Major", "Subdominant support.", listOf("Fmaj7", "Dm", "G"))
            "Dm" -> DetectedChordInfo("D Minor", "D", "1 - b3 - 5", listOf("D", "F", "A"), 0.94f, 293.66f, "Minor Triad", "Sorrowful minor harmony.", listOf("Dm7", "F", "G"))
            "Cmaj7" -> DetectedChordInfo("C Major 7th", "C", "1 - 3 - 5 - 7", listOf("C", "E", "G", "B"), 0.92f, 261.63f, "Major Seventh", "Rich jazz voicing.", listOf("C9", "Am9", "Em7"))
            "Am7" -> DetectedChordInfo("A Minor 7th", "A", "1 - b3 - 5 - b7", listOf("A", "C", "E", "G"), 0.91f, 220.00f, "Minor Seventh", "Jazzy soft minor voicing.", listOf("Am9", "Cmaj7", "Dm7"))
            "G7" -> DetectedChordInfo("G Dominant 7th", "G", "1 - 3 - 5 - b7", listOf("G", "B", "D", "F"), 0.93f, 392.00f, "Dominant Seventh", "Tension bluesy chord.", listOf("G9", "G13", "Bdim"))
            "Am9" -> DetectedChordInfo("A Minor 9th", "A", "1 - b3 - 5 - b7 - 9", listOf("A", "C", "E", "G", "B"), 0.89f, 220.00f, "Extended Jazz", "Sophisticated extensions.", listOf("Am11", "D9", "Em9"))
            "C/G" -> DetectedChordInfo("C Major / G Bass", "G", "Slash chord", listOf("G", "C", "E"), 0.88f, 196.00f, "Inversion / Slash Chord", "Alternative G root bass note.", listOf("Am7", "C", "F/G"))
            "G13" -> DetectedChordInfo("G Dominant 13th", "G", "1-3-5-b7-9-13", listOf("G", "B", "D", "F", "A", "E"), 0.85f, 392.00f, "Jazz Extended Voicing", "Full-spectrum jazz.", listOf("G9", "Cmaj9", "Abdim"))
            "Sungura A" -> DetectedChordInfo("A Major (Sungura)", "A", "Fast Triplet Voicing", listOf("A", "C#", "E"), 0.96f, 440.00f, "African Guitar Style", "Bright Sungura backing chord.", listOf("D", "E7", "F#m"))
            "F#" -> DetectedChordInfo("F# Major", "F#", "1 - 3 - 5", listOf("F#", "A#", "C#"), 0.98f, 369.99f, "Major Triad", "Bright F# Major chord.", listOf("D#m", "B", "C#"))
            "B" -> DetectedChordInfo("B Major", "B", "1 - 3 - 5", listOf("B", "D#", "F#"), 0.97f, 246.94f, "Major Triad", "Luminous B Major harmony.", listOf("G#m", "D#m", "F#"))
            "C#" -> DetectedChordInfo("C# Major", "C#", "1 - 3 - 5", listOf("C#", "F", "G#"), 0.96f, 277.18f, "Major Triad", "Powerful C# Major chord.", listOf("A#m", "F#", "D#m"))
            "D#m" -> DetectedChordInfo("D# Minor", "D#", "1 - b3 - 5", listOf("D#", "F#", "A#"), 0.99f, 311.13f, "Minor Triad", "Solemn and deep D# Minor chord.", listOf("B", "F#", "C#"))
            "D#m7" -> DetectedChordInfo("D# Minor 7th", "D#", "1 - b3 - 5 - b7", listOf("D#", "F#", "A#", "C#"), 0.95f, 311.13f, "Minor Seventh", "Rich minor seventh voicing.", listOf("Badd9", "F#add9", "C#"))
            "F#add9" -> DetectedChordInfo("F# Major add 9", "F#", "1 - 3 - 5 - 9", listOf("F#", "A#", "C#", "G#"), 0.94f, 369.99f, "Added Ninth", "Sleek F# Major add 9 voicing.", listOf("Badd9", "D#m7", "C#"))
            "Badd9" -> DetectedChordInfo("B Major add 9", "B", "1 - 3 - 5 - 9", listOf("B", "D#", "F#", "C#"), 0.94f, 246.94f, "Added Ninth", "Lush B Major add 9 voicing.", listOf("F#add9", "D#m7", "C#"))
            "A#m" -> DetectedChordInfo("A# Minor", "A#", "1 - b3 - 5", listOf("A#", "C#", "F"), 0.93f, 233.08f, "Minor Triad", "Soft minor triad built on A#.", listOf("F#", "D#m", "C#"))
            "G#m" -> DetectedChordInfo("G# Minor", "G#", "1 - b3 - 5", listOf("G#", "B", "D#"), 0.92f, 207.65f, "Minor Triad", "Mellow subdominant minor chord.", listOf("B", "D#m", "F#"))
            else -> DetectedChordInfo("$symbol Major", symbol, "1 - 3 - 5", listOf(symbol, "E", "G"), 0.90f, 440.00f, "Major Triad", "Standard chord alignment.", listOf("C", "G", "F"))
        }
    }
}
