package com.example.whatsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.whatsapp.presentation.navigation.WhatsAppNavigationSystem
import com.example.whatsapp.presentation.viewmodel.BaseViewModel
import com.example.whatsapp.ui.theme.WhatsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var baseViewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 INIT VIEWMODEL
        baseViewModel = ViewModelProvider(this)[BaseViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            WhatsAppTheme {
                WhatsAppNavigationSystem()
            }
        }
    }

    // ✅ USER IS ONLINE (APP IN FOREGROUND)
    override fun onStart() {
        super.onStart()
        baseViewModel.setUserOnline()
    }

    // ✅ USER GOES OFFLINE (APP IN BACKGROUND)
    override fun onStop() {
        super.onStop()
        baseViewModel.setUserOffline()
    }
}
