package com.example.aquaboom.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun register(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess(it.user!!)
            }
            .addOnFailureListener {
                onError(it.message ?: "Registration failed")
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Login Failed")
            }
    }
    fun logout(context: Context) {
        auth.signOut()

        val googleClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )
        googleClient.signOut()
    }
    fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = auth.currentUser
                val firestore = FirebaseFirestore.getInstance()
                user?.let {
                    val userMap = hashMapOf(
                        "name" to (it.displayName ?: "User"),
                        "email" to (it.email ?: "")
                    )
                    firestore.collection("users")
                        .document(it.uid)
                        .set(userMap)
                }
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Google Sign-In Failed")
            }
    }
}