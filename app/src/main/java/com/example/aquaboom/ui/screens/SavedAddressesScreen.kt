package com.example.aquaboom.ui.screens

import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquaboom.data.AddressManager
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.ui.components.Background

@Composable
fun SavedAddressesScreen(navController: NavHostController) {

    val addresses = AddressManager.addresses
    val darkMode = DarkModeManager.isDarkMode.value
    var selectedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(addresses.size) {
        if (addresses.isNotEmpty()) {
            selectedIndex = addresses.lastIndex
        }
    }

    Scaffold{ padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Background()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding) // ✅ THIS FIXES SCREEN FIT
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                    Text(
                        text = "Saved Addresses",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                if (addresses.isEmpty()) {
                    Text(
                        text = "No address added yet",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                } else {
                    addresses.forEachIndexed { index, address ->
                        val dismissState = rememberSwipeToDismissBoxState()
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {}
                        ) {
                            AddressCard(
                                title = address.name,
                                address = "${address.street}, ${address.city}",
                                onEdit = {
                                    navController.navigate("editAddress/$index")
                                },
                                isSelected = selectedIndex == index,
                                onClick = {
                                    selectedIndex = index

                                    navController.navigate("orderSummary") {
                                        launchSingleTop = true
                                        popUpTo("addresses") {
                                            inclusive = false
                                        }
                                    }
                                }
                            )
                        }

                        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                            AddressManager.deleteAddress(index)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate("addAddress") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (darkMode)
                                Color(0xFF272C2E)
                            else
                                Color(0xFF26658C)
                    )
                ) {
                    Text(text = "Add New Address", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AddressCard(
    title: String,
    address: String,
    onEdit: () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isSelected)
                    Color.White.copy(alpha = 0.25f)
                else
                    Color.White.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = address,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Address",
                    tint = Color.White
                )
            }
        }
    }
}