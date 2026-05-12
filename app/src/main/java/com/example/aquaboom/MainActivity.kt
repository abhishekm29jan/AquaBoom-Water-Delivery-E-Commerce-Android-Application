package com.example.aquaboom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aquaboom.ui.navigation.AppNavigation
import com.example.aquaboom.ui.theme.AquaBoomTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AquaBoomTheme {
                AppNavigation()
            }
        }
    }
}