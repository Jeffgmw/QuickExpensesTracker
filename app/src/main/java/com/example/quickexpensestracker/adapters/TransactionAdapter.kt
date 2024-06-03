package com.example.quickexpensestracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickexpensestracker.model.Transaction
import com.example.quickexpensestracker.model.getFormattedAmount
import com.example.quickexpensestracker.model.getFormattedDate
import com.example.quickexpensetracker.R
import com.example.quickexpensetracker.databinding.TransactionLayoutBinding

class TransactionAdapter(private val onItemClicked: (Transaction) -> Unit) :
    ListAdapter<Transaction, TransactionAdapter.TransactionHolder>(DiffCallback) { // Define TransactionAdapter class that extends ListAdapter with Transaction as item type.

    class TransactionHolder(private val binding: TransactionLayoutBinding) : RecyclerView.ViewHolder(binding.root) { // Define TransactionHolder class as a ViewHolder for holding views of individual items.

        fun bind(transaction: Transaction, onItemClicked: (Transaction) -> Unit) { // Define bind function to bind transaction data to views.
            binding.labelT.text = transaction.label // Set transaction label text.

            val context = binding.labelT.context // Get the context of the label text view.
            if (transaction.amount >= 0) { // Check if transaction amount is positive.
                binding.amountT.setTextColor(ContextCompat.getColor(context, R.color.green)) // Set text color to green for positive amount.
            } else {
                binding.amountT.setTextColor(ContextCompat.getColor(context, R.color.red)) // Set text color to red for negative amount.
            }

            binding.amountT.text = transaction.getFormattedAmount() // Set formatted transaction amount text.
            binding.dateT.text = transaction.getFormattedDate() // Set formatted transaction date text.

            itemView.setOnClickListener { // Set click listener for the item view.
                onItemClicked(transaction) // Invoke the onItemClick callback with the clicked transaction.
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder { // Override onCreateViewHolder method to create ViewHolder instances.
        val binding = TransactionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false) // Inflate the layout XML file into a binding object.
        return TransactionHolder(binding) // Return a new TransactionHolder instance with the inflated binding object.
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) { // Override onBindViewHolder method to bind data to ViewHolder.
        val current = getItem(position) // Get the transaction item at the specified position.
        holder.bind(current, onItemClicked) // Bind the transaction data to the ViewHolder.
    }

    companion object { // Define a companion object.
        private val DiffCallback = object : DiffUtil.ItemCallback<Transaction>() { // Define DiffCallback object for calculating differences between old and new items.
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean { // Override areItemsTheSame method to check if items have the same identity.
                return oldItem.id == newItem.id // Compare the IDs of old and new transactions.
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean { // Override areContentsTheSame method to check if item contents are the same.
                return oldItem == newItem // Compare the contents of old and new transactions.
            }
        }
    }
}

