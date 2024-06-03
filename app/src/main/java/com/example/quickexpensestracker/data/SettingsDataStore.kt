package com.example.quickexpensestracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

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


