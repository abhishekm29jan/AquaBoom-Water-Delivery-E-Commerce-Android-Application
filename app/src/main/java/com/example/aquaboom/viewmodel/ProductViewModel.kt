package com.example.aquaboom.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aquaboom.data.ProductRepository
import com.example.aquaboom.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    init {
        loadProducts()
    }

    private fun loadProducts() {
        _products.value = ProductRepository.getProducts()
    }
}