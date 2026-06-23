package com.example.havetime.data.repository

import android.content.Context
import com.example.havetime.domain.repository.GeocodingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.location.GeocoderNominatim
import java.util.Locale
import javax.inject.Inject
import android.location.Geocoder as AndroidGeocoder


class GeocodingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val osmGeocoder: GeocoderNominatim
): GeocodingRepository {
    private val androidGeocoder = android.location.Geocoder(context, Locale.getDefault())

    override suspend fun getAddressFromCoords(
        lat: Double,
        lon: Double
    ): String? = withContext(Dispatchers.IO){
        if (AndroidGeocoder.isPresent()) {
            try {
                @Suppress("DEPRECATION")
                val addresses = androidGeocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    return@withContext addresses[0].getAddressLine(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            val osmAddresses = osmGeocoder.getFromLocation(lat, lon, 1)
            if (!osmAddresses.isNullOrEmpty()) {
                val address = osmAddresses[0]
                return@withContext address.extras.getString("display_name") ?: address.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }
}