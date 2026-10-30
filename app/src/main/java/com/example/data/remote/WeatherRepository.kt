package com.example.data.remote

import com.example.model.AlertSeverity
import com.example.model.AlertType
import com.example.model.LatLon
import com.example.model.MorningBriefing
import com.example.model.WeatherAlert
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WeatherSnapshot(
    val currentTemp: Double,
    val currentCode: Int,
    val todayMin: Double,
    val todayMax: Double,
    val todayCode: Int,
    val precipitationMm: Double,
    val gustMaxKmh: Double,
    val uvMax: Double
)

/** Clima real desde Open-Meteo (gratis, sin API key). https://open-meteo.com */
object WeatherRepository {

    suspend fun fetch(point: LatLon): WeatherSnapshot {
        val url = "https://api.open-meteo.com/v1/forecast" +
            "?latitude=" + String.format(Locale.US, "%.4f", point.lat) +
            "&longitude=" + String.format(Locale.US, "%.4f", point.lon) +
            "&current=temperature_2m,weather_code" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_gusts_10m_max,uv_index_max" +
            "&timezone=auto&forecast_days=1&wind_speed_unit=kmh"
        val json = JSONObject(Http.get(url))
        val current = json.getJSONObject("current")
        val daily = json.getJSONObject("daily")
        fun day(name: String): Double = daily.getJSONArray(name).optDouble(0, 0.0)
        return WeatherSnapshot(
            currentTemp = current.optDouble("temperature_2m", 0.0),
            currentCode = current.optInt("weather_code", 0),
            todayMin = day("temperature_2m_min"),
            todayMax = day("temperature_2m_max"),
            todayCode = daily.getJSONArray("weather_code").optInt(0, 0),
            precipitationMm = day("precipitation_sum"),
            gustMaxKmh = day("wind_gusts_10m_max"),
            uvMax = day("uv_index_max")
        )
    }

    /** Código WMO de Open-Meteo -> descripción en español. */
    fun describe(code: Int): String = when (code) {
        0 -> "Cielo despejado"
        1 -> "Mayormente despejado"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Niebla"
        in 51..57 -> "Llovizna"
        in 61..67 -> "Lluvia"
        in 71..77 -> "Nevadas"
        in 80..82 -> "Chubascos"
        85, 86 -> "Chubascos de nieve"
        95 -> "Tormenta eléctrica"
        96, 99 -> "Tormenta con granizo"
        else -> "Condiciones variables"
    }

    /**
     * Avisos AUTOMÁTICOS calculados a partir del pronóstico. NO son alertas oficiales del SMN:
     * por eso cada título lo aclara. Para alertas oficiales hay que consultar smn.gob.ar.
     */
    fun buildAlerts(s: WeatherSnapshot): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()
        val note = " (aviso automático por pronóstico, no oficial)"

        if (s.todayMax >= 35) {
            val sev = when {
                s.todayMax >= 40 -> AlertSeverity.ROJO
                s.todayMax >= 38 -> AlertSeverity.NARANJA
                else -> AlertSeverity.AMARILLO
            }
            alerts += WeatherAlert(
                id = "auto_heat", type = AlertType.CLIMA_CALOR, severity = sev,
                title = "Calor extremo$note",
                description = "Máxima prevista de ${Math.round(s.todayMax)}°C para hoy.",
                instructions = "Hidrátate, evita salir en el pico de calor y busca lugares con sombra o aire acondicionado.",
                validUntil = "Hoy"
            )
        }
        if (s.todayMin <= 0) {
            val sev = when {
                s.todayMin <= -5 -> AlertSeverity.ROJO
                s.todayMin <= -3 -> AlertSeverity.NARANJA
                else -> AlertSeverity.AMARILLO
            }
            alerts += WeatherAlert(
                id = "auto_cold", type = AlertType.CLIMA_FRIO, severity = sev,
                title = "Frío intenso$note",
                description = "Mínima prevista de ${Math.round(s.todayMin)}°C.",
                instructions = "Abrígate bien, cuida a personas mayores y mascotas, y ventila los ambientes con precaución.",
                validUntil = "Hoy"
            )
        }
        if (s.todayCode in 95..99 || s.currentCode in 95..99) {
            val hail = s.todayCode in 96..99 || s.currentCode in 96..99
            alerts += WeatherAlert(
                id = "auto_storm", type = AlertType.TORMENTA,
                severity = if (hail) AlertSeverity.NARANJA else AlertSeverity.AMARILLO,
                title = "Tormentas${if (hail) " con posible granizo" else ""}$note",
                description = "El pronóstico indica tormenta eléctrica en tu zona.",
                instructions = "Evita zonas abiertas y árboles, y no dejes objetos sueltos en balcones.",
                validUntil = "Hoy"
            )
        }
        if (s.gustMaxKmh >= 60) {
            val sev = when {
                s.gustMaxKmh >= 100 -> AlertSeverity.ROJO
                s.gustMaxKmh >= 80 -> AlertSeverity.NARANJA
                else -> AlertSeverity.AMARILLO
            }
            alerts += WeatherAlert(
                id = "auto_wind", type = AlertType.HURACAN_TORNADO, severity = sev,
                title = "Ráfagas fuertes$note",
                description = "Ráfagas de hasta ${Math.round(s.gustMaxKmh)} km/h previstas.",
                instructions = "Asegura objetos exteriores y evita transitar cerca de carteles y árboles.",
                validUntil = "Hoy"
            )
        }
        if (s.precipitationMm >= 30) {
            alerts += WeatherAlert(
                id = "auto_rain", type = AlertType.CRECIDA_RIO,
                severity = if (s.precipitationMm >= 50) AlertSeverity.NARANJA else AlertSeverity.AMARILLO,
                title = "Lluvias abundantes$note",
                description = "Se esperan unos ${Math.round(s.precipitationMm)} mm en el día.",
                instructions = "Evita zonas anegables y planifica tus traslados con anticipación.",
                validUntil = "Hoy"
            )
        }
        if (s.uvMax >= 8) {
            alerts += WeatherAlert(
                id = "auto_uv", type = AlertType.CLIMA_CALOR, severity = AlertSeverity.INFO,
                title = "Radiación UV alta$note",
                description = "Índice UV máximo previsto: ${Math.round(s.uvMax)}.",
                instructions = "Usa protector solar y evita la exposición directa al mediodía.",
                validUntil = "Hoy"
            )
        }
        return alerts.sortedByDescending { it.severity.ordinal }
    }

    fun buildBriefing(
        s: WeatherSnapshot,
        firstName: String,
        pendingTasks: Int,
        activeDeals: Int,
        quote: String
    ): MorningBriefing {
        val locale = Locale("es", "AR")
        val date = SimpleDateFormat("EEEE d 'de' MMMM", locale).format(Date())
            .replaceFirstChar { it.titlecase(locale) }
        return MorningBriefing(
            greeting = "¡Hola, $firstName! Estas son las condiciones de hoy en tu zona.",
            dateText = date,
            weatherSummary = "${describe(s.todayCode)} • Ahora ${Math.round(s.currentTemp)}°C" +
                if (s.precipitationMm >= 1) " • Lluvia: ${Math.round(s.precipitationMm)} mm" else "",
            temperatureRange = "Min ${Math.round(s.todayMin)}°C • Máx ${Math.round(s.todayMax)}°C",
            motivationalQuote = quote,
            activeDealsCount = activeDeals,
            pendingTasksCount = pendingTasks
        )
    }
}
