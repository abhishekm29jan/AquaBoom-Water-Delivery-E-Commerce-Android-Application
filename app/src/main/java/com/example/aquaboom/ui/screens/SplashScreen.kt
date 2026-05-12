package com.example.aquaboom.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquaboom.ui.components.Splash_Background
import com.example.aquaboom.ui.theme.CormorantFont
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    val fullText = "AQUABOOM"
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        for (i in fullText.indices) {
            displayedText = fullText.substring(0, i + 1)
            delay(80)
        }

        delay(600)

        for (i in fullText.indices) {
            displayedText = fullText.substring(i)
            delay(120)
        }

        delay(300)

        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Splash_Background()

        Text(
            text = displayedText,
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = CormorantFont,
            letterSpacing = 4.sp
        )
    }
}