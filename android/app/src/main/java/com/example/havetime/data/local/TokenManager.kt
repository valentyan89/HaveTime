package com.example.havetime.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Singleton
class TokenManager @Inject constructor(@Named("auth_store") private val dataStore: DataStore<Preferences>) {
    private companion object{
        val AUTH_TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }

    suspend fun clearToken() {
        dataStore.edit {
            it.clear()
        }
    }
}