package com.example.havetime.data.model.activity

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val geocodedAddress: String? = null
)