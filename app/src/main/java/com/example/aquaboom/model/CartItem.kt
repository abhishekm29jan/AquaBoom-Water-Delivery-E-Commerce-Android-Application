package com.example.aquaboom.model

data class CartItem(
    val product: Product = Product(),
    val quantity: Int = 1
)