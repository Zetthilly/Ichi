package com.example.audio

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.util.Log
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Helper class to load pre-trained TFLite or ONNX models for neural chord recognition.
 * Performs audio pre-processing (chromagram/spectrogram extraction) and runs ONNX inference.
 */
class AudioClassifier(private val context: Context? = null) {

    private var ortEnv: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    private var isModelLoaded = false

    data class ChordClassificationResult(
        val chordName: String,
        val confidence: Float,
        val rootNote: String,
        val chordQuality: String,
        val chromaProfile: FloatArray,
        val alternativeCandidates: List<Pair<String, Float>>
    )

    private val chordVocabulary = listOf(
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
        "Cm", "C#m", "Dm", "D#m", "Em", "Fm", "F#m", "Gm", "G#m", "Am", "A#m", "Bm",
        "C7", "D7", "E7", "F7", "G7", "A7", "B7",
        "Cmaj7", "Dmaj7", "Emaj7", "Fmaj7", "Gmaj7", "Amaj7", "Bmaj7",
        "Cmin7", "Dmin7", "Emin7", "Fmin7", "Gmin7", "Amin7", "Bmin7"
    )

    init {
        try {
            ortEnv = OrtEnvironment.getEnvironment()
            Log.d(TAG, "ONNX Runtime Environment initialized successfully.")
        } catch (e: Throwable) {
            Log.w(TAG, "ONNX Runtime initialization fallback notice: ${e.message}")
        }
    }

    /**
     * Loads a pre-trained ONNX or TFLite model from assets.
     */
    fun loadModel(modelAssetPath: String = "models/chord_recognition_model.onnx"): Boolean {
        if (context == null || ortEnv == null) return false
        return try {
            val modelBytes = context.assets.open(modelAssetPath).readBytes()
            ortSession = ortEnv?.createSession(modelBytes)
            isModelLoaded = true
            Log.d(TAG, "ONNX Chord Recognition model loaded from $modelAssetPath")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Could not load model asset '$modelAssetPath'. Using DSP Neural Chroma Fallback Engine: ${e.message}")
            isModelLoaded = false
            false
        }
    }

    private val chordHistoryBuffer = java.util.Collections.synchronizedList(mutableListOf<ChordClassificationResult>())

    /**
     * Classifies an audio PCM buffer (float samples between -1.0 and 1.0) for chord recognition.
     */
    fun classifyAudioBuffer(audioSamples: FloatArray, sampleRate: Int = 44100): ChordClassificationResult {
        if (audioSamples.isEmpty()) return formatClassificationResult("C", 0.5f, FloatArray(12))

        // Apply Hann window to input audio buffer to prevent spectral leakage
        val windowed = FloatArray(audioSamples.size)
        val n = audioSamples.size
        for (i in 0 until n) {
            val hann = 0.5f * (1.0f - cos(2.0 * Math.PI * i / (n - 1)).toFloat())
            windowed[i] = audioSamples[i] * hann
        }

        val chroma = extractChromaFeatures(windowed, sampleRate)
        val bassNoteIndex = detectBassPitchIndex(windowed, sampleRate)

        val rawResult = if (isModelLoaded && ortSession != null && ortEnv != null) {
            try {
                val inputShape = longArrayOf(1, 12)
                val floatBuffer = FloatBuffer.wrap(chroma)
                val inputTensor = OnnxTensor.createTensor(ortEnv, floatBuffer, inputShape)

                var modelRes: ChordClassificationResult? = null
                inputTensor.use { tensor ->
                    val inputs = mapOf("input" to tensor)
                    ortSession?.run(inputs).use { result ->
                        val outputValue = result?.get(0)?.value
                        if (outputValue is Array<*>) {
                            val probabilities = (outputValue[0] as FloatArray)
                            val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
                            val predictedChord = chordVocabulary.getOrElse(maxIdx) { "C" }
                            val confidence = probabilities[maxIdx]
                            modelRes = formatClassificationResult(predictedChord, confidence, chroma, bassNoteIndex = bassNoteIndex)
                        }
                    }
                }
                modelRes ?: classifyChromaCosineSimilarity(chroma, bassNoteIndex)
            } catch (e: Exception) {
                Log.e(TAG, "Error executing ONNX inference: ${e.message}")
                classifyChromaCosineSimilarity(chroma, bassNoteIndex)
            }
        } else {
            classifyChromaCosineSimilarity(chroma, bassNoteIndex)
        }

        // Apply temporal smoothing across last 3 frames to avoid flickering frame-to-frame
        synchronized(chordHistoryBuffer) {
            chordHistoryBuffer.add(rawResult)
            if (chordHistoryBuffer.size > 3) {
                chordHistoryBuffer.removeAt(0)
            }
            val dominantName = chordHistoryBuffer.groupBy { it.chordName }
                .maxByOrNull { it.value.size }?.key ?: rawResult.chordName
            return chordHistoryBuffer.find { it.chordName == dominantName } ?: rawResult
        }
    }

    /**
     * Autocorrelation on lowpass-filtered PCM to detect true fundamental bass note (40Hz to 250Hz).
     */
    private fun detectBassPitchIndex(audioSamples: FloatArray, sampleRate: Int): Int {
        val numSamples = audioSamples.size
        if (numSamples < 512) return -1

        val minLag = (sampleRate / 250.0).toInt().coerceAtLeast(1)
        val maxLag = (sampleRate / 40.0).toInt().coerceAtMost(numSamples - 1)
        if (maxLag <= minLag) return -1

        var maxCorr = 0.0f
        var bestLag = -1

        for (lag in minLag..maxLag) {
            var corr = 0.0f
            val maxI = numSamples - lag
            val step = (maxI / 256).coerceAtLeast(1)
            for (i in 0 until maxI step step) {
                corr += audioSamples[i] * audioSamples[i + lag]
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestLag = lag
            }
        }

        if (bestLag <= 0 || maxCorr < 0.05f) return -1

        val fundFreq = sampleRate.toFloat() / bestLag
        val midiPitch = Math.round(12.0 * Math.log(fundFreq / 440.0) / Math.log(2.0) + 69).toInt()
        val pitchIndex = ((midiPitch % 12) + 12) % 12
        return pitchIndex
    }

    /**
     * Computes a 12-bin Chromagram profile from windowed audio PCM data.
     */
    private fun extractChromaFeatures(audioSamples: FloatArray, sampleRate: Int): FloatArray {
        val chroma = FloatArray(12)
        if (audioSamples.isEmpty()) return chroma

        val numSamples = audioSamples.size
        val pitchFrequencies = floatArrayOf(
            16.35f, 17.32f, 18.35f, 19.45f, 20.60f, 21.83f, 23.12f, 24.50f, 25.96f, 27.50f, 29.14f, 30.87f
        )

        for (pitchIdx in 0 until 12) {
            var energy = 0f
            for (octave in 2..5) {
                val freq = pitchFrequencies[pitchIdx] * (1 shl octave)
                val k = (freq * numSamples / sampleRate).toInt()
                if (k in 1 until numSamples / 2) {
                    var real = 0f
                    var imag = 0f
                    val step = (numSamples / 256).coerceAtLeast(1)
                    for (i in 0 until numSamples step step) {
                        val angle = 2.0 * Math.PI * k * i / numSamples
                        real += (audioSamples[i] * cos(angle)).toFloat()
                        imag -= (audioSamples[i] * sin(angle)).toFloat()
                    }
                    energy += sqrt(real * real + imag * imag)
                }
            }
            chroma[pitchIdx] = energy
        }

        val maxVal = chroma.maxOrNull() ?: 1.0f
        if (maxVal > 0.0001f) {
            for (i in 0 until 12) {
                chroma[i] /= maxVal
            }
        }
        return chroma
    }

    /**
     * Cosine similarity matching of 12-bin chroma vector against weighted chord templates.
     */
    private fun classifyChromaCosineSimilarity(chroma: FloatArray, bassNoteIndex: Int): ChordClassificationResult {
        val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        // Weighted template definitions (root relative intervals -> weights)
        val templates = listOf(
            "" to floatArrayOf(1.0f, 0f, 0f, 0f, 0.8f, 0f, 0f, 0.9f, 0f, 0f, 0f, 0f),       // Major
            "m" to floatArrayOf(1.0f, 0f, 0f, 0.8f, 0f, 0f, 0f, 0.9f, 0f, 0f, 0f, 0f),      // Minor
            "dim" to floatArrayOf(1.0f, 0f, 0f, 0.8f, 0f, 0f, 0.8f, 0f, 0f, 0f, 0f, 0f),    // Diminished
            "aug" to floatArrayOf(1.0f, 0f, 0f, 0f, 0.8f, 0f, 0f, 0f, 0.8f, 0f, 0f, 0f),    // Augmented
            "sus2" to floatArrayOf(1.0f, 0f, 0.8f, 0f, 0f, 0f, 0f, 0.9f, 0f, 0f, 0f, 0f),   // Sus2
            "sus4" to floatArrayOf(1.0f, 0f, 0f, 0f, 0f, 0.8f, 0f, 0.9f, 0f, 0f, 0f, 0f),   // Sus4
            "7" to floatArrayOf(1.0f, 0f, 0f, 0f, 0.8f, 0f, 0f, 0.9f, 0f, 0.7f, 0f, 0f),    // Dom 7th
            "maj7" to floatArrayOf(1.0f, 0f, 0f, 0f, 0.8f, 0f, 0f, 0.9f, 0f, 0f, 0f, 0.7f), // Maj 7th
            "m7" to floatArrayOf(1.0f, 0f, 0f, 0.8f, 0f, 0f, 0f, 0.9f, 0f, 0.7f, 0f, 0f),   // Min 7th
            "add9" to floatArrayOf(1.0f, 0f, 0.7f, 0f, 0.8f, 0f, 0f, 0.9f, 0f, 0f, 0f, 0f)  // Add 9
        )

        val scores = mutableListOf<Triple<String, String, Float>>()

        for (root in 0 until 12) {
            val rootName = noteNames[root]
            for ((suffix, templatePattern) in templates) {
                // Rotate template pattern to root
                val shiftedTemplate = FloatArray(12)
                for (i in 0 until 12) {
                    shiftedTemplate[(i + root) % 12] = templatePattern[i]
                }
                // Compute Cosine Similarity
                val sim = cosineSimilarity(chroma, shiftedTemplate)
                val fullChordName = "$rootName$suffix"
                scores.add(Triple(fullChordName, rootName, sim))
            }
        }

        scores.sortByDescending { it.third }
        val topMatch = scores.firstOrNull() ?: Triple("C", "C", 1.0f)
        val secondMatch = scores.getOrNull(1) ?: Triple("C", "C", 0.5f)

        // Confidence calibration using margin between top-1 and top-2
        val margin = (topMatch.third - secondMatch.third).coerceIn(0.0f, 1.0f)
        val calibratedConfidence = (0.50f + margin * 0.49f).coerceIn(0.50f, 0.99f)

        var finalChordName = topMatch.first
        // Bass slash chord detection
        if (bassNoteIndex in 0..11) {
            val bassName = noteNames[bassNoteIndex]
            if (bassName != topMatch.second && !finalChordName.contains("/")) {
                finalChordName = "$finalChordName/$bassName"
            }
        }

        val topCandidates = scores.take(4).map { Pair(it.first, it.third.coerceIn(0.0f, 1.0f)) }

        return formatClassificationResult(
            predictedChord = finalChordName,
            confidence = calibratedConfidence,
            chroma = chroma,
            candidates = topCandidates,
            bassNoteIndex = bassNoteIndex
        )
    }

    private fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        var dot = 0f
        var norm1 = 0f
        var norm2 = 0f
        for (i in v1.indices) {
            dot += v1[i] * v2[i]
            norm1 += v1[i] * v1[i]
            norm2 += v2[i] * v2[i]
        }
        if (norm1 < 0.00001f || norm2 < 0.00001f) return 0f
        return (dot / (sqrt(norm1) * sqrt(norm2))).toFloat()
    }

    private fun formatClassificationResult(
        predictedChord: String,
        confidence: Float,
        chroma: FloatArray,
        candidates: List<Pair<String, Float>> = emptyList(),
        bassNoteIndex: Int = -1
    ): ChordClassificationResult {
        val baseName = predictedChord.substringBefore("/")
        val rootNote = baseName.takeWhile { it != 'm' && it != '7' && it != 'j' && it != 'd' && it != 'a' && it != 's' }
        val quality = when {
            baseName.contains("maj7") -> "Major 7th"
            baseName.contains("m7") -> "Minor 7th"
            baseName.contains("add9") -> "Added Ninth"
            baseName.contains("dim") -> "Diminished"
            baseName.contains("aug") -> "Augmented"
            baseName.contains("sus2") -> "Suspended 2nd"
            baseName.contains("sus4") -> "Suspended 4th"
            baseName.endsWith("m") -> "Minor"
            baseName.contains("7") -> "Dominant 7th"
            else -> "Major"
        }

        return ChordClassificationResult(
            chordName = predictedChord,
            confidence = confidence,
            rootNote = if (rootNote.isEmpty()) "C" else rootNote,
            chordQuality = quality,
            chromaProfile = chroma,
            alternativeCandidates = candidates
        )
    }

    fun close() {
        try {
            ortSession?.close()
            ortEnv?.close()
            ortSession = null
            ortEnv = null
            isModelLoaded = false
        } catch (e: Exception) {
            Log.e(TAG, "Error closing ONNX Session: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "AudioClassifier"
    }
}
