package nonozi.freefamilytracking

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "PreferenceDataStore")

    suspend fun readPeriod(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[PERIOD_KEY] ?: 0
        }
    }


    suspend fun savePeriod(period: Int) {
        Log.d("DataStoreManager", "Stockage dans le DataStore de $period")
        context.dataStore.edit { preferences ->
            preferences[PERIOD_KEY] = period
        }
    }

    suspend fun observePeriodChanges(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[PERIOD_KEY] ?: 0
        }
    }

    companion object {
        private val PERIOD_KEY = intPreferencesKey("period")
    }
}




