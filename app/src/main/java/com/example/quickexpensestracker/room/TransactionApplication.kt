package com.example.quickexpensestracker.room

import android.app.Application // Import the Application class from the Android framework.

class TransactionApplication : Application() { // Class that extends the Android Application class.
    // Lazy initialization of the AppDatabase instance.
    // This ensures the database is only created when it is first accessed.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

}

