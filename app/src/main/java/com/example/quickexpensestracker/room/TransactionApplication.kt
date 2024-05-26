package com.example.quickexpensestracker.room

import android.app.Application

class TransactionApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
