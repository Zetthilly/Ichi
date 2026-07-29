package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class StudioDspViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val noiseReductionEnabled = sharedViewModel.noiseReductionEnabled
    val humRemovalEnabled = sharedViewModel.humRemovalEnabled
    val clippingRepairEnabled = sharedViewModel.clippingRepairEnabled
    val vocalEnhancementEnabled = sharedViewModel.vocalEnhancementEnabled
    val exportLog = sharedViewModel.exportLog
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs

    fun toggleNoiseReduction() {
        sharedViewModel.engine.toggleNoiseReduction()
    }

    fun toggleHumRemoval() {
        sharedViewModel.engine.toggleHumRemoval()
    }

    fun toggleClippingRepair() {
        sharedViewModel.engine.toggleClippingRepair()
    }

    fun toggleVocalEnhancement() {
        sharedViewModel.engine.toggleVocalEnhancement()
    }

    fun exportProcessedAudio(format: String) {
        sharedViewModel.triggerExport(format, null)
    }
}
