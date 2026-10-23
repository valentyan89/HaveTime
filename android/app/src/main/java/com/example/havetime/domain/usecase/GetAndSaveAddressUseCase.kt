package com.example.havetime.domain.usecase

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.repository.GeocodingRepository

class GetAndSaveAddressUseCase(
    private val repository: ActivityRepository,
    private val geocoding: GeocodingRepository
) {
    suspend operator fun invoke(id: Int): Boolean {
        val activity = repository.getActivityById(id) ?: return false

        val lat = activity.location?.latitude
        val lon = activity.location?.longitude

        if (lat != null && lon != null) {
            val fetchedAddress = geocoding.getAddressFromCoords(lat, lon)
            if (fetchedAddress != null) {
                return repository.updateGeocodedAddress(id, lat, lon, fetchedAddress)
            }
        }
        return false
    }
}
