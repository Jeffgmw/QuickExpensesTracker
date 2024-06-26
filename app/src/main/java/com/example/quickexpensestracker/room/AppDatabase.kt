package com.example.quickexpensestracker.room

import android.content.Context
import androidx.room.*
import com.example.quickexpensestracker.utils.DateTypeConverter
import com.example.quickexpensestracker.model.Transaction

// Define the database with entities, version, and type converters.
@Database(entities = [Transaction::class],
    version = 1,
    exportSchema = false)
@TypeConverters(DateTypeConverter::class) // Use the custom type converter for date fields.
abstract class AppDatabase : RoomDatabase() { // Abstract class that extends RoomDatabase.
    abstract fun transactionDao() : TransactionDao // Abstract method to get the DAO.

    companion object { // Companion object to hold the singleton instance of the database.
        @Volatile
        private var INSTANCE: AppDatabase? = null // Volatile variable to ensure visibility of changes across threads.

        fun getDatabase(context: Context): AppDatabase { // Method to get the singleton instance of the database.
            // Check if the instance is already created, if not create a new one.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transactions_database"
                )
                    // Wipes and rebuilds the database instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance // Set the INSTANCE to the newly created database.
                // Return the newly created instance.
                instance
            }
        }
    }
}

