package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainWorkstationApp
import com.example.viewmodel.WorkstationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge full screen support
        enableEdgeToEdge()
        
        setContent {
            val workstationViewModel: WorkstationViewModel = viewModel()
            MainWorkstationApp(viewModel = workstationViewModel)
        }
    }
}
