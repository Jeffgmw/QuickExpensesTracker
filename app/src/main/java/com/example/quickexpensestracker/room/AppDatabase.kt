package com.example.quickexpensestracker.room

import android.content.Context
import androidx.room.*
<<<<<<< HEAD:app/src/main/java/com/example/quickexpensestracker/room/AppDatabase.kt
import com.example.quickexpensestracker.utils.DateTypeConverter
import com.example.quickexpensestracker.model.Transaction
=======
import com.example.quickexpensestracker.DateTypeConverter
import com.example.quickexpensestracker.database.Transaction.Transaction
import com.example.quickexpensestracker.database.Transaction.TransactionDao
>>>>>>> 1a2242ef8a87d4cea63d1ee776d61ab7f794da5d:app/src/main/java/com/example/quickexpensestracker/database/AppDatabase.kt


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