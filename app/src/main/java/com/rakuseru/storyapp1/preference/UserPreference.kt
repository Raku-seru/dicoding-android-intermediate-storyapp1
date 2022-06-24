package com.rakuseru.storyapp1.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginState(): Flow<Boolean> {
        return dataStore.data.map { pref ->
            pref[IS_LOGIN] ?: false
        }
    }

    suspend fun saveLoginState(isLogin: Boolean) {
        dataStore.edit { pref ->
            pref[IS_LOGIN] = isLogin
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { pref ->
            pref[TOKEN] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { pref ->
            pref[TOKEN] = token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { pref ->
            pref[NAME] ?: ""
        }
    }

    suspend fun saveName(name: String) {
        dataStore.edit { pref ->
            pref[NAME] = name
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val IS_LOGIN = booleanPreferencesKey("is_login")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")

    }
}