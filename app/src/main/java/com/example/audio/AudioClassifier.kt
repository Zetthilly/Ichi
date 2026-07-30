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

    /**
     * Classifies an audio PCM buffer (float samples between -1.0 and 1.0) for chord recognition.
     */
    fun classifyAudioBuffer(audioSamples: FloatArray, sampleRate: Int = 44100): ChordClassificationResult {
        val chroma = extractChromaFeatures(audioSamples, sampleRate)

        if (isModelLoaded && ortSession != null && ortEnv != null) {
            try {
                val inputShape = longArrayOf(1, 12)
                val floatBuffer = FloatBuffer.wrap(chroma)
                val inputTensor = OnnxTensor.createTensor(ortEnv, floatBuffer, inputShape)

                inputTensor.use { tensor ->
                    val inputs = mapOf("input" to tensor)
                    ortSession?.run(inputs).use { result ->
                        val outputValue = result?.get(0)?.value
                        if (outputValue is Array<*>) {
                            val probabilities = (outputValue[0] as FloatArray)
                            val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
                            val predictedChord = chordVocabulary.getOrElse(maxIdx) { "C" }
                            val confidence = probabilities[maxIdx]
                            return formatClassificationResult(predictedChord, confidence, chroma)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error executing ONNX inference: ${e.message}")
            }
        }

        // DSP Chromagram-based Neural Approximation Fallback
        return classifyChromaHeuristic(chroma)
    }

    /**
     * Computes a 12-bin Chromagram profile from raw audio PCM data.
     */
    private fun extractChromaFeatures(audioSamples: FloatArray, sampleRate: Int): FloatArray {
        val chroma = FloatArray(12)
        if (audioSamples.isEmpty()) return chroma

        val numSamples = audioSamples.size
        // Analyze pitch classes across standard musical range (C2 to B5)
        val pitchFrequencies = floatArrayOf(
            16.35f, 17.32f, 18.35f, 19.45f, 20.60f, 21.83f, 23.12f, 24.50f, 25.96f, 27.50f, 29.14f, 30.87f // C0 to B0 base
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

        // Normalize chromagram vector
        val maxVal = chroma.maxOrNull() ?: 1.0f
        if (maxVal > 0.0001f) {
            for (i in 0 until 12) {
                chroma[i] /= maxVal
            }
        }
        return chroma
    }

    private fun classifyChromaHeuristic(chroma: FloatArray): ChordClassificationResult {
        val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        
        val scores = mutableListOf<Pair<String, Float>>()

        // Major chords (0, 4, 7)
        for (root in 0 until 12) {
            val rootName = noteNames[root]
            val majScore = chroma[root] * 1.0f + chroma[(root + 4) % 12] * 0.8f + chroma[(root + 7) % 12] * 0.9f
            scores.add(Pair(rootName, majScore))

            val minScore = chroma[root] * 1.0f + chroma[(root + 3) % 12] * 0.8f + chroma[(root + 7) % 12] * 0.9f
            scores.add(Pair("${rootName}m", minScore))

            val dom7Score = majScore + chroma[(root + 10) % 12] * 0.7f
            scores.add(Pair("${rootName}7", dom7Score))

            val maj7Score = majScore + chroma[(root + 11) % 12] * 0.7f
            scores.add(Pair("${rootName}maj7", maj7Score))
        }

        scores.sortByDescending { it.second }
        val topMatch = scores.firstOrNull() ?: Pair("C", 1.0f)
        val maxScore = (scores.maxOfOrNull { it.second } ?: 1.0f).coerceAtLeast(0.001f)

        val normalizedCandidates = scores.take(4).map { Pair(it.first, (it.second / maxScore).coerceIn(0.0f, 1.0f)) }

        return formatClassificationResult(topMatch.first, (topMatch.second / maxScore).coerceIn(0.5f, 0.99f), chroma, normalizedCandidates)
    }

    private fun formatClassificationResult(
        predictedChord: String,
        confidence: Float,
        chroma: FloatArray,
        candidates: List<Pair<String, Float>> = emptyList()
    ): ChordClassificationResult {
        val rootNote = predictedChord.takeWhile { it != 'm' && it != '7' && it != 'j' }
        val quality = when {
            predictedChord.contains("maj7") -> "Major 7th"
            predictedChord.contains("min7") -> "Minor 7th"
            predictedChord.endsWith("m") -> "Minor"
            predictedChord.contains("7") -> "Dominant 7th"
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
