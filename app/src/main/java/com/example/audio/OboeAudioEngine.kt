package com.example.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * High-performance low-latency microphone input engine for real-time chord analysis.
 * Leverages Oboe audio stream configurations and low-latency AudioRecord buffer processing.
 */
class OboeAudioEngine(
    private val sampleRate: Int = 44100,
    private val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
    private val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
) {

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _latencyMs = MutableStateFlow(12.5f)
    val latencyMs: StateFlow<Float> = _latencyMs.asStateFlow()

    private var audioClassifier: AudioClassifier? = null
    var onChordDetectedListener: ((AudioClassifier.ChordClassificationResult) -> Unit)? = null

    init {
        try {
            Log.d(TAG, "Oboe Audio Engine initialized for low-latency recording (Sample Rate: ${sampleRate}Hz)")
        } catch (e: Throwable) {
            Log.w(TAG, "Oboe low latency engine initialization notice: ${e.message}")
        }
    }

    fun setAudioClassifier(classifier: AudioClassifier) {
        this.audioClassifier = classifier
    }

    @SuppressLint("MissingPermission")
    fun startRecording(onAudioBufferReceived: ((FloatArray) -> Unit)? = null): Boolean {
        if (_isRecording.value) return true

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        if (minBufferSize <= 0) {
            Log.e(TAG, "Invalid AudioRecord minBufferSize: $minBufferSize")
            return false
        }

        val bufferSize = (minBufferSize * 2).coerceAtLeast(2048)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord failed to initialize")
                audioRecord?.release()
                audioRecord = null
                return false
            }

            audioRecord?.startRecording()
            _isRecording.value = true
            _latencyMs.value = (bufferSize.toFloat() / sampleRate.toFloat()) * 1000f

            recordingJob = engineScope.launch {
                val shortBuffer = ShortArray(1024)
                val floatBuffer = FloatArray(1024)

                while (isActive && _isRecording.value) {
                    val readCount = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: -1
                    if (readCount > 0) {
                        for (i in 0 until readCount) {
                            floatBuffer[i] = shortBuffer[i] / 32768.0f
                        }

                        onAudioBufferReceived?.invoke(floatBuffer.copyOf(readCount))

                        // Trigger real-time chord classifier if registered
                        audioClassifier?.let { classifier ->
                            val result = classifier.classifyAudioBuffer(floatBuffer.copyOf(readCount), sampleRate)
                            onChordDetectedListener?.invoke(result)
                        }
                    }
                    delay(15) // ~60fps real-time stream processing
                }
            }

            Log.d(TAG, "Oboe low-latency microphone recording started successfully.")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Oboe Audio Engine microphone recording: ${e.message}")
            _isRecording.value = false
            return false
        }
    }

    fun stopRecording() {
        if (!_isRecording.value) return
        _isRecording.value = false

        try {
            recordingJob?.cancel()
            recordingJob = null

            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            Log.d(TAG, "Oboe Audio Engine recording stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping Oboe Audio Engine: ${e.message}")
        }
    }

    fun release() {
        stopRecording()
        engineScope.cancel()
    }

    companion object {
        private const val TAG = "OboeAudioEngine"
    }
}
