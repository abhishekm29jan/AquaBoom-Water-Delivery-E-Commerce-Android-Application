package com.example.aquaboom.ui.screens

import android.R
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.model.Product
import com.example.aquaboom.ui.components.Background
import com.example.aquaboom.ui.components.BottomNavBar
import com.example.aquaboom.viewmodel.CartViewModel
import com.example.aquaboom.viewmodel.ProductViewModel

@Composable
fun ProductScreen(navController: NavHostController,
                  cartViewModel: CartViewModel)
{
    val viewModel: ProductViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val darkMode = DarkModeManager.isDarkMode.value
    
    Scaffold(
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = cartItems.isNotEmpty(),
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    val total = cartItems.fold(0) { acc, item ->
                        acc + (item.product.price * item.quantity)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (darkMode)
                                    Color(0xFF272C2E)   // darker glass blue
                                else
                                    Color(0xFF26658C),   // Button background   // Button background
                            contentColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("cart") {
                                        launchSingleTop = true
                                    }
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${cartItems.sumOf { it.quantity }} items",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "₹$total",
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "View Cart →",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                BottomNavBar(navController)
            }
        }
    ) {
        padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Browse Bottles 💧",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProductGrid(
                    products = products,
                    cartViewModel = cartViewModel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 4.dp,
            end = 4.dp,
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(products) { product ->
            ProductGridItem(product, cartViewModel)
        }
    }
}

@Composable
fun ProductGridItem(
    product: Product,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val quantity = cartItems.firstOrNull { it.product.id == product.id }?.quantity ?: 0
    val darkMode = DarkModeManager.isDarkMode.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(20.dp)) // 🔥 THIS FIX
            .background(Color.White.copy(alpha = 0.15f))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.25f),
                RoundedCornerShape(20.dp)
            )
            .padding(10.dp) // move inner padding here
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = product.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.name,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 2,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹${product.price}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (quantity == 0) {
                OutlinedButton(
                    onClick = { cartViewModel.addToCart(product) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (darkMode) Color(0xFF272C2E) else Color(0xFF26658C)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (darkMode) Color(0xFF272C2E) else Color(0xFF26658C),
                        contentColor = Color.White
                    )
                ) {
                    Text("ADD", fontSize = 12.sp)
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (darkMode) Color(0xFF272C2E)
                            else Color(0xFF26658C)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "-",
                        modifier = Modifier
                            .clickable { cartViewModel.removeFromCart(product) }
                            .padding(4.dp),
                        color = Color.White
                    )

                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 6.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "+",
                        modifier = Modifier
                            .clickable { cartViewModel.addToCart(product) }
                            .padding(4.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}
  