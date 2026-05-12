package com.example.aquaboom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.aquaboom.data.DirectionsApi
import com.example.aquaboom.data.TrackingRepository
import com.example.aquaboom.data.decodePolyline
import com.example.aquaboom.model.TrackingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java

class TrackingViewModel : ViewModel() {

    private val _trackingState = MutableStateFlow(TrackingState())
    val trackingState: StateFlow<TrackingState> = _trackingState

    fun startTracking(orderId: String) {
        FirebaseFirestore.getInstance()
            .collection("delivery_tracking")
            .document(orderId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val lat = it.getDouble("lat") ?: 0.0
                    val lng = it.getDouble("lng") ?: 0.0
                    val status = it.getString("status") ?: ""

                    _trackingState.value = TrackingState(
                        lat = lat,
                        lng = lng,
                        status = status
                    )
                }
            }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(DirectionsApi::class.java)

    suspend fun getRoutePoints(
        origin: LatLng,
        destination: LatLng
    ): List<LatLng> {

        val response = api.getRoute(
            origin = "${origin.latitude},${origin.longitude}",
            destination = "${destination.latitude},${destination.longitude}",
            apiKey = "AIzaSyCW5luHw5ElZW7Sal_eZtQx1tYnOGgRrto"
        )
        Log.d("ROUTE_DEBUG", "Response: ${response.routes}")

        val encoded = response.routes.firstOrNull()
            ?.overview_polyline?.points ?: ""

        return decodePolyline(encoded)
    }
}