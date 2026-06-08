package com.example.havetime.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

class TokenManager(private val context: Context) {
    private companion object{
        val AUTH_TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    suspend fun saveToken(token: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    val tokenFlow: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }

    suspend fun clearToken() {
        context.tokenDataStore.edit {
            it.clear()
        }
    }
}