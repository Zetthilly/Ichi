package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.SettingsTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel

@Composable
fun SettingsModuleScreen(
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "CONSOLE SYSTEM SETTINGS",
                subtitle = "Workspace • Audio Latency & Options",
                moduleIcon = Icons.Default.Settings,
                accentColor = Color(0xFF64748B),
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
                .testTag("settings_workspace_screen")
        ) {
            SettingsTab(
                viewModel = sharedViewModel
            )
        }
    }
}
