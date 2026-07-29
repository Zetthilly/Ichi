package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class InstrumentVoicingsViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val currentChord = sharedViewModel.currentChord
    val liveNotesBuffer = sharedViewModel.liveNotesBuffer
    val detectedArpeggio = sharedViewModel.detectedArpeggio
    val africanStyleLick = sharedViewModel.africanStyleLick
    val globalKeySignature = sharedViewModel.globalKeySignature
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs

    fun selectChord(chordSymbol: String) {
        sharedViewModel.engine.selectChord(chordSymbol)
    }

    fun tapNote(noteName: String) {
        sharedViewModel.engine.tapLiveMusicalNote(noteName)
    }

    fun clearNotes() {
        sharedViewModel.engine.clearLiveNotes()
    }

    fun generateAfricanLick() {
        sharedViewModel.engine.tapLiveMusicalNote("A C E G")
    }
}
