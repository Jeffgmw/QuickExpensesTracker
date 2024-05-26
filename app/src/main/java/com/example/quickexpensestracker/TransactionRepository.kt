package com.example.quickexpensestracker

import com.example.quickexpensestracker.database.AppDatabase
import com.example.quickexpensestracker.database.Transaction.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(database: AppDatabase) {

    private val transactionDao = database.transactionDao()

    fun getAllTransactions(isAsc: Boolean): Flow<List<Transaction>> {
        return transactionDao.getAll(isAsc)
    }

    fun getTransactionById(transactionId: Int): Flow<Transaction> {
        return transactionDao.getById(transactionId)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertAll(transaction)
    }

    suspend fun deleteTransaction(transactionId: Int) {
        transactionDao.delete(transactionId)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    fun searchTransactions(searchQuery: String, isAsc: Boolean): Flow<List<Transaction>> {
        return transactionDao.searchDatabase(searchQuery, isAsc)
    }
}
