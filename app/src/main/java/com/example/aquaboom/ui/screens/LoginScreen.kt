package com.example.aquaboom.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquaboom.R
import com.example.aquaboom.data.FirebaseAuthManager
import com.example.aquaboom.ui.components.*
import com.example.aquaboom.utils.ADMIN_EMAIL
import com.example.aquaboom.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val viewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    val authManager = FirebaseAuthManager()
    val googleSignInClient = remember {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1027878828175-uh8tbvc83lelip57ehmb0q2oedmmk6ia.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    // ✅ ONLY THIS BLOCK IS MODIFIED (Google Success Part)

                    authManager.firebaseAuthWithGoogle(
                        idToken = idToken,
                        onSuccess = {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val userEmail = currentUser?.email?.trim()?.lowercase()
                            val adminEmail = ADMIN_EMAIL.trim().lowercase()

                            if (!adminEmail.isNullOrEmpty() && userEmail == adminEmail) {

                                // 🔐 FIXED: GO TO OTP (NOT ADMIN DIRECTLY)
                                navController.navigate("admin") {      // otp navigation for google
                                    popUpTo("login") { inclusive = true }
                                }

                            } else {

                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        },
                        onError = {
                            Log.e("GOOGLE", "Error : $it")
                        }
                    )
                } else {
                    Log.e("GOOGLE", "ID Token is null")
                }
            } catch (e: Exception) {
                Log.e("GOOGLE", e.message ?: "Google Sign-In failed")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Main_Background()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TopBar()

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(22.dp)
            ) {
                PremiumTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    icon = Icons.Default.Person,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                PremiumPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("Forgot password?", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        viewModel.login(email, password) {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val userEmail = currentUser?.email?.trim()?.lowercase()
                            val adminEmail = ADMIN_EMAIL.trim().lowercase()

                            if (!adminEmail.isNullOrEmpty() && userEmail == adminEmail) {
                                navController.navigate("admin_otp") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF26658C),
                        contentColor = Color.White
                    )
                ) {
                    Text("Login", fontSize = 18.sp)
                }

                viewModel.errorMessage?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(it, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.4f)
                    )
                    Text("  Or continue with  ", fontSize = 12.sp, color = Color.White)
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.google),
                            contentDescription = "Google",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Google", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't have an account? ", color = Color.White)
                    Text(
                        "Sign Up",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate("register")
                        }
                    )
                }
            }
        }

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}