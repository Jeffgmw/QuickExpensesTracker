package com.example.quickexpensestracker.room

import android.content.Context // Import the Context class from the Android framework.
import androidx.room.* // Import Room persistence library classes.
import com.example.quickexpensestracker.utils.DateTypeConverter // Import the custom DateTypeConverter class.
import com.example.quickexpensestracker.model.Transaction // Import the Transaction model class.

// Define the database with entities, version, and type converters.
@Database(entities = [Transaction::class], version = 3, exportSchema = false)
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

/*
Explanation of source of methods and their functionality:

1. Context: This class is part of the Android framework and provides access to application-specific resources and classes, such as databases and preferences.

2. RoomDatabase: This is a class from the Room persistence library that serves as the main access point to the persisted data and handles the database configuration and version management.

3. @Database: This annotation defines the entities and version for the Room database. It also includes the type converter to handle custom data types.

4. @TypeConverters: This annotation specifies the custom type converters to be used by the Room database. In this case, it uses DateTypeConverter to handle date fields.

5. abstract fun transactionDao(): TransactionDao: This abstract method provides access to the DAO (Data Access Object) for the Transaction entity.

6. companion object: This is a singleton pattern implementation to ensure that only one instance of the database is created throughout the application.

7. @Volatile: This annotation ensures that changes to the INSTANCE variable are visible across all threads immediately.

8. synchronized: This keyword ensures that only one thread at a time can execute the block of code to create the database instance, preventing multiple instances from being created in a multi-threaded environment.

9. Room.databaseBuilder: This method is used to create and configure a Room database instance. It takes the application context, the database class, and the database name as parameters.

10. .fallbackToDestructiveMigration(): This method configures the database to be wiped and rebuilt if no migration is provided. This can be useful during development but should be handled carefully in production.

By using Room and these annotations, we create a robust and efficient way to handle database operations in Android, ensuring type safety and reducing boilerplate code.
*/
