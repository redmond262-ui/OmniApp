package com.example.data.remote

import com.example.model.LatLon
import com.example.model.Store
import com.example.model.StoreType
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class GeoResult(val point: LatLon, val displayName: String)

data class RouteStepData(val instruction: String, val distanceMeters: Double, val subtext: String?)

data class RouteResult(
    val distanceKm: Double,
    val durationMinutes: Int,
    val geometry: List<LatLon>,
    val steps: List<RouteStepData>
)

/** Geocodificación (Nominatim), rutas (OSRM / routing.openstreetmap.de) y comercios (Overpass). Todo OpenStreetMap, sin API key. */
object GeoRepository {

    // ---------- Geocodificación ----------
    suspend fun geocode(query: String, near: LatLon?): GeoResult? {
        var url = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&countrycodes=ar&q=" + Http.enc(query)
        if (near != null) {
            // Sesgo hacia ~0.5° alrededor del usuario (no excluyente).
            val d = 0.5
            url += "&viewbox=" + "${near.lon - d},${near.lat + d},${near.lon + d},${near.lat - d}"
        }
        val arr = JSONArray(Http.get(url))
        if (arr.length() == 0) return null
        val o = arr.getJSONObject(0)
        return GeoResult(
            point = LatLon(o.getString("lat").toDouble(), o.getString("lon").toDouble()),
            displayName = o.optString("display_name", query)
        )
    }

    // ---------- Rutas ----------
    enum class Profile(val path: String) { CAR("routed-car"), FOOT("routed-foot") }

    suspend fun route(from: LatLon, to: LatLon, profile: Profile): RouteResult {
        val url = "https://routing.openstreetmap.de/${profile.path}/route/v1/driving/" +
            "${fmt(from.lon)},${fmt(from.lat)};${fmt(to.lon)},${fmt(to.lat)}" +
            "?overview=full&geometries=geojson&steps=true"
        val json = JSONObject(Http.get(url))
        if (json.optString("code") != "Ok") throw java.io.IOException("Sin ruta: ${json.optString("code")}")
        val r = json.getJSONArray("routes").getJSONObject(0)

        val coords = r.getJSONObject("geometry").getJSONArray("coordinates")
        val geometry = ArrayList<LatLon>(coords.length())
        for (i in 0 until coords.length()) {
            val c = coords.getJSONArray(i)
            geometry.add(LatLon(c.getDouble(1), c.getDouble(0)))
        }

        val steps = mutableListOf<RouteStepData>()
        val legs = r.getJSONArray("legs")
        for (l in 0 until legs.length()) {
            val st = legs.getJSONObject(l).getJSONArray("steps")
            for (i in 0 until st.length()) {
                val s = st.getJSONObject(i)
                val man = s.optJSONObject("maneuver")
                val street = s.optString("name", "")
                steps += RouteStepData(
                    instruction = spanishInstruction(man?.optString("type").orEmpty(), man?.optString("modifier").orEmpty()),
                    distanceMeters = s.optDouble("distance", 0.0),
                    subtext = street.takeIf { it.isNotBlank() }?.let { "por $it" }
                )
            }
        }
        return RouteResult(
            distanceKm = r.getDouble("distance") / 1000.0,
            durationMinutes = maxOf(1, Math.round(r.getDouble("duration") / 60.0).toInt()),
            geometry = geometry,
            steps = steps
        )
    }

    private fun fmt(v: Double) = String.format(java.util.Locale.US, "%.6f", v)

    private fun spanishInstruction(type: String, modifier: String): String {
        val dir = when (modifier) {
            "left" -> "a la izquierda"
            "right" -> "a la derecha"
            "slight left" -> "levemente a la izquierda"
            "slight right" -> "levemente a la derecha"
            "sharp left" -> "pronunciadamente a la izquierda"
            "sharp right" -> "pronunciadamente a la derecha"
            "uturn" -> "en U"
            else -> ""
        }
        return when (type) {
            "depart" -> "Salida"
            "arrive" -> "Llegada al destino"
            "turn", "end of road" -> if (dir.isNotEmpty()) "Gira $dir" else "Gira"
            "fork" -> if (dir.isNotEmpty()) "Toma la bifurcación $dir" else "Toma la bifurcación"
            "merge" -> "Incorpórate"
            "roundabout", "rotary", "roundabout turn" -> "Toma la rotonda"
            "new name", "continue" -> "Continúa"
            else -> "Continúa"
        }
    }

    // ---------- Comercios cercanos (Overpass) ----------
    suspend fun nearbyStores(center: LatLon, radiusMeters: Int): List<Store> {
        val query = """
            [out:json][timeout:20];
            (
              nwr["shop"~"supermarket|convenience|bakery|greengrocer|butcher|health_food"](around:$radiusMeters,${fmt(center.lat)},${fmt(center.lon)});
              nwr["amenity"="pharmacy"](around:$radiusMeters,${fmt(center.lat)},${fmt(center.lon)});
            );
            out center tags 80;
        """.trimIndent()
        val json = JSONObject(Http.postForm("https://overpass-api.de/api/interpreter", "data", query))
        val elements = json.optJSONArray("elements") ?: return emptyList()
        val result = mutableListOf<Store>()
        for (i in 0 until elements.length()) {
            val e = elements.getJSONObject(i)
            val tags = e.optJSONObject("tags") ?: continue
            val name = tags.optString("name", "").trim()
            if (name.isEmpty()) continue
            val lat = if (e.has("lat")) e.getDouble("lat") else e.optJSONObject("center")?.optDouble("lat")
            val lon = if (e.has("lon")) e.getDouble("lon") else e.optJSONObject("center")?.optDouble("lon")
            if (lat == null || lon == null || lat.isNaN() || lon.isNaN()) continue

            val type = when {
                tags.optString("amenity") == "pharmacy" -> StoreType.FARMACIA
                tags.optString("shop") == "supermarket" -> StoreType.SUPERMERCADO
                tags.optString("shop") == "bakery" -> StoreType.PANADERIA
                tags.optString("shop") == "health_food" -> StoreType.TIENDA_NATURAL
                tags.optString("shop") == "convenience" -> StoreType.KIOSCO
                else -> StoreType.ALMACEN
            }
            val hours = tags.optString("opening_hours", "")
            val is24 = hours == "24/7"
            val street = listOf(tags.optString("addr:street"), tags.optString("addr:housenumber"))
                .filter { it.isNotBlank() }.joinToString(" ")
            val dist = haversineMeters(center, LatLon(lat, lon)).toInt()
            val id = "osm_${e.optString("type")}_${e.optLong("id")}"
            result += Store(
                id = id,
                name = name,
                type = type,
                rating = 0f,
                reviewsCount = 0,
                distanceMeters = dist,
                address = street.ifBlank { "Dirección no informada en OpenStreetMap" },
                isOpen = is24,
                hoursText = if (hours.isNotBlank()) "Horario (OSM): $hours" else "Horario no informado",
                accentColorHex = 0xFF0D9488,
                photosCount = 0,
                hasVideoTour = false,
                videoTourDuration = "",
                description = "Dato público de OpenStreetMap. Este comercio todavía no cargó catálogo ni precios en OmniHub.",
                specialOfferText = "",
                minSpendDiscountThreshold = 0.0,
                minSpendDiscountPercent = 0,
                comboDeals = emptyList(),
                inventory = emptyList(),
                mapOffsetX = 0.15f + (abs(id.hashCode()) % 70) / 100f,
                mapOffsetY = 0.15f + (abs(id.hashCode() / 7) % 70) / 100f,
                is24Hours = is24,
                scheduleNotes = "Horario sin verificar",
                lat = lat,
                lon = lon
            )
        }
        return result.sortedBy { it.distanceMeters }.take(40)
    }

    fun haversineMeters(a: LatLon, b: LatLon): Double {
        val r = 6_371_000.0
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLon = Math.toRadians(b.lon - a.lon)
        val h = sin(dLat / 2).pow(2) + cos(Math.toRadians(a.lat)) * cos(Math.toRadians(b.lat)) * sin(dLon / 2).pow(2)
        return 2 * r * asin(sqrt(h))
    }
}
