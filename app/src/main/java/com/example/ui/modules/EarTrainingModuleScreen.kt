package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.DashboardTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.EarTrainingViewModel

@Composable
fun EarTrainingModuleScreen(
    earTrainingViewModel: EarTrainingViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "EAR TRAINING & KARAOKE LYRICS",
                subtitle = "Module 06 • Interval Quizzes & Synced Chords",
                moduleIcon = Icons.Default.Psychology,
                accentColor = Color(0xFFA855F7),
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
                .testTag("ear_training_workspace_screen")
        ) {
            DashboardTab(
                viewModel = sharedViewModel,
                onOpenRecorder = {},
                onOpenImport = {},
                onOpenSendToDialog = onOpenSendToDialog
            )
        }
    }
}
