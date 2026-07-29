package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class ModuleLauncherViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val currentSection = sharedViewModel.currentSection
    val currentChord = sharedViewModel.currentChord
    val globalKeySignature = sharedViewModel.globalKeySignature
    val uploadedFileName = sharedViewModel.uploadedFileName
    val uploadedFileSize = sharedViewModel.uploadedFileSize
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs
    val universalAudioSharingState = sharedViewModel.universalAudioSharingState
    val bpm = sharedViewModel.bpm

    fun navigateToModule(moduleRoute: String) {
        sharedViewModel.setSection(moduleRoute)
    }

    fun togglePlayback() {
        sharedViewModel.toggleMasterPlayback()
    }
}
