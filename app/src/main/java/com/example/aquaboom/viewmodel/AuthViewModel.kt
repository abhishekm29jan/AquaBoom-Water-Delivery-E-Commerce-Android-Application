package com.example.aquaboom.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.aquaboom.data.FirebaseAuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val authManager = FirebaseAuthManager()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }

        isLoading = true
        errorMessage = null

        authManager.loginUser(
            email,
            password,
            onSuccess = {
                isLoading = false
                onSuccess()
            },
            onError = {
                isLoading = false
                errorMessage = it
            }
        )
    }

    fun register(email: String, password: String, name: String, onSuccess: () -> Unit) {
        isLoading = true
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        firestore.collection("users")
                            .document(currentUser.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                isLoading = false
                                onSuccess()
                            }
                            .addOnFailureListener {
                                isLoading = false
                                errorMessage = it.message
                            }
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message
                }
            }
    }
}