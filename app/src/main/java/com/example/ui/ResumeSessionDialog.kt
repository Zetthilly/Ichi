package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.WorkstationViewModel

@Composable
fun ResumeSessionDialog(
    viewModel: WorkstationViewModel,
    onDismiss: () -> Unit
) {
    val sessionInfo by viewModel.resumeSessionInfo.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF071122),
            border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("resume_session_dialog")
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.12f),
                                Color(0xFF040A18)
                            )
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Text(
                        text = "💾 SMART SESSION RECOVERY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Resume Previous Session?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "HZ CHORD AI saved all module states, audio buffers, chord timelines, and mixer settings automatically.",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                sessionInfo?.let { info ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🎵 ${info.activeProjectTitle}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "BPM: ${info.bpm}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Key: ${info.keySignature}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF00E5FF)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Action Buttons
                Button(
                    onClick = { viewModel.resumePreviousSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("resume_session_btn_resume")
                ) {
                    Text(
                        text = "⚡ RESUME SESSION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF030814)
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.startNewProject() },
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("resume_session_btn_new")
                ) {
                    Text(
                        text = "✨ START NEW PROJECT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                TextButton(
                    onClick = { viewModel.openAnotherProject() },
                    modifier = Modifier.testTag("resume_session_btn_open_another")
                ) {
                    Text(
                        text = "📂 Open Another Project",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

