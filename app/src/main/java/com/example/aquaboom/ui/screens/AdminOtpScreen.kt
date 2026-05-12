package com.example.aquaboom.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aquaboom.utils.AdminConfig
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

@Composable
fun AdminOtpScreen(navController: NavController) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as Activity

    val auth = FirebaseAuth.getInstance()

    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // 🔥 SEND OTP
    fun sendOtp() {

        loading = true

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(AdminConfig.ADMIN_PHONE)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    // 🔥 AUTO VERIFY (IMPORTANT)
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            navController.navigate("admin") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    loading = false
                    Log.e("OTP", "FAILED: ${e.message}")
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = verId
                    isOtpSent = true
                    loading = false
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 🔥 VERIFY OTP
    fun verifyOtp() {

        if (verificationId == null) return

        loading = true

        val credential = PhoneAuthProvider.getCredential(
            verificationId!!,
            otp
        )

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                navController.navigate("admin") {
                    popUpTo("login") { inclusive = true }
                }
            }
            .addOnFailureListener {
                loading = false
                Log.e("OTP", "VERIFY FAILED: ${it.message}")
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        if (!isOtpSent) {

            Button(
                onClick = { sendOtp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send OTP")
            }

        } else {

            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Enter OTP") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { verifyOtp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verify OTP")
            }
        }

        if (loading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}