package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import com.example.model.LatLon
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/** Ubicación del dispositivo usando solo el LocationManager de Android (sin Google Play Services). */
object LocationProvider {

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun lastKnown(context: Context): LatLon? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        var best: Location? = null
        for (provider in lm.getProviders(true)) {
            val loc = try {
                lm.getLastKnownLocation(provider)
            } catch (e: SecurityException) {
                null
            } ?: continue
            if (best == null || loc.accuracy < best!!.accuracy) best = loc
        }
        return best?.let { LatLon(it.latitude, it.longitude) }
    }

    /** Última ubicación conocida o, si no hay, una lectura nueva (hasta 8 s). Null si no hay permiso o señal. */
    @SuppressLint("MissingPermission")
    suspend fun get(context: Context): LatLon? {
        if (!hasPermission(context)) return null
        lastKnown(context)?.let { return it }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return null

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val provider = when {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return null
        }
        return withTimeoutOrNull(8_000) {
            suspendCancellableCoroutine<LatLon?> { cont ->
                val signal = CancellationSignal()
                cont.invokeOnCancellation { signal.cancel() }
                try {
                    lm.getCurrentLocation(provider, signal, context.mainExecutor) { loc ->
                        if (cont.isActive) cont.resume(loc?.let { LatLon(it.latitude, it.longitude) })
                    }
                } catch (e: SecurityException) {
                    if (cont.isActive) cont.resume(null)
                }
            }
        }
    }
}
