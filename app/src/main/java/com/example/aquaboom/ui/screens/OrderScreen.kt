package com.example.aquaboom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquaboom.model.Order
import com.example.aquaboom.ui.components.Background
import com.example.aquaboom.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrdersScreen(
    navController: NavHostController,
    viewModel: OrderViewModel
) {
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModel.fetchOrders(user.uid)
        }
    }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Text(
                    text = "My Orders 📦",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (orders.isEmpty()) {
                    Text(
                        text = "No orders yet",
                        color = Color.White
                    )
                } else {
                    LazyColumn {
                        items(orders) { order ->
                            OrderCard(
                                order = order,
                                onClick = {
                                    viewModel.selectOrder(order)
                                    navController.navigate("orderDetails")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    val firstItem = order.items.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔥 IMAGE / FALLBACK
            if (firstItem != null) {
                Image(
                    painter = painterResource(firstItem.product.image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧾")
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // 🔥 PRODUCT NAME / FALLBACK
                Text(
                    text = firstItem?.product?.name ?: "Water Order",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 🔥 ITEM COUNT + PRICE
                Text(
                    text = if (order.items.isNotEmpty())
                        "${order.items.size} items • ₹${order.total}"
                    else
                        "₹${order.total}",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Text(
                text = "›",
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
    }
}