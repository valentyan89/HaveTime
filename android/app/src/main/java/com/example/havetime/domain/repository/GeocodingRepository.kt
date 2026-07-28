package com.example.havetime.domain.repository

interface GeocodingRepository {
    suspend fun getAddressFromCoords(lat: Double, lon: Double): String?
}