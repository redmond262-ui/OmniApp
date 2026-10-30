package com.example.model

enum class TravelMode(val label: String) {
    WALKING("A pie"),
    DRIVING("Auto / Remis"),
    TRANSIT("Transporte")
}

data class TripStep(
    val instruction: String,
    val distanceText: String,
    val subtext: String? = null
)

data class TripRoute(
    val originName: String,
    val destinationName: String,
    val distanceKm: Double,
    val walkingMinutes: Int,
    val drivingMinutes: Int,
    val transitMinutes: Int,
    val estimatedRemisCost: Double,
    val steps: List<TripStep>,
    val landmarksOnRoute: List<String>,
    // Datos de ruta real (OpenStreetMap). Vacíos si es solo una estimación.
    val geometry: List<LatLon> = emptyList(),
    val originPoint: LatLon? = null,
    val destinationPoint: LatLon? = null,
    val isRealRoute: Boolean = false
)
