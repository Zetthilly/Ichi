package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.RealtimeAnalyzerTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.InstrumentVoicingsViewModel

@Composable
fun InstrumentVoicingsModuleScreen(
    voicingsViewModel: InstrumentVoicingsViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "INTERACTIVE INSTRUMENT VOICINGS",
                subtitle = "Module 02 • Piano, Guitar & Arpeggios",
                moduleIcon = Icons.Default.Piano,
                accentColor = Color(0xFFD4AF37),
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
                .testTag("instrument_voicings_workspace_screen")
        ) {
            RealtimeAnalyzerTab(
                viewModel = sharedViewModel,
                onOpenSendToDialog = onOpenSendToDialog
            )
        }
    }
}
