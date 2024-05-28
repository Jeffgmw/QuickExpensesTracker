package com.example.quickexpensestracker.repository

import com.example.quickexpensestracker.room.AppDatabase // Import the AppDatabase class.
import com.example.quickexpensestracker.model.Transaction // Import the Transaction model class.
import kotlinx.coroutines.flow.Flow // Import the Flow class from coroutines.

class TransactionRepository(database: AppDatabase) { // Define the TransactionRepository class.

    private val transactionDao = database.transactionDao() // Initialize the transaction DAO.

    fun getAllTransactions(isAsc: Boolean): Flow<List<Transaction>> { // Method to get all transactions.
        return transactionDao.getAll(isAsc) // Return a Flow of transaction list.
    }

    fun getTransactionById(transactionId: Int): Flow<Transaction> { // Method to get transaction by ID.
        return transactionDao.getById(transactionId) // Return a Flow of single transaction.
    }

    suspend fun insertTransaction(transaction: Transaction) { // Method to insert a transaction.
        transactionDao.insertAll(transaction) // Call DAO method to insert transaction.
    }

    suspend fun deleteTransaction(transactionId: Int) { // Method to delete a transaction.
        transactionDao.delete(transactionId) // Call DAO method to delete transaction.
    }

    suspend fun updateTransaction(transaction: Transaction) { // Method to update a transaction.
        transactionDao.update(transaction) // Call DAO method to update transaction.
    }

    fun searchTransactions(searchQuery: String, isAsc: Boolean): Flow<List<Transaction>> { // Method to search transactions.
        return transactionDao.searchDatabase(searchQuery, isAsc) // Return a Flow of searched transaction list.
    }
}

