package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainWorkstationApp
import com.example.viewmodel.WorkstationViewModel

class MainActivity : ComponentActivity() {
    private val workstationViewModel: WorkstationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge full screen support
        enableEdgeToEdge()
        
        setContent {
            MainWorkstationApp(viewModel = workstationViewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        workstationViewModel.saveCurrentModuleStateImmediately()
    }

    override fun onStop() {
        super.onStop()
        workstationViewModel.saveCurrentModuleStateImmediately()
    }
}
