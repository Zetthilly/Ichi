package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.StudioAudioSeparationTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.StemSeparatorViewModel

@Composable
fun StemSeparatorModuleScreen(
    stemViewModel: StemSeparatorViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "AI STEM SEPARATOR & MIXER",
                subtitle = "Module 03 • 4-Stem Multi-Track Studio",
                moduleIcon = Icons.Default.Tune,
                accentColor = Color(0xFF10B981),
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
                .testTag("stem_separator_workspace_screen")
        ) {
            StudioAudioSeparationTab(
                viewModel = sharedViewModel,
                onOpenSendToDialog = onOpenSendToDialog
            )
        }
    }
}
