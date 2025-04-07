package com.binissa.particlize.showcase

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Main entry point for the Particlize example app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticlizeExampleApp() {
    val navController = rememberNavController()
    
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("directional") {
                DirectionalEffectsScreen(navController)
            }
            composable("radial") {
                RadialEffectsScreen(navController)
            }
            composable("explosion") {
                ExplosionEffectsScreen(navController)
            }
            composable("vortex") {
                VortexEffectsScreen(navController)
            }
            composable("assembly") {
                AssemblyEffectsScreen(navController)
            }
            composable("confetti") {
                ConfettiEffectsScreen(navController)
            }
            composable("custom") {
                CustomEffectsScreen(navController)
            }
        }
    }
}