package com.example.aquaboom.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class LocationService : Service() {

    private val firestore = FirebaseFirestore.getInstance()
    private var orderId: String = ""

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        orderId = intent?.getStringExtra("orderId") ?: ""

        Log.d("SERVICE", "Started with orderId = $orderId")

        if (orderId.isEmpty()) {
            Log.e("SERVICE", "OrderId empty, stopping")
            return START_NOT_STICKY
        }

        startForegroundNotification()

        // 🚀 FETCH ROUTE + START SIMULATION
        Thread {
            val route = fetchRoute()
            startSimulation(route)
        }.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // 🔔 Notification
    private fun startForegroundNotification() {

        val channelId = "location_channel"

        val manager = getSystemService(NotificationManager::class.java)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking Delivery 🚚")
            .setContentText("Live delivery in progress...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }

    // 🌍 FETCH ROUTE FROM GOOGLE DIRECTIONS API
    private fun fetchRoute(): List<LatLng> {

        return try {

            val apiKey = "AIzaSyCW5luHw5ElZW7Sal_eZtQx1tYnOGgRrto"

            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=20.2961,85.8245&destination=20.2730,85.8400&key=$apiKey"

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return emptyList()

            val json = JSONObject(body)

            val points = json.getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("overview_polyline")
                .getString("points")

            decodePolyline(points)

        } catch (e: Exception) {
            Log.e("SERVICE", "Route fetch failed: ${e.message}")
            emptyList()
        }
    }

    // 🛣 DECODE POLYLINE
    private fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {

            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return poly
    }

    // 🚚 SIMULATED MOVEMENT
    private fun startSimulation(route: List<LatLng>) {

        if (route.isEmpty()) {
            Log.e("SERVICE", "Route is empty")
            return
        }

        for (point in route) {

            try {

                firestore.collection("delivery_tracking")
                    .document(orderId)
                    .set(
                        mapOf(
                            "lat" to point.latitude,
                            "lng" to point.longitude,
                            "status" to "On the way",
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )

                Thread.sleep(1500)

            } catch (e: Exception) {
                Log.e("SERVICE", "Firestore error: ${e.message}")
            }
        }

        Log.d("SERVICE", "Delivery completed")
    }
}