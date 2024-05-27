package com.example.quickexpensestracker.room

import androidx.room.* // Import Room annotations and classes for database interaction.
import com.example.quickexpensestracker.model.Transaction // Import the Transaction data model class.
import kotlinx.coroutines.flow.Flow // Import Flow for reactive data handling.

@Dao // Annotation to mark this interface as a Data Access Object for Room.
interface TransactionDao { // Interface defining database operations for transactions.

    @Query("SELECT * from transactions ORDER BY " + // SQL query to get all transactions.
            "CASE WHEN :isAsc = 1 THEN date END ASC, " + // Order by date ascending if isAsc is true.
            "CASE WHEN :isAsc = 0 THEN date END DESC") // Order by date descending if isAsc is false.
    fun getAll(isAsc: Boolean): Flow<List<Transaction>> // Returns a Flow of a list of transactions.

    @Query("SELECT * from transactions WHERE label LIKE :searchQuery ORDER BY" + // SQL query to search transactions by label.
            " CASE WHEN :isAsc = 1 THEN date END ASC," + // Order by date ascending if isAsc is true.
            "CASE WHEN :isAsc = 0 THEN date END DESC") // Order by date descending if isAsc is false.
    fun searchDatabase(searchQuery: String, isAsc: Boolean): Flow<List<Transaction>> // Returns a Flow of a list of transactions filtered by search query.

    @Query("SELECT * from transactions WHERE id = :transactionId") // SQL query to get a transaction by its ID.
    fun getById(transactionId: Int): Flow<Transaction> // Returns a Flow of a single transaction.

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Annotation to mark this method for inserting a transaction. Ignore conflicts.
    suspend fun insertAll(transaction: Transaction) // Suspended function to insert a transaction.

    @Query("DELETE from transactions WHERE id = :transactionId") // SQL query to delete a transaction by its ID.
    suspend fun delete(transactionId: Int) // Suspended function to delete a transaction.

    @Update // Annotation to mark this method for updating a transaction.
    suspend fun update(transaction: Transaction) // Suspended function to update a transaction.

}

/*
Explanation of source of methods and their functionality:

1. getAll(isAsc: Boolean): This method is annotated with @Query and defines a SQL query to get all transactions from the database. It orders the transactions by date in ascending or descending order based on the value of the isAsc parameter. It returns a Flow, which allows reactive data handling and automatic updates when the data changes.

2. searchDatabase(searchQuery: String, isAsc: Boolean): This method is annotated with @Query and defines a SQL query to search transactions by their label. It orders the transactions by date in ascending or descending order based on the value of the isAsc parameter. It returns a Flow, which allows reactive data handling and automatic updates when the data changes.

3. getById(transactionId: Int): This method is annotated with @Query and defines a SQL query to get a transaction by its ID. It returns a Flow, which allows reactive data handling and automatic updates when the data changes.

4. insertAll(transaction: Transaction): This method is annotated with @Insert and is used to insert a transaction into the database. The onConflict parameter is set to OnConflictStrategy.IGNORE, which means that if there is a conflict (e.g., a transaction with the same ID already exists), the insert will be ignored. This is a suspended function, meaning it is designed to be called from a coroutine or another suspend function.

5. delete(transactionId: Int): This method is annotated with @Query and defines a SQL query to delete a transaction by its ID. This is a suspended function, meaning it is designed to be called from a coroutine or another suspend function.

6. update(transaction: Transaction): This method is annotated with @Update and is used to update an existing transaction in the database. This is a suspended function, meaning it is designed to be called from a coroutine or another suspend function.
*/
