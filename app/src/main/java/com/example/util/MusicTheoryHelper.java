package com.example.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicTheoryHelper {

    // Tagline constants
    public static final String TAGLINE = "Hear the Notes. Understand the Music. Powered by AI.";
    public static final String DESIGNER = "Designed and Built by Joseph Hilary Zulukwa";

    /**
     * Gets scale notes for common music keys signature calculations.
     * Offline local music theory assistant logic.
     */
    public static List<String> getScaleNotes(String keySignature) {
        List<String> scaleNotes = new ArrayList<>();
        if (keySignature == null || keySignature.isEmpty()) {
            keySignature = "C Major";
        }

        switch (keySignature) {
            case "C Major":
            case "A Minor":
                Collections.addAll(scaleNotes, "C", "D", "E", "F", "G", "A", "B");
                break;
            case "G Major":
            case "E Minor":
                Collections.addAll(scaleNotes, "G", "A", "B", "C", "D", "E", "F#");
                break;
            case "D Major":
            case "B Minor":
                Collections.addAll(scaleNotes, "D", "E", "F#", "G", "A", "B", "C#");
                break;
            case "A Major":
            case "F# Minor":
                Collections.addAll(scaleNotes, "A", "B", "C#", "D", "E", "F#", "G#");
                break;
            case "F Major":
            case "D Minor":
                Collections.addAll(scaleNotes, "F", "G", "A", "Bb", "C", "D", "E");
                break;
            default:
                Collections.addAll(scaleNotes, "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "Bb", "B");
                break;
        }
        return scaleNotes;
    }

    /**
     * Returns description of key signature roots.
     */
    public static String getKeySignatureDescription(String keySignature) {
        if (keySignature == null || keySignature.isEmpty()) {
            return "Standard natural scale alignment.";
        }
        if (keySignature.contains("Major")) {
            return "Bright tone signature, perfect for melodic Sungura leads and Uplifting lead patterns.";
        } else if (keySignature.contains("Minor")) {
            return "Mellow harmonic scale, perfect for Afro-jazz progressions and emotional call-and-response solos.";
        }
        return "Special chromatic interval setup.";
    }
}
