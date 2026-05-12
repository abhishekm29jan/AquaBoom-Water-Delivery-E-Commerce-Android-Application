package com.example.aquaboom.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Int = 0,
    val status: String = "Placed",
    val timestamp: Long = 0L
)