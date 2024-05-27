package com.example.quickexpensestracker.room

import android.app.Application // Import the Application class from the Android framework.

class TransactionApplication : Application() { // Class that extends the Android Application class.
    // Lazy initialization of the AppDatabase instance.
    // This ensures the database is only created when it is first accessed.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}

/*
Explanation of source of methods and their functionality:

1. Application: This class is part of the Android framework and represents the base class for maintaining global application state. It is a superclass of all application classes that want to use some global state.

2. TransactionApplication: This is a custom application class that extends the Android Application class. It is used to initialize the AppDatabase instance when the application starts.

3. database: This is a property of the TransactionApplication class. It uses lazy initialization to create an instance of AppDatabase. The lazy delegate ensures that the database is only created the first time it is accessed, improving efficiency.

4. AppDatabase.getDatabase(this): This is a call to a static method in the AppDatabase class that returns a singleton instance of the database. It takes the application context as a parameter to ensure the database is properly initialized.

By using a custom Application class and lazy initialization, we ensure that the database instance is readily available throughout the application without unnecessary early initialization, thus optimizing resource usage.
*/
