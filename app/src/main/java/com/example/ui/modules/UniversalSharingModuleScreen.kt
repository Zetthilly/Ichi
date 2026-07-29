package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.UniversalAudioSharingEngineCard
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel
import com.example.viewmodel.modules.UniversalSharingViewModel

@Composable
fun UniversalSharingModuleScreen(
    sharingViewModel: UniversalSharingViewModel,
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "UNIVERSAL AUDIO SHARING & C++ BUS",
                subtitle = "Module 05 • Direct In-Memory Buffer Pointers",
                moduleIcon = Icons.Default.Hub,
                accentColor = Color(0xFFEC4899),
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
                .padding(14.dp)
                .testTag("universal_sharing_workspace_screen")
        ) {
            UniversalAudioSharingEngineCard(
                viewModel = sharedViewModel
            )
        }
    }
}
