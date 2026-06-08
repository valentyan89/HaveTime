package com.example.havetime.data.model.activity

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val geocodedAddress: String? = null
)