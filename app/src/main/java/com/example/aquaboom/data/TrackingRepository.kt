package com.example.aquaboom.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class TrackingRepository {

    private val db = FirebaseFirestore.getInstance()

    fun listenToTracking(orderId: String) = callbackFlow {

        val listener = db.collection("delivery_tracking")
            .document(orderId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val lat = it.getDouble("lat") ?: 0.0
                    val lng = it.getDouble("lng") ?: 0.0
                    val status = it.getString("status") ?: "Preparing"

                    trySend(Triple(lat, lng, status))
                }
            }

        awaitClose { listener.remove() }
    }
}