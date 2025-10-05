package ru.netology.nework.util

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ru.netology.nework.model.Coordinates

object MapUtils {

    fun addMarkerAndMoveCamera(
        map: GoogleMap,
        coordinates: Coordinates,
        title: String = "Местоположение",
        zoom: Float = 15f
    ) {
        val location = LatLng(coordinates.lat, coordinates.lng)
        map.addMarker(
            MarkerOptions()
                .position(location)
                .title(title)
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }

    fun setupMap(map: GoogleMap) {
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true
    }

    fun isValidCoordinates(coordinates: Coordinates?): Boolean {
        return coordinates != null && 
               coordinates.lat in -90.0..90.0 && 
               coordinates.lng in -180.0..180.0
    }
    fun formatCoordinates(coordinates: Coordinates): String {
        return String.format("%.6f, %.6f", coordinates.lat, coordinates.lng)
    }
}










