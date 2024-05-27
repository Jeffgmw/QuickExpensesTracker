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

/*
Explanation of source of methods and their functionality:

1. AppDatabase: This class is imported from the com.example.quickexpensestracker.room package and provides access to the database instance.

2. Transaction: This is the model class for the transaction entity.

3. Flow: This class is imported from kotlinx.coroutines.flow package and represents a stream of values that are asynchronously delivered.

4. transactionDao: This private property holds an instance of the TransactionDao interface obtained from the provided AppDatabase instance.

5. getAllTransactions(isAsc: Boolean): Flow<List<Transaction>>: This method retrieves all transactions from the database. It takes a boolean parameter isAsc to determine the sorting order of the transactions. It returns a Flow of List<Transaction> representing the asynchronous stream of transaction data.

6. getTransactionById(transactionId: Int): Flow<Transaction>: This method retrieves a specific transaction from the database based on its ID. It takes an integer parameter transactionId representing the ID of the transaction to be retrieved. It returns a Flow of Transaction representing the asynchronous stream of the transaction data.

7. insertTransaction(transaction: Transaction): This method inserts a new transaction into the database. It takes a Transaction object representing the transaction to be inserted as a parameter. It is a suspend function as it performs a database operation, which can be a long-running task, and is called from a coroutine.

8. deleteTransaction(transactionId: Int): This method deletes a transaction from the database based on its ID. It takes an integer parameter transactionId representing the ID of the transaction to be deleted. It is a suspend function as it performs a database operation, which can be a long-running task, and is called from a coroutine.

9. updateTransaction(transaction: Transaction): This method updates an existing transaction in the database. It takes a Transaction object representing the updated transaction as a parameter. It is a suspend function as it performs a database operation, which can be a long-running task, and is called from a coroutine.

10. searchTransactions(searchQuery: String, isAsc: Boolean): Flow<List<Transaction>>: This method searches for transactions in the database based on a search query and sorting order. It takes a string parameter searchQuery representing the search query and a boolean parameter isAsc representing the sorting order. It returns a Flow of List<Transaction> representing the asynchronous stream of searched transaction data.

The TransactionRepository class encapsulates the data access logic for transactions, providing methods to interact with the database and perform CRUD (Create, Read, Update, Delete) operations on transaction data.

 */