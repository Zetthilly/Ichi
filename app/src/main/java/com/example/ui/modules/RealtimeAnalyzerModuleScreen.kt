package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.RealtimeAnalyzerTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.RealtimeAnalyzerViewModel

@Composable
fun RealtimeAnalyzerModuleScreen(
    analyzerViewModel: RealtimeAnalyzerViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "REALTIME CHORD & KEY ANALYZER",
                subtitle = "Module 01 • Harmonic Frequency Engine",
                moduleIcon = Icons.Default.GraphicEq,
                accentColor = Color(0xFF00E5FF),
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
                .testTag("realtime_analyzer_workspace_screen")
        ) {
            RealtimeAnalyzerTab(
                viewModel = sharedViewModel,
                onOpenSendToDialog = onOpenSendToDialog
            )
        }
    }
}
