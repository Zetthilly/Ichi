package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class UniversalSharingViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val universalAudioSharingState = sharedViewModel.engine.sharingState
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs

    fun toggleStageBypass(stageId: String) {
        sharedViewModel.engine.toggleSharingModuleBypass(stageId)
    }

    fun updateStageGain(stageId: String, gain: Float) {
        sharedViewModel.engine.setSharingModuleGain(stageId, gain)
    }

    fun toggleSharingPlayback() {
        sharedViewModel.engine.toggleSharingPlayback()
    }

    fun setSharingPlayhead(ms: Long) {
        sharedViewModel.setPlaybackPosition(ms)
    }
}
