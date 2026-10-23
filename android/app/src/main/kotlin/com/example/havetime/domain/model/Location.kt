package com.example.havetime.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val geocodedAddress: String? = null
)
