package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class StemSeparatorViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val stemSeparationState = sharedViewModel.stemSeparation
    val uploadedFileName = sharedViewModel.uploadedFileName
    val uploadedFileSize = sharedViewModel.uploadedFileSize
    val aiAnalysisResult = sharedViewModel.aiAnalysisResult
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs
    val bpm = sharedViewModel.bpm
    val speedMultiplier = sharedViewModel.tempoPreservedMultiplier

    fun processStemSeparation(mode: String) {
        sharedViewModel.runStemSeparation(mode)
    }

    fun updateStemChannelVolume(channelId: String, volume: Float) {
        sharedViewModel.engine.adjustStemVolume(channelId, volume)
    }

    fun toggleStemChannelMute(channelId: String) {
        sharedViewModel.engine.toggleStemMute(channelId)
    }

    fun toggleStemChannelSolo(channelId: String) {
        sharedViewModel.engine.toggleStemSolo(channelId)
    }

    fun setSpeedMultiplier(multiplier: Float) {
        sharedViewModel.setSpeedMultiplier(multiplier)
    }

    fun adjustBpm(delta: Int) {
        sharedViewModel.engine.adjustBpm(delta)
    }

    fun togglePlayback() {
        sharedViewModel.toggleMasterPlayback()
    }
}
