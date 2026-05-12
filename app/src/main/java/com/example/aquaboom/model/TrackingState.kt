package com.example.aquaboom.model

data class TrackingState(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val status: String = "Preparing"
)
