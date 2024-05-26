package com.example.quickexpensestracker.database

import android.content.Context
import androidx.room.*
import com.example.quickexpensestracker.DateTypeConverter
import com.example.quickexpensestracker.database.Transaction.Transaction
import com.example.quickexpensestracker.database.Transaction.TransactionDao


@Database(entities = [Transaction::class], version = 3, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao() : TransactionDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transactions_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}