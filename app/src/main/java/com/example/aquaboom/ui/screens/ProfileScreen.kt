package com.example.aquaboom.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.aquaboom.R
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.data.FirebaseAuthManager
import com.example.aquaboom.ui.components.Background
import com.example.aquaboom.ui.components.BottomNavBar
import com.example.aquaboom.ui.components.DarkModeToggle
import com.example.aquaboom.utils.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavHostController) {

    val user = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: ""
                    email = document.getString("email") ?: ""
                }
        }
    }
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Background()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                ProfileTopBar(navController)

                Spacer(modifier = Modifier.height(16.dp))

                ProfileHeader(name, email)

                Spacer(modifier = Modifier.height(16.dp))

                ProfileMenu(navController)
            }
        }
    }
}

@Composable
fun ProfileTopBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = "Profile",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ProfileHeader(name: String, email: String) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable { launcher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable { launcher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                        .clickable { launcher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (name.isEmpty()) "Enter your name" else name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ProfileMenu(navController: NavHostController) {

    val context = LocalContext.current
    val authManager = FirebaseAuthManager()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val darkMode = DarkModeManager.isDarkMode.value

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = if (darkMode)
                Color(0xFF272C2E)
            else
                Color(0xFF26658C),
            tonalElevation = 8.dp,

            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    "Are you sure you want to logout?",
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authManager.logout(context)

                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(
                        "Cancel",
                        color = Color.White
                    )
                }
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DarkModeToggle()

        ProfileMenuItem(
            icon = Icons.Default.Person,
            title = "Personal Information",
            onClick = { navController.navigate("personalInfo") }
        )

        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            title = "Saved Addresses",
            onClick = { navController.navigate("addresses") }
        )

        ProfileMenuItem(
            icon = Icons.Default.CreditCard,
            title = "Payment Methods",
            onClick = { }
        )

        ProfileMenuItem(
            icon = Icons.Default.ShoppingBag,
            title = "Orders",
            onClick = { navController.navigate("myOrders") }
        )

        ProfileMenuItem(
            icon = Icons.Default.Help,
            title = "Help & Support",
            onClick = { navController.navigate(Routes.HELP_SUPPORT)}
        )

        ProfileMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            onClick = {
                showLogoutDialog = true
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

