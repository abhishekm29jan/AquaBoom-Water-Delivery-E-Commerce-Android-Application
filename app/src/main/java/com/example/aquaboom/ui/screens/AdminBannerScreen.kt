package com.example.aquaboom.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aquaboom.ui.components.Splash_Background
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBannerScreen(navController: NavController) {

    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var banners by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var uploading by remember { mutableStateOf(false) }

    // 🔥 FETCH BANNERS
    LaunchedEffect(Unit) {
        firestore.collection("banners")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    banners = snapshot.documents.mapNotNull {
                        val url = it.getString("imageUrl")
                        if (url != null) Pair(it.id, url) else null
                    }
                }
            }
    }

    // 🔥 IMAGE PICKER + UPLOAD
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploading = true
            val ref = storage.reference
                .child("banners/${System.currentTimeMillis()}.jpg")
            ref.putFile(it)
                .addOnSuccessListener {

                    Log.d("UPLOAD", "Storage upload success")

                    ref.downloadUrl
                        .addOnSuccessListener { downloadUrl ->

                            Log.d("UPLOAD", "URL: $downloadUrl")

                            // ✅ SAVE TO FIRESTORE
                            firestore.collection("banners")
                                .add(
                                    mapOf(
                                        "imageUrl" to downloadUrl.toString(),
                                        "timestamp" to System.currentTimeMillis()
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d("UPLOAD", "Firestore success ✅")
                                }
                                .addOnFailureListener {
                                    Log.e("UPLOAD", "Firestore FAILED: ${it.message}")
                                }

                            uploading = false
                        }
                        .addOnFailureListener {
                            uploading = false
                            Log.e("UPLOAD", "URL FAILED: ${it.message}")
                        }
                }
                .addOnFailureListener {
                    uploading = false
                    Log.e("UPLOAD", "UPLOAD FAILED: ${it.message}")
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Your Banner", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                )
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Splash_Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {

                // 🔥 UPLOAD BUTTON
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Banner")
                }

                if (uploading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔥 BANNER LIST
                LazyColumn {

                    items(banners) { (id, url) ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.12f)
                            )
                        ) {

                            Column(modifier = Modifier.padding(12.dp)) {

                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        firestore.collection("banners")
                                            .document(id)
                                            .delete()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Delete, null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}