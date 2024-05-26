package com.example.quickexpensestracker

import android.app.Application
import com.example.quickexpensestracker.database.AppDatabase

class TransactionApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
