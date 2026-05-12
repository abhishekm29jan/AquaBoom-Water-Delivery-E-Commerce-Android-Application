package com.example.aquaboom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aquaboom.model.*
import com.example.aquaboom.ui.components.Splash_Background
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        firestore.collection("orders")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    orders = snapshot.documents.map {

                        val itemsList =
                            it.get("items") as? List<Map<String, Any>> ?: emptyList()

                        val cartItems = itemsList.map { item ->

                            val productMap =
                                item["product"] as? Map<String, Any> ?: emptyMap()

                            CartItem(
                                product = Product(
                                    name = productMap["name"] as? String ?: "",
                                    price = (productMap["price"] as? Long)?.toInt() ?: 0
                                ),
                                quantity = (item["quantity"] as? Long)?.toInt() ?: 1
                            )
                        }

                        Order(
                            id = it.id,
                            userId = it.getString("userId") ?: "",
                            items = cartItems,
                            total = (it.getLong("total") ?: 0L).toInt(),
                            status = it.getString("status") ?: "Placed",
                            timestamp = it.getLong("timestamp") ?: 0L
                        )
                    }
                }
            }
    }

    val filteredOrders = orders.filter {
        val matchesSearch = it.id.contains(searchQuery, true)
        val matchesFilter = selectedFilter == "All" || it.status == selectedFilter
        matchesSearch && matchesFilter
    }

    val revenue = orders.filter { it.status == "Delivered" }.sumOf { it.total }
    val totalOrders = orders.size
    val pending = orders.count { it.status != "Delivered" }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Splash_Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                // HEADER
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Manage orders", color = Color.White.copy(0.6f))
                    }

                    Row {
                        IconButton(onClick = {
                            navController.navigate("banner_admin")
                        }) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.White)
                        }

                        IconButton(onClick = {
                            navController.navigate("home")
                        }) {
                            Icon(Icons.Default.Home, null, tint = Color.White)
                        }
                    }
                }

                // KPI
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { KPI("Revenue", "₹$revenue", Color(0xFF22C55E)) }
                    item { KPI("Orders", "$totalOrders", Color(0xFF3B82F6)) }
                    item { KPI("Pending", "$pending", Color(0xFFF59E0B)) }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search orders...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("All", "Placed", "Preparing", "On the way", "Delivered")) {
                        FilterPill(it, selectedFilter == it) {
                            selectedFilter = it
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredOrders) { order ->
                        AdminOrderCard(order) {
                            selectedOrder = order
                            showBottomSheet = true
                        }
                    }
                }
            }
        }

        if (showBottomSheet && selectedOrder != null) {
            val order = selectedOrder!!
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = Color.Transparent // 🔥 remove default dark bg
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            ),
                            RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        // 🔹 HANDLE BAR
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(40.dp)
                                .height(4.dp)
                                .background(
                                    Color.White.copy(alpha = 0.4f),
                                    RoundedCornerShape(50)
                                )
                        )
                        Spacer(Modifier.height(16.dp))

                        // 🔥 TITLE
                        Text(
                            "Order Details",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        // 🔹 CARD SECTION
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.06f)
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {

                                InfoRow("Order ID", order.id.takeLast(8))
                                InfoRow("Status", order.status)
                                InfoRow("Total", "₹${order.total}")
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // 🔹 ITEMS
                        Text(
                            "Items",
                            color = Color.White.copy(0.8f),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(8.dp))

                        order.items.forEach {
                            Text(
                                "• ${it.product.name} x${it.quantity}",
                                color = Color.White.copy(0.7f)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // 🔥 STATUS SECTION (YOUR EXISTING LOGIC)
                        SmartStatusSection(order)

                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderProgress(status: String) {

    val steps = listOf("Placed", "Preparing", "On the way", "Delivered")

    Row(Modifier.fillMaxWidth()) {

        steps.forEachIndexed { index, step ->

            val active = steps.indexOf(status) >= index

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (active) Color.Green else Color.White.copy(0.3f),
                            RoundedCornerShape(50)
                        )
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    step,
                    color = Color.White.copy(0.6f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun AnimatedStatusButton(
    text: String,
    color: Color,
    orderId: String,
    newStatus: String
) {

    Button(
        onClick = { updateStatus(orderId, newStatus) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // 🔥 FIX
            contentColor = color
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {

        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(50))
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onClick: () -> Unit) {
    val statusColor = when (order.status) {
        "Delivered" -> Color(0xFF22C55E)
        "On the way" -> Color(0xFFF59E0B)
        "Preparing" -> Color(0xFF3B82F6)
        else -> Color(0xFFA855F7)
    }

    val gradient = Brush.linearGradient(
        listOf(
            statusColor.copy(alpha = 0.25f),
            Color.Transparent   // 🔥 FIX: remove dark overlay
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {

        // 🔥 ONLY ONE BACKGROUND LAYER (no nested boxes)
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(18.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Order #${order.id.takeLast(5)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                StatusChip(order.status)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "₹${order.total}",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(14.dp))

            // 🔥 NO BOX WRAPPING HERE → FIXES OVERLAY
            OrderProgress(order.status)

            Spacer(Modifier.height(10.dp))

            SmartStatusSection(order)
        }
    }
}

@Composable
fun StatusChip(status: String) {

    val (bgColor, textColor) = when (status) {
        "Delivered" -> Color(0xFF22C55E) to Color.White
        "On the way" -> Color(0xFFF59E0B) to Color.White
        "Preparing" -> Color(0xFF3B82F6) to Color.White
        else -> Color(0xFFA855F7) to Color.White
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor.copy(alpha = 0.15f),
        tonalElevation = 4.dp
    ) {
        Text(
            text = status,
            color = bgColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun SmartStatusSection(order: Order) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Current: ${order.status}",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium
        )

        // 🔥 ACTION BUTTON BASED ON STATUS
        when (order.status) {
            "Placed" -> AnimatedStatusButton(
                text = "Start Preparing",
                color = Color(0xFFA855F7),
                orderId = order.id,
                newStatus = "Preparing"
            )

            "Preparing" -> AnimatedStatusButton(
                text = "Dispatch Order",
                color = Color(0xFFF59E0B),
                orderId = order.id,
                newStatus = "On the way"
            )

            "On the way" -> AnimatedStatusButton(
                text = "Mark as Delivered",
                color = Color(0xFF22C55E),
                orderId = order.id,
                newStatus = "Delivered"
            )

            "Delivered" -> {
                Text(
                    " Order Completed",
                    color = Color(0xFF22C55E),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {

    val bg = if (selected)
        Brush.horizontalGradient(
            listOf(Color(0xFF7F00FF), Color(0xFFE100FF))
        )
    else
        Brush.horizontalGradient(
            listOf(Color.White.copy(0.08f), Color.Transparent)
        )

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text,
            color = Color.White,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun KPI(title: String, value: String, color: Color) {

    val gradient = Brush.verticalGradient(
        listOf(color.copy(0.9f), color.copy(0.3f))
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.shadow(10.dp, RoundedCornerShape(22.dp))
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp)
        ) {
            Column {
                Text(title, color = Color.White.copy(0.8f))
                Text(
                    value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

fun updateStatus(orderId: String, status: String) {
    FirebaseFirestore.getInstance()
        .collection("orders")
        .document(orderId)
        .update("status", status)
}

@Composable
fun InfoRow(label: String, value: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(0.6f))
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}