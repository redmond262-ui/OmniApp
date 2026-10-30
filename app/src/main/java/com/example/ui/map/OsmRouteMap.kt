package com.example.ui.map

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.LatLon
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File

/** Mapa de OpenStreetMap (osmdroid) con la ruta y marcadores. No requiere API key. */
@Composable
fun OsmRouteMap(
    route: List<LatLon>,
    origin: LatLon?,
    destination: LatLon?,
    markers: List<Pair<String, LatLon>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    remember {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            val base = File(context.cacheDir, "osmdroid")
            osmdroidBasePath = base
            osmdroidTileCache = File(base, "tiles")
        }
        true
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(14.0)
            // El mapa vive dentro de una lista con scroll: que los gestos sobre el mapa no se los lleve la lista.
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { map ->
            map.overlays.clear()

            if (route.size >= 2) {
                val line = Polyline().apply {
                    setPoints(route.map { GeoPoint(it.lat, it.lon) })
                    outlinePaint.color = android.graphics.Color.parseColor("#0D9488")
                    outlinePaint.strokeWidth = 12f
                }
                map.overlays.add(line)
            }

            fun addMarker(title: String, p: LatLon) {
                val m = Marker(map).apply {
                    position = GeoPoint(p.lat, p.lon)
                    this.title = title
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                map.overlays.add(m)
            }
            origin?.let { addMarker("Origen", it) }
            destination?.let { addMarker("Destino", it) }
            markers.forEach { (name, p) -> addMarker(name, p) }

            val all = buildList {
                addAll(route)
                origin?.let { add(it) }
                destination?.let { add(it) }
                if (isEmpty()) addAll(markers.map { it.second })
            }.map { GeoPoint(it.lat, it.lon) }

            map.post {
                if (all.size >= 2) {
                    val box = BoundingBox.fromGeoPoints(all)
                    if (box.latitudeSpan > 0.0001 || box.longitudeSpan > 0.0001) {
                        map.zoomToBoundingBox(box, false, 80)
                    } else {
                        map.controller.setZoom(16.0)
                        map.controller.setCenter(all.first())
                    }
                } else if (all.size == 1) {
                    map.controller.setZoom(16.0)
                    map.controller.setCenter(all.first())
                }
                map.invalidate()
            }
        }
    )
}
