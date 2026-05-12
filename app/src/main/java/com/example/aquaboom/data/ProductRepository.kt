package com.example.aquaboom.data

import com.example.aquaboom.R
import com.example.aquaboom.model.Product

object ProductRepository {

    fun getProducts(): List<Product> {
        return listOf(
            Product("1", "250ml Bottle", 5, R.drawable.bottle),
            Product("2", "500ml Bottle", 10, R.drawable.bottle1),
            Product("3", "1L Bottle", 20, R.drawable.bottle2),
            Product("4", "2L Bottle", 30, R.drawable.bottle3),
            Product("5", "5L Bottle", 60, R.drawable.bottle4),
            Product("6", "10L Bottle", 110, R.drawable.bottle5),
            Product("7", "20L Bottle", 200, R.drawable.bottle6)
        )
    }
}