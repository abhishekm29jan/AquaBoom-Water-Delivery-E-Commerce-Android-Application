package com.example.aquaboom.ui.screens

import android.content.Intent
import android.widget.Toast
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.aquaboom.service.LocationService
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.ui.components.Background

@Composable
fun OrderSuccessScreen(navController: NavHostController) {

    val darkMode = DarkModeManager.isDarkMode.value
    val orderId = "order_123"
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

            // 🚀 START SERVICE AFTER PERMISSION
            val intent = Intent(context, LocationService::class.java)
            intent.putExtra("orderId", orderId)

            ContextCompat.startForegroundService(context, intent)

            navController.navigate("tracking/$orderId")

        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎉 Order Placed Successfully!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your order is on the way 💧",
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    if (ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        val intent = Intent(context, LocationService::class.java)
                        intent.putExtra("orderId", orderId)

                        ContextCompat.startForegroundService(context, intent)

                        navController.navigate("tracking/$orderId")

                    } else {
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (darkMode)
                            Color(0xFF272C2E)
                        else
                            Color(0xFF26658C),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Track Order")
            }
        }
    }
}