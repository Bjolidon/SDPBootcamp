package com.github.sdp.tarjetakuna.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a pair of coordinates.
 * It is used to represent a location for a user.
 */
data class Coordinates(
    var latitude: Float,
    var longitude: Float
) {

    init {
        latitude = latitude.coerceAtMost(latitudeRange).coerceAtLeast(-latitudeRange)
        longitude = longitude.coerceAtMost(longitudeRange).coerceAtLeast(-longitudeRange)
    }

    /**
     * Calculates the distance in kilometers to another [Coordinates].
     */
    fun distanceKmTo(other: Coordinates): Float {
        // Haversine formula
        val earthRadius = 6371
        val latDistance = Math.toRadians((latitude - other.latitude).toDouble())
        val lngDistance = Math.toRadians((longitude - other.longitude).toDouble())
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(latitude.toDouble())) * cos(Math.toRadians(other.latitude.toDouble()))
                * sin(lngDistance / 2) * sin(lngDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }

    companion object {
        const val latitudeRange = 90.0f
        const val longitudeRange = 180.0f
    }
}
