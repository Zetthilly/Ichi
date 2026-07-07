package com.example.data

object MusicTheoryUtils {

    private val CHROMATIC_SHARPS = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val CHROMATIC_FLATS = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

    /**
     * Finds the index of a note in the chromatic scale.
     */
    private fun getNoteIndex(note: String): Int {
        val normalized = note.trim().replaceFirstChar { it.uppercase() }
        val sharpIndex = CHROMATIC_SHARPS.indexOf(normalized)
        if (sharpIndex != -1) return sharpIndex
        
        val flatIndex = CHROMATIC_FLATS.indexOf(normalized)
        if (flatIndex != -1) return flatIndex
        
        // Try fallback matches (case insensitive or trimming pitch info)
        val cleaned = normalized.takeWhile { it.isLetter() || it == '#' || it == 'b' }
        val fallbackSharp = CHROMATIC_SHARPS.indexOf(cleaned)
        if (fallbackSharp != -1) return fallbackSharp
        
        val fallbackFlat = CHROMATIC_FLATS.indexOf(cleaned)
        if (fallbackFlat != -1) return fallbackFlat
        
        return 0 // Default fallback to C
    }

    /**
     * Splits a chord name into its root note and its symbol/suffix (e.g. "Cmaj7" -> "C" to "maj7")
     */
    fun splitChord(chordName: String): Pair<String, String> {
        val trimmed = chordName.trim()
        if (trimmed.isEmpty()) return Pair("", "")
        
        if (trimmed.length >= 2) {
            val firstTwo = trimmed.substring(0, 2)
            if (firstTwo[1] == '#' || firstTwo[1] == 'b') {
                return Pair(firstTwo, trimmed.substring(2))
            }
        }
        return Pair(trimmed.substring(0, 1), trimmed.substring(1))
    }

    /**
     * Transposes a single note by a given number of semitones.
     */
    fun transposeNote(note: String, semitones: Int): String {
        val index = getNoteIndex(note)
        var newIndex = (index + semitones) % 12
        if (newIndex < 0) newIndex += 12
        
        // Choose flat/sharp based on input signature if possible
        return if (note.contains("b")) {
            CHROMATIC_FLATS[newIndex]
        } else {
            CHROMATIC_SHARPS[newIndex]
        }
    }

    /**
     * Transposes a full chord symbol by a given number of semitones (e.g. "Cmaj7" + 2 -> "Dmaj7")
     */
    fun transposeChord(chordName: String, semitones: Int): String {
        val (root, extension) = splitChord(chordName)
        if (root.isEmpty()) return chordName
        val newRoot = transposeNote(root, semitones)
        return newRoot + extension
    }

    /**
     * Transposes an array of chord progression elements.
     */
    fun transposeProgression(progression: List<String>, semitones: Int): List<String> {
        return progression.map { transposeChord(it, semitones) }
    }

    /**
     * Converts a chord name into its corresponding Roman numeral functional progression step
     * under a specific key signature (e.g. "C" in "C Major" -> "I", "Am" in "C Major" -> "vi").
     */
    fun getRomanNumeral(chordName: String, keySignature: String): String {
        val (chordRoot, suffix) = splitChord(chordName)
        if (chordRoot.isEmpty()) return ""

        val keyPart = keySignature.replace("Key of", "").trim()
        val isMinorKey = keyPart.contains("Minor", ignoreCase = true) || keyPart.endsWith("m")
        val scaleRoot = keyPart.split(" ")[0]

        val rootOffset = (getNoteIndex(chordRoot) - getNoteIndex(scaleRoot) + 12) % 12

        // Determine if chord itself is major or minor
        val isChordMinor = (suffix.startsWith("m") && !suffix.startsWith("maj")) || 
                           suffix.contains("min") || 
                           suffix.contains("dim") || 
                           suffix.contains("°")

        val baseNumeral = if (isMinorKey) {
            // Roman Numeral table relative to Natural Minor Key
            when (rootOffset) {
                0 -> if (isChordMinor) "i" else "I"
                1 -> if (isChordMinor) "bii" else "bII"
                2 -> if (isChordMinor) "ii°" else "II"
                3 -> if (isChordMinor) "biii" else "bIII"
                4 -> if (isChordMinor) "iv" else "IV"
                5 -> if (isChordMinor) "iv" else "IV"
                6 -> "bV"
                7 -> if (isChordMinor) "v" else "V"
                8 -> if (isChordMinor) "bvi" else "bVI"
                9 -> if (isChordMinor) "vi°" else "VI"
                10 -> if (isChordMinor) "bvii" else "bVII"
                11 -> if (isChordMinor) "vii°" else "VII"
                else -> "i"
            }
        } else {
            // Roman Numeral table relative to Major Key
            when (rootOffset) {
                0 -> if (isChordMinor) "i" else "I"
                1 -> if (isChordMinor) "bii" else "bII"
                2 -> if (isChordMinor) "ii" else "II"
                3 -> if (isChordMinor) "biii" else "bIII"
                4 -> if (isChordMinor) "iii" else "III"
                5 -> if (isChordMinor) "iv" else "IV"
                6 -> "bV"
                7 -> if (isChordMinor) "v" else "V"
                8 -> if (isChordMinor) "bvi" else "bVI"
                9 -> if (isChordMinor) "vi" else "VI"
                10 -> if (isChordMinor) "bvii" else "bVII"
                11 -> if (isChordMinor) "vii°" else "VII"
                else -> "I"
            }
        }
        
        // Add extension for clarity but strip standard minor prefix characters already encoded
        val extensionLabel = if (suffix.startsWith("m") && !suffix.startsWith("maj")) {
            suffix.substring(1)
        } else {
            suffix
        }
        
        return baseNumeral + extensionLabel
    }
}
