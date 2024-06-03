package com.example.quickexpensestracker.model

import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.*

@Entity(tableName = "transactions") // Define Entity annotation with table name "transactions".
data class Transaction( // Define Transaction data class for representing transaction entity.
    @PrimaryKey(autoGenerate = true) val id: Int, // Primary key auto-generated integer ID.
    val label: String, // Label for the transaction.
    val amount: Double, // Amount of the transaction.
    val description: String, // Description of the transaction.
    val date: Date // Date of the transaction.
)

fun Transaction.getFormattedDate(): String = // Define extension function to format transaction date.
    SimpleDateFormat("EEEE, dd MMM yyyy").format(date) // Format the date using SimpleDateFormat.

fun Transaction.getFormattedAmount(): String = // Define extension function to format transaction amount.
    NumberFormat.getCurrencyInstance().format(amount) // Format the amount using NumberFormat.

