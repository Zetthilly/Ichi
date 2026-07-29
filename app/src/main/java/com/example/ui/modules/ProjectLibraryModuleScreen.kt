package com.example.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.LibrarySessionTab
import com.example.ui.navigation.ModuleToolbar
import com.example.viewmodel.WorkstationViewModel

@Composable
fun ProjectLibraryModuleScreen(
    sharedViewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit
) {
    Scaffold(
        topBar = {
            ModuleToolbar(
                title = "PROJECT LIBRARY & SESSIONS",
                subtitle = "Workspace • Recorded Audio & History",
                moduleIcon = Icons.Default.FolderSpecial,
                accentColor = Color(0xFF3B82F6),
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
                .testTag("project_library_workspace_screen")
        ) {
            LibrarySessionTab(
                viewModel = sharedViewModel
            )
        }
    }
}
