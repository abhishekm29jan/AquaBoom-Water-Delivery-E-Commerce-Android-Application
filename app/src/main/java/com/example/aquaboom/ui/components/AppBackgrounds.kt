package com.example.aquaboom.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.aquaboom.R
import com.example.aquaboom.data.DarkModeManager

@Composable
fun Splash_Background() {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // VERY IMPORTANT
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun Main_Background() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // VERY IMPORTANT
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}

// Home + Product + Profile + Everything
@Composable
fun Background(modifier: Modifier = Modifier) {
    val darkMode = DarkModeManager.isDarkMode.value

    val gradient = if (darkMode) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F0F0F),
                Color(0xFF1A1A1A),
                Color(0xFF000000)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFB3E5FC),
                Color(0xFF4FC3F7),
                Color(0xFF0288D1)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    )
}