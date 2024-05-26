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
    ListAdapter<Transaction, TransactionAdapter.TransactionHolder>(DiffCallback) {

    class TransactionHolder(private val binding: TransactionLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction, onItemClicked: (Transaction) -> Unit) {
            binding.labelT.text = transaction.label

            val context = binding.labelT.context
            if (transaction.amount >= 0) {
                binding.amountT.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                binding.amountT.setTextColor(ContextCompat.getColor(context, R.color.red))
            }

            binding.amountT.text = transaction.getFormattedAmount()
            binding.dateT.text = transaction.getFormattedDate()

            itemView.setOnClickListener {
                onItemClicked(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val binding = TransactionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onItemClicked)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }
}
