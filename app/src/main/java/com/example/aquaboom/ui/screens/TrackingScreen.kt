package com.example.aquaboom.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aquaboom.viewmodel.TrackingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.example.aquaboom.R
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun TrackingScreen(
    navController: NavController,
    orderId: String
) {
    val context = LocalContext.current
    val viewModel: TrackingViewModel = viewModel()
    val state by viewModel.trackingState.collectAsState()

    val storeLocation = LatLng(20.2961, 85.8245)
    val homeLocation = LatLng(20.2730, 85.8400)

    // ✅ FIXED: Default camera (no Africa issue)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(storeLocation, 14f)
    }

    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val markerState = rememberMarkerState(position = storeLocation)

    val rawLocation = LatLng(state.lat, state.lng)
    val isLocationReady = state.lat != 0.0 && state.lng != 0.0

    val snappedLocation = if (routePoints.isNotEmpty() && isLocationReady) {
        getClosestPoint(rawLocation, routePoints)
    } else rawLocation

    var isUserMovingMap by remember { mutableStateOf(false) }
    var animatedPosition by remember { mutableStateOf(storeLocation) }

    // 🚀 Start tracking
    LaunchedEffect(Unit) {
        viewModel.startTracking(orderId)
        routePoints = viewModel.getRoutePoints(storeLocation, homeLocation)
    }

    // 🎯 Initial camera when location ready
    LaunchedEffect(isLocationReady) {
        if (isLocationReady) {
            animatedPosition = rawLocation
            markerState.position = rawLocation

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(rawLocation, 15f),
                1000
            )
        }
    }

    // 🚚 SMOOTH MOVEMENT (IMPROVED)
    LaunchedEffect(snappedLocation) {
        if (!isLocationReady) return@LaunchedEffect

        val start = markerState.position
        val end = snappedLocation

        val steps = 20 // smoother

        for (i in 1..steps) {
            val fraction = i / steps.toFloat()

            val lat = start.latitude + (end.latitude - start.latitude) * fraction
            val lng = start.longitude + (end.longitude - start.longitude) * fraction

            animatedPosition = LatLng(lat, lng)
            kotlinx.coroutines.delay(30) // smoother
        }
    }

    // 🚚 Update marker
    LaunchedEffect(animatedPosition) {
        markerState.position = animatedPosition
    }

    // 📷 SMART CAMERA FOLLOW (FIXED)
    LaunchedEffect(animatedPosition) {
        if (!isUserMovingMap && isLocationReady) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(animatedPosition),
                600
            )
        }
    }

    // 👆 Detect user interaction (Compose-compatible)
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            isUserMovingMap = true
        }
    }

    // ⏳ Reset follow after 5 sec
    LaunchedEffect(isUserMovingMap) {
        if (isUserMovingMap) {
            kotlinx.coroutines.delay(5000)
            isUserMovingMap = false
        }
    }

    // 🔄 Rotation (optimized)
    val rotation = remember(animatedPosition.latitude, animatedPosition.longitude) {
        val next = routePoints.minByOrNull {
            val dLat = it.latitude - animatedPosition.latitude
            val dLng = it.longitude - animatedPosition.longitude
            dLat * dLat + dLng * dLng
        }
        if (next != null) getRotation(animatedPosition, next) else 0f
    }

    // 📊 Progress
    val closestIndex = routePoints.indexOfFirst {
        it == getClosestPoint(animatedPosition, routePoints)
    }.coerceAtLeast(0)

    val progress = if (routePoints.isNotEmpty()) {
        closestIndex.toFloat() / routePoints.size
    } else 0f

    val remaining = routePoints.size - closestIndex

    val etaText = "${(remaining * 0.02).toInt().coerceAtLeast(1)} min"
    val distanceText = "%.2f km".format(remaining * 0.05)

    val autoStatus = when (progress) {
        in 0f..0.25f -> "Preparing your order"
        in 0.25f..0.5f -> "Picked up 🚚"
        in 0.5f..0.85f -> "On the way"
        else -> "Arriving soon ⏳"
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            
            Marker(
                state = MarkerState(storeLocation),
                title = "Store"
            )

            Marker(
                state = MarkerState(homeLocation),
                title = "Delivery Location"
            )

            Marker(
                state = markerState,
                title = "Delivery Partner",
                icon = bitmapFromResource(context, R.drawable.bike),
                anchor = Offset(0.5f, 1f),
                rotation = rotation,
                flat = true
            )

            Polyline(
                points = routePoints,
                color = Color.Blue,
                width = 8f
            )
        }

        // 📦 Bottom UI (UNCHANGED)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                Text("Live Tracking 🚚", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Text(autoStatus, color = Color.Gray)

                Spacer(modifier = Modifier.height(4.dp))

                Text("ETA: $etaText", color = Color.Gray)
                Text("Distance left: $distanceText", color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// 🔄 Rotation
fun getRotation(start: LatLng, end: LatLng): Float {
    val latDiff = end.latitude - start.latitude
    val lngDiff = end.longitude - start.longitude
    val angle = atan2(lngDiff, latDiff) * (180 / PI)
    return angle.toFloat()
}

// 📍 Snap
fun getClosestPoint(current: LatLng, route: List<LatLng>): LatLng {
    return route.minByOrNull {
        val dLat = it.latitude - current.latitude
        val dLng = it.longitude - current.longitude
        dLat * dLat + dLng * dLng
    } ?: current
}

// 🎨 Marker icon
fun bitmapFromResource(
    context: Context,
    resId: Int
): BitmapDescriptor {

    val drawable = ContextCompat.getDrawable(context, resId)
        ?: return BitmapDescriptorFactory.defaultMarker()

    // 🔥 BIGGER SIZE (FINAL)
    val width = 140
    val height = 140

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}