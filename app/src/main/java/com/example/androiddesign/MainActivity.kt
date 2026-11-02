package com.example.androiddesign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.androiddesign.ui.MockupAppScreen
import com.example.androiddesign.ui.theme.AndroidDesignTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidDesignTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MockupAppScreen()
                }
            }
        }
    }
}
