package com.example.aquaboom.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp)),
            containerColor = Color.White.copy(alpha = 0.85f),
            tonalElevation = 6.dp
        ) {
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("home") },
                icon = {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3F8CFF),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("product") },
                icon = {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Shop")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3F8CFF),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("tracking/{orderId}") },
                icon = {
                    Icon(Icons.Default.LocalShipping, contentDescription = "Tracking")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3F8CFF),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("profile") },
                icon = {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3F8CFF),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}