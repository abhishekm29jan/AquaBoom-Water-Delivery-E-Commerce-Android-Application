package com.example.aquaboom.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aquaboom.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrderViewModel : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val db = FirebaseFirestore.getInstance()

    fun fetchOrders(userId: String) {
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->

                val list = result.documents.mapNotNull { doc ->
                    try {
                        val order = doc.toObject(Order::class.java)

                        // ✅ SAFE FIX
                        order?.copy(
                            items = order.items.ifEmpty { emptyList() }
                        )

                    } catch (e: Exception) {
                        null
                    }
                }

                _orders.value = list
            }
    }

    // 🔥 SELECTED ORDER (for navigation)
    var selectedOrder = mutableStateOf<Order?>(null)

    fun selectOrder(order: Order) {
        selectedOrder.value = order
    }
}