package com.example.quickexpensestracker.room

import androidx.room.*
import com.example.quickexpensestracker.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * from transactions ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN date END ASC, " +
            "CASE WHEN :isAsc = 0 THEN date END DESC")
    fun getAll(isAsc:Boolean): Flow<List<Transaction>>


    @Query("SELECT * from transactions WHERE label LIKE :searchQuery ORDER BY" +
            " CASE WHEN :isAsc = 1 THEN date END ASC," +
            "CASE WHEN :isAsc = 0 THEN date END DESC")
    fun searchDatabase(searchQuery: String, isAsc:Boolean): Flow<List<Transaction>>

    @Query("SELECT * from transactions WHERE id = :transactionId")
    fun getById(transactionId: Int): Flow<Transaction>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(transaction: Transaction)

    @Query("DELETE from transactions WHERE id = :transactionId")
    suspend fun delete(transactionId: Int)

    @Update
    suspend fun update(transaction: Transaction)

}