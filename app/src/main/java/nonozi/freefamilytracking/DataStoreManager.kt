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
    suspend fun readPeriod(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[PERIOD_KEY] ?: 0

        }
    }


    suspend fun readName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[NAME_KEY]
        }
    }

    suspend fun readGroupName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[GROUP_NAME_KEY]
        }
    }

    suspend fun savePeriod(period: Int) {
        Log.d("DataStoreManager", "Stockage dans le DataStore de $period")
        context.dataStore.edit { preferences ->
            preferences[PERIOD_KEY] = period
        }
    }


    suspend fun saveName(name: String) {
        Log.d("DataStoreManager", "Enregistrement du nom $name dans le DataStore")
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
        }
    }

    suspend fun saveGroupName(groupName: String) {
        Log.d("DataStoreManager", "Enregistrement du nom du groupe $groupName dans le DataStore")
        context.dataStore.edit { preferences ->
            preferences[GROUP_NAME_KEY] = groupName
        }
    }






    suspend fun observePeriodChanges(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[PERIOD_KEY] ?: 0
        }
    }


    suspend fun saveConfiguration(period: Int, name: String, groupName: String) {
        Log.d("DataStoreManager", "Enregistrement des valeurs $period, $name et $groupName dans le DataStore")
        context.dataStore.edit { preferences ->
            preferences[PERIOD_KEY] = period
            preferences[NAME_KEY] = name
            preferences[GROUP_NAME_KEY] = groupName
        }
    }



    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PreferenceDataStore")
        private val PERIOD_KEY = intPreferencesKey("period")
        val NAME_KEY = stringPreferencesKey("name")
        val GROUP_NAME_KEY = stringPreferencesKey("groupName")

        @Volatile
        private var instance: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return instance ?: synchronized(this) {
                instance ?: DataStoreManager(context.applicationContext).also { instance = it }
            }
        }
    }





}




