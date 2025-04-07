package com.binissa.particlize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.binissa.particlize.showcase.ParticlizeDemo
import com.binissa.particlize.ui.theme.ParticlizeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ParticlizeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ParticlizeDemo()
                }
            }
        }
    }
}



