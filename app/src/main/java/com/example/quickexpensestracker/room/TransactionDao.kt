package com.example.quickexpensestracker.room

import androidx.room.*
import com.example.quickexpensestracker.model.Transaction
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

