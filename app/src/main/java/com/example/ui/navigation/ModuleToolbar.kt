package com.example.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.UniversalSendToButton
import com.example.util.UniversalAudioMetadataExtractor
import com.example.viewmodel.WorkstationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleToolbar(
    title: String,
    subtitle: String,
    moduleIcon: ImageVector,
    accentColor: Color,
    viewModel: WorkstationViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSendToDialog: (sourceName: String) -> Unit,
    onImportAudioClick: (() -> Unit)? = null,
    onQuickRecordClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentChordModel by viewModel.currentChord.collectAsStateWithLifecycle()
    val globalKey by viewModel.globalKeySignature.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isStemPlaybackActive.collectAsStateWithLifecycle()

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val metadata = UniversalAudioMetadataExtractor.extractMetadataFromUri(context, uri)
            viewModel.importUniversalAudio(metadata)
        }
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF070F1C),
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.testTag("drawer_hamburger_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Drawer",
                    tint = accentColor
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, accentColor),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = moduleIcon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        if (isPlaying) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00E5FF), CircleShape)
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    )
                }
            }
        },
        actions = {
            // Live Chord / Key Pill Badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF13233C),
                border = BorderStroke(1.dp, Color(0xFF1E3A60)),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFFD4AF37),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = currentChordModel?.name ?: globalKey ?: "C Maj",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37)
                    )
                }
            }

            // Universal Import Audio Action Button
            Button(
                onClick = {
                    if (onImportAudioClick != null) {
                        onImportAudioClick()
                    } else {
                        documentLauncher.launch(arrayOf("audio/*"))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, accentColor),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier
                    .height(28.dp)
                    .padding(end = 4.dp)
                    .testTag("toolbar_import_audio_button")
            ) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = "Import Audio File",
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "IMPORT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            // Universal Send To Action Button on Module Toolbar
            UniversalSendToButton(
                sourceName = "$title Module Workspace Payload",
                buttonText = "SEND TO",
                accentColor = accentColor,
                onOpenSendToDialog = onOpenSendToDialog
            )

            // Settings icon
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Console Settings",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
}
