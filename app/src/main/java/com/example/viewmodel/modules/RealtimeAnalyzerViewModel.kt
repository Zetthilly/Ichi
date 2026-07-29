package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class RealtimeAnalyzerViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val currentChord = sharedViewModel.currentChord
    val chordTimeline = sharedViewModel.chordTimeline
    val globalKeySignature = sharedViewModel.globalKeySignature
    val isAnalyzingKey = sharedViewModel.isAnalyzingKey
    val keyAnalysisProgress = sharedViewModel.keyAnalysisProgress
    val liveNotesBuffer = sharedViewModel.liveNotesBuffer
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs
    val detectionMode = sharedViewModel.detectionMode

    fun setDetectionMode(mode: String) {
        sharedViewModel.engine.setDetectionMode(mode)
    }

    fun selectChord(chordSymbol: String) {
        sharedViewModel.engine.selectChord(chordSymbol)
    }

    fun tapNote(note: String) {
        sharedViewModel.engine.tapLiveMusicalNote(note)
    }

    fun togglePlayback() {
        sharedViewModel.toggleMasterPlayback()
    }
}
