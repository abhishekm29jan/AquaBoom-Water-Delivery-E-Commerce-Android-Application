package com.example.aquaboom.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.model.Order
import com.example.aquaboom.ui.components.Background
import com.example.aquaboom.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrderSummaryScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total by cartViewModel.totalPrice.collectAsState()
    val darkMode = DarkModeManager.isDarkMode.value

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Order Summary 🧾",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    "${item.product.name} x ${item.quantity}",
                                    color = Color.White
                                )

                                Text(
                                    "₹${item.product.price * item.quantity}",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Total: ₹$total",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        cartViewModel.placeOrder(
                            userId = userId,
                            onSuccess = {
                                navController.navigate("orderSuccess") {
                                    popUpTo("product") { inclusive = false }
                                }
                            },
                            onFailure = {
                                Log.e("ORDER", "Failed: ${it.message}")
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (darkMode)
                                Color(0xFF272C2E)
                            else
                                Color(0xFF26658C)
                    )
                ) {
                    Text(
                        text = "Place Order",
                        color = Color.White
                    )
                }
            }
        }
    }
}