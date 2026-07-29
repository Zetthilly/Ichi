package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.StudioAudioSeparationTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.StudioDspViewModel

@Composable
fun StudioDspModuleScreen(
    dspViewModel: StudioDspViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "STUDIO DSP & RESTORATION",
                subtitle = "Module 04 • Spectral Denoise & Clean FX",
                moduleIcon = Icons.Default.Equalizer,
                accentColor = Color(0xFFF59E0B),
                viewModel = sharedViewModel,
                onOpenDrawer = onOpenDrawer,
                onOpenSendToDialog = onOpenSendToDialog
            )
        },
        containerColor = Color(0xFF030814)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("studio_dsp_workspace_screen")
        ) {
            StudioAudioSeparationTab(
                viewModel = sharedViewModel,
                onOpenSendToDialog = onOpenSendToDialog
            )
        }
    }
}
