package com.example.quickexpensestracker.data

import android.content.Context // Import Context class for accessing Android application-specific resources.
import androidx.datastore.core.DataStore // Import DataStore class for storing and managing data.
import androidx.datastore.preferences.core.Preferences // Import Preferences class for accessing preferences data.
import androidx.datastore.preferences.core.booleanPreferencesKey // Import booleanPreferencesKey function for defining boolean preferences keys.
import androidx.datastore.preferences.core.edit // Import edit extension function for editing preferences.
import androidx.datastore.preferences.core.emptyPreferences // Import emptyPreferences function for creating an empty preferences instance.
import androidx.datastore.preferences.preferencesDataStore // Import preferencesDataStore delegate for creating DataStore instances.
import kotlinx.coroutines.flow.Flow // Import Flow interface for representing a flow of values.
import kotlinx.coroutines.flow.catch // Import catch operator for handling exceptions in flows.
import kotlinx.coroutines.flow.map // Import map operator for transforming values in flows.
import java.io.IOException // Import IOException class for handling I/O exceptions.

private const val LAYOUT_PREFERENCES_NAME = "layout_preferences" // Define the name of the preferences data store.

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCES_NAME
)

class SettingsDataStore(context: Context) { // Define SettingsDataStore class responsible for managing settings data.

    private val IS_ASC = booleanPreferencesKey("is_asc") // Define a boolean preferences key for sorting order.

    // Write to the Preferences DataStore
    suspend fun saveLayoutToPreferencesStore(isAsc: Boolean, context: Context) { // Define a suspend function to save layout preferences to the data store.
        context.dataStore.edit { preferences -> // Access the DataStore instance and start editing preferences.
            preferences[IS_ASC] = isAsc // Set the value of IS_ASC preference to the provided isAsc parameter.
        }
    }

    // Read from the Preferences DataStore
    val preferenceFlow: Flow<Boolean> = context.dataStore.data // Define a flow of boolean values representing layout preferences.
        .catch { // Use catch operator to handle exceptions in the flow.
            if (it is IOException) { // Check if the exception is an IOException.
                it.printStackTrace() // Print the stack trace of the exception.
                emit(emptyPreferences()) // Emit an empty preferences instance.
            } else {
                throw it // Throw the exception if it is not an IOException.
            }
        }
        .map { preferences -> // Use map operator to transform preferences data.
            // On the first run of the app, we will use LinearLayoutManager by default
            preferences[IS_ASC] ?: false // Return the value of IS_ASC preference, defaulting to false if it's null.
        }
}


