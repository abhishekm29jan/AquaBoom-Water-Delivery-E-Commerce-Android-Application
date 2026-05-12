package com.example.aquaboom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items   // ✅ IMPORTANT
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource   // ✅ IMPORTANT
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.model.CartItem      // ✅ IMPORTANT
import com.example.aquaboom.ui.components.Background
import com.example.aquaboom.ui.components.BottomNavBar
import com.example.aquaboom.viewmodel.CartViewModel

@Composable
fun CartScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val darkMode = DarkModeManager.isDarkMode.value

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Text(
                    text = "Your Cart 🛒",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (cartItems.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Your cart is empty 😔",
                            color = Color.White
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemCard(item, cartViewModel)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController.navigate("addresses") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (darkMode)
                                    Color(0xFF272C2E)   // darker glass blue
                                else
                                    Color(0xFF26658C),   // Button background   // Button background
                            contentColor = Color.White
                        )
                    ) {
                        Text("Checkout")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    cartViewModel: CartViewModel
) {
    val product = item.product
    val darkMode = DarkModeManager.isDarkMode.value

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = product.image),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = Color.White)
                Text(
                    text = "₹${product.price}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .background(
                        if (darkMode)
                            Color(0xFF272C2E)
                        else
                            Color(0xFF26658C),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "-",
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            cartViewModel.removeFromCart(product)
                        }
                )

                Text(
                    item.quantity.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "+",
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            cartViewModel.addToCart(product)
                        }
                )
            }
        }
    }
}