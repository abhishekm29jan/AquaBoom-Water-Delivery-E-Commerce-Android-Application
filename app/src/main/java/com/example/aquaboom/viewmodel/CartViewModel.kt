package com.example.aquaboom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquaboom.model.CartItem
import com.example.aquaboom.model.Order
import com.example.aquaboom.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import java.util.UUID

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // ➕ Add item
    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val existing = current.find { it.product.id == product.id }

        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + 1)
            current[current.indexOf(existing)] = updated
        } else {
            current.add(CartItem(product, 1))
        }

        _cartItems.value = current
    }

    // ➖ Remove / decrease
    fun removeFromCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val existing = current.find { it.product.id == product.id }

        if (existing != null) {
            if (existing.quantity > 1) {
                val updated = existing.copy(quantity = existing.quantity - 1)
                current[current.indexOf(existing)] = updated
            } else {
                current.remove(existing)
            }
        }

        _cartItems.value = current
    }

    fun decreaseQuantity(product: Product) {
        removeFromCart(product)
    }

    // 🧹 Clear cart
    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // 💰 Total price
    val totalPrice: StateFlow<Int> = cartItems
        .map { items ->
            items.sumOf { it.product.price * it.quantity }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )
    fun placeOrder(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val orderId = UUID.randomUUID().toString()

        val order = Order(
            id = orderId,
            userId = userId,
            items = _cartItems.value,
            total = totalPrice.value,
            status = "Placed",
            timestamp = System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("orders")
            .document(orderId)
            .set(order)
            .addOnSuccessListener {
                _cartItems.value = emptyList()
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}